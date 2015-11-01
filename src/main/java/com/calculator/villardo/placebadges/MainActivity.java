package com.calculator.villardo.placebadges;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    ListView listView;
    MyListAdapter myListAdapter;
    private final static int REQUEST_CODE = 0;
    private static final String FILE_NAME = "PlaceBadgeMainActivityData.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        RelativeLayout footer = (RelativeLayout) getLayoutInflater().inflate(R.layout.list_footer_view, null);
        listView = (ListView) findViewById(R.id.listView);
        myListAdapter = new MyListAdapter(getApplicationContext());
        listView.addFooterView(footer);
        Button footerButton = (Button) footer.findViewById(R.id.footerButton);
        footerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               startNewActivityForResult();

            }
        });

        listView.setAdapter(myListAdapter);

        // Only load if needed
        if (myListAdapter.getCount() == 0) {
           /* Toast.makeText(getApplicationContext(),
                    "myListAdapter.getCount(): " + myListAdapter.getCount(), Toast.LENGTH_LONG).show();*/
            myListAdapter.clear();
            loadItems();
        }

    }

    private void startNewActivityForResult(){
        Intent intent = new Intent(MainActivity.this, LongitudeLatitude.class);

        // Launch the Activity using the intent
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            myListAdapter.clear();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                /*Toast.makeText(getApplicationContext(),
                        data.getStringExtra("Country"), Toast.LENGTH_LONG).show();*/
                String country = data.getStringExtra("Country");
                String place = data.getStringExtra("Place");
                String code = data.getStringExtra("Code");

                PlaceBadgeItem item = new PlaceBadgeItem(country, place, code);

                myListAdapter.add(item);
            }
        }
    }

    // CUSTOM LISTADAPTER
    public class MyListAdapter extends BaseAdapter {

        private final List<PlaceBadgeItem> placeBadgeItems = new ArrayList<PlaceBadgeItem>();
        private final Context mContext;
        private LayoutInflater mInflater;

        MyListAdapter(Context context) {

            this.mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void clear() {
            placeBadgeItems.clear();
            File file = new File(FILE_NAME);
            file.delete();
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {

            return placeBadgeItems.size();
        }

        @Override
        public Object getItem(int position) {

            return placeBadgeItems.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        public void writeItems(PrintWriter writer) {
            for(int i = 0; i < placeBadgeItems.size(); i ++) {
                writer.println(placeBadgeItems.get(i).getCountry());
                writer.println(placeBadgeItems.get(i).getPlace());
                writer.println(placeBadgeItems.get(i).getCode());
            }
        }

        // add PlaceBadge item to a list to be used in inflating the view in getView()
        // this add method is called from OnActivityResult from MainActivity
        public void add(PlaceBadgeItem item) {
            Boolean itemNotExist = true;
            for(int i = 0; i < placeBadgeItems.size(); i++){
                    if(compareBadgeExist(placeBadgeItems.get(i), item)){
                        //Toast.makeText(mContext, placeBadgeItems.get(i).getPlace() + " " + item.getPlace(), Toast.LENGTH_LONG).show();
                        itemNotExist = false;
                        //Toast.makeText(mContext, "EXIST TRIGGERED", Toast.LENGTH_LONG).show();
                    }
            }

            if(itemNotExist) {
                placeBadgeItems.add(item);
                notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, "That badge has already been added", Toast.LENGTH_LONG).show();
            }
        }

        public boolean compareBadgeExist(PlaceBadgeItem item1, PlaceBadgeItem item2){

            return (item1.getPlace().length() != 0 && item2.getPlace().length() != 0 &&
                    !item1.getPlace().equals(null) && !item2.getPlace().equals(null) &&
                    item1.getPlace().equals(item2.getPlace()));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PlaceBadgeItem item = (PlaceBadgeItem) getItem(position);

            RelativeLayout itemLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(
                    R.layout.place_badge_item, parent, false);

            TextView placeTextView = (TextView) itemLayout.findViewById(R.id.place_textview);
            placeTextView.setText(item.getPlace());


            TextView countryTextView = (TextView) itemLayout.findViewById(R.id.country_textview);
            countryTextView.setText(item.getCountry());


            // uses asynctask class to dl image
            new DownloadImageTask((ImageView) itemLayout.findViewById
                    (R.id.badge_imageview)).execute(item.getUrl());
           /* // change size of image
            ImageView imageView = (ImageView) itemLayout.findViewById
                    (R.id.badge_imageview);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params.height = 197;
            params.width = 398;
            imageView.setLayoutParams(params);*/

            return itemLayout;
        }





    }
    /*class to download thumbnail urls in background */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    // Load stored ToDoItems
    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String country = null;
            String place = null;
            String code = null;


            while (null != (country = reader.readLine())) {
                place = reader.readLine();
                code = reader.readLine();

                PlaceBadgeItem item = new PlaceBadgeItem(country, place, code);
                myListAdapter.add(item);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Save ToDoItems to file
    private void saveItems() {
        PrintWriter writer = null;
        try {
            File file = new File(FILE_NAME);
            file.delete();
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < myListAdapter.getCount(); idx++) {

               myListAdapter.writeItems(writer);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //saveItems();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveItems();
    }
}
