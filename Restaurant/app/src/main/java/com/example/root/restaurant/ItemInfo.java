package com.example.root.restaurant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ItemInfo extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    String myJSON;
    JSONArray items = null;

    ImageView itemImage;
    private static final String TAG = "jennysMsg";
    private static final String TAG_RESULTS = "result";
    private static final String TAG_IMAGE = "IMAGE";
    private static final String TAG_PRICE = "PRICE";
    private static final String TAG_RATING = "RATING";
    TextView itemPrice, itemRating;
    private Integer cnt = 0;
    EditText quanEdit;
    String tradeString;
    String itemString;
    String itemRatingS, itemPriceS, imageLink;
    String logged = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        Bundle itemData = getIntent().getExtras();
        if(itemData == null) {
            return ;
        }

        logged = itemData.getString("logged");

        tradeString = itemData.getString("TRADE_LICENSE_NO");
        itemString = itemData.getString("ITEM_NAME");
        quanEdit = (EditText) findViewById(R.id.quanEdit);
        itemPrice = (TextView) findViewById(R.id.itemPrice);
        itemRating = (TextView) findViewById(R.id.itemRating);

        cnt = 0;

        new itemInfoTask().execute(tradeString, itemString);

        itemImage = (ImageView) findViewById(R.id.itemImage);
    }

    public void onPlus(View v) {

        cnt ++;
        quanEdit.setText(String.valueOf(cnt), TextView.BufferType.EDITABLE);
    }
    public void onMinus(View v) {

        if(cnt > 0) {
            cnt --;
            quanEdit.setText(String.valueOf(cnt), TextView.BufferType.EDITABLE);
        }
    }

    public void addToBasket(View v) {
        Intent i = new Intent();
        i.putExtra("quanItem", String.valueOf(cnt));
        i.putExtra("itemName", itemString);
        i.putExtra("itemPrice", itemPriceS);
        setResult(RESULT_OK, i);
        finish();
    }

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
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    protected void show() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            items = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<items.length();i++){

                JSONObject c = items.getJSONObject(i);
                imageLink = "http://10.0.3.2/images/" + c.getString(TAG_IMAGE);
                itemPriceS = c.getString(TAG_PRICE);
                itemRatingS = c.getString(TAG_RATING);
                itemPrice.setText("Price : " + itemPriceS + " tk");
                itemRating.setText("Rating : " + itemRatingS);
            }
            new DownloadImageTask(itemImage).execute(imageLink);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class itemInfoTask extends AsyncTask<String, String, String > {

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
                url = new URL("http://10.0.3.2/itemInfo.php");

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
                        .appendQueryParameter("trade_license_no", params[0])
                        .appendQueryParameter("itemName", params[1]);
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
            show();
        }
    }
}
