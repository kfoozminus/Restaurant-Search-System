package com.example.root.restaurant;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ResInfo extends AppCompatActivity {

    private static int trade_license_no = 0;
    TextView location, contactno, rating, opening, delivery;
    String logged, tradeString;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    String myJSON;
    JSONArray items = null;

    private static final String TAG = "jennysMsg";
    private static final String TAG_RESULTS = "result";
    private static final String TAG_IMAGE = "IMAGE";
    private static final String TAG_PRICE = "PRICE";
    private static final String TAG_RATING = "RATING";

    String openingS, ratingS, deliveryS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_info);

        Bundle itemData = getIntent().getExtras();
        if(itemData == null) {
            return ;
        }

        logged = itemData.getString("logged");
        tradeString = itemData.getString("tradeString");

        //location = (TextView) findViewById(R.id.location);
        //contactno = (TextView) findViewById(R.id.contactno);
        rating = (TextView) findViewById(R.id.rating);
        opening = (TextView) findViewById(R.id.opening);
        delivery = (TextView) findViewById(R.id.delivery);

        new resInfoTask().execute(tradeString);
    }

    protected void show() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            items = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<items.length();i++){

                JSONObject c = items.getJSONObject(i);
                openingS = "Opening Hours : " + c.getString("START") + " - " + c.getString("END");
                opening.setText(openingS);
                ratingS = "Rating : " + c.getString("RATING");
                rating.setText(ratingS);
                Log.i(TAG, ratingS);
                deliveryS = "Delivery Fee : " + c.getString("DELIVERY_FEE");
                delivery.setText(deliveryS);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class resInfoTask extends AsyncTask<String, String, String > {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("http://10.0.3.2/resInfo1.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }


            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("tradeString", params[0]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }



        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            myJSON = result;
            Log.i(TAG, result);
            show();
        }
    }
}
