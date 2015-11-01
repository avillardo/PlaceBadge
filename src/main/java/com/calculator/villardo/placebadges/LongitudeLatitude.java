package com.calculator.villardo.placebadges;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class LongitudeLatitude extends Activity {
    EditText latEditText, longEditText;
    Button setButton;
    private String latitude, longitude, place, country;
    private static final String ns = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_longitude_latitude);

        final EditText latEditText = (EditText) findViewById(R.id.latitude_edit_text);
        final EditText longEditText = (EditText) findViewById(R.id.longitude_edit_text);

        Button setButton = (Button) findViewById(R.id.set_location_button);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmpty(latEditText) && !isEmpty(longEditText)) {
                    Float latIntCheck = Float.parseFloat(latEditText.getText().toString());
                    Float longitudeIntCheck = Float.parseFloat(longEditText.getText().toString());
                    if (latIntCheck > 90 || latIntCheck < (-90) || longitudeIntCheck > 180 ||
                            longitudeIntCheck < (-180)) {
                        if (latIntCheck > 90 || latIntCheck < (-90))
                            Toast.makeText(getApplicationContext(),
                                    "Please enter a valid latitude value", Toast.LENGTH_LONG).show();
                        if (longitudeIntCheck > 180 || longitudeIntCheck < (-180))
                            Toast.makeText(getApplicationContext(),
                                    "Please enter a valid longitude value", Toast.LENGTH_LONG).show();
                    } else {
                        latitude = latEditText.getText().toString();
                        longitude = longEditText.getText().toString();
                        String link = "http://www.geonames.org/findNearbyPlaceName?username=" +
                                "mercium&style=full&lat=" + latitude + "&lng=" + longitude;
                        new LinkCheckerTask().execute(link);
                    }
                } else {

                    Toast.makeText(getApplicationContext(),
                            "Please enter values for both latitude and longitude", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    private class LinkCheckerTask extends AsyncTask<String, Void, ArrayList<String>> {


        private final ProgressDialog dialog = new ProgressDialog(LongitudeLatitude.this);

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            HttpURLConnection httpUrlConnection = null;
            ArrayList<String> result = new ArrayList<>();

            try {
                /*httpUrlConnection = (HttpURLConnection) new URL("http://www.geonames.org/findNearbyPlaceName?username=mercium&style=full&lat=60&lng=0")
                        .openConnection();*/
                httpUrlConnection = (HttpURLConnection) new URL(params[0])
                        .openConnection();
                InputStream in = httpUrlConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();


                NodeList nodeList = docEle.getElementsByTagName("geoname");
                if (nodeList != null && nodeList.getLength() > 0) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element entry = (Element) nodeList.item(i);
                        Element countryEle = (Element) entry.getElementsByTagName("countryName").item(0);
                        String country = countryEle.getFirstChild().getNodeValue();
                        result.add(country);

                        Element placeEle = (Element) entry.getElementsByTagName("toponymName").item(0);
                        String place = placeEle.getFirstChild().getNodeValue();
                        result.add(place);

                        Element codeEle = (Element) entry.getElementsByTagName("countryCode").item(0);
                        String code = codeEle.getFirstChild().getNodeValue();
                        result.add(code);
                    }
                }

                return result;
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait. Checking location...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            Intent data = new Intent();

            if (result == null) {
                Toast.makeText(getApplicationContext(), "Invalid Latitude or Longitude", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } else if (result.size() == 0) {
                Toast.makeText(getApplicationContext(), "No Named Country at this location", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } else {
                data.putExtra("Country", result.get(0));
                data.putExtra("Place", result.get(1));
                data.putExtra("Code", result.get(2));
                setResult(LongitudeLatitude.RESULT_OK, data);
                dialog.dismiss();
                finish();
            }


        }
    }


}
