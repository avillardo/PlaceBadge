package com.calculator.villardo.placebadges;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mercium on 10/27/15.
 */
public class MyListAdapter extends BaseAdapter {

    private final List<PlaceBadgeItem> placeBadgeItems = new ArrayList<PlaceBadgeItem>();
    private final Context mContext;
    private LayoutInflater mInflater;

    MyListAdapter(Context context) {

        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    // add PlaceBadge item to a list to be used in inflating the view in getView()
    // this add method is called from OnActivityResult from MainActivity
    public void add(PlaceBadgeItem item) {
        placeBadgeItems.add(item);
        notifyDataSetChanged();
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



        new DownloadImageTask((ImageView) itemLayout.findViewById(R.id.badge_imageview)).execute(item.getUrl());

        return itemLayout;
    }

    /**class to download thumbnail urls in background */
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



}
