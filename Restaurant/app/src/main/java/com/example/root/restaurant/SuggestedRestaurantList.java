package com.example.root.restaurant;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import android.net.Uri;
import android.content.SharedPreferences;
import android.app.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SuggestedRestaurantList extends AppCompatActivity {

    String myJSON;
    JSONArray items = null;
    ArrayList<HashMap<String, String>> itemList;
    ListView itList;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private static final String TAG = "jennysMsg";
    private static final String TAG_RESULTS="result";
    private static final String TAG_TRADE_LICENSE_NO = "TRADE_LICENSE_NO";
    private static final String TAG_NAME = "NAME";
    private static final String TAG_ADDRESS = "ADDRESS";
    String logged = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_restaurant_list);

        Bundle locData = getIntent().getExtras();
        if(locData == null) {
            return ;
        }

        String latitude = locData.getString("latitude");
        String longitude = locData.getString("longitude");
        logged = locData.getString("logged");
        itemList = new ArrayList<>();
        itList = (ListView) findViewById(R.id.itList);

        Log.i(TAG, "Suggested Restaurant" + latitude + longitude);

        new sugResTask().execute(latitude, longitude);
    }

    protected void show() {
        try {
            Log.i(TAG, "show");
            JSONObject jsonObj = new JSONObject(myJSON);
            items = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<items.length();i++){

                JSONObject c = items.getJSONObject(i);
                String name = c.getString(TAG_NAME);
                String address = c.getString(TAG_ADDRESS);

                HashMap<String,String> itlists = new HashMap<>();

                itlists.put(TAG_NAME,name);
                itlists.put(TAG_ADDRESS,address);

                itemList.add(itlists);
            }

            Log.i(TAG, "after loop");

            ListAdapter adapter = new SimpleAdapter(SuggestedRestaurantList.this, itemList,R.layout.res_custom_list,
                    new String[]{TAG_NAME,TAG_ADDRESS},
                    new int[]{R.id.resName, R.id.resAddress});
            itList.setAdapter(adapter);

            Log.i(TAG, "after adapter");

            itList.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent i = new Intent(SuggestedRestaurantList.this, ItemsList.class);

                            Log.i(TAG, "item click");

                            JSONObject c = null;
                            String trade_license_no = null;
                            String pay = null;

                            try {
                                c = items.getJSONObject(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                trade_license_no = c.getString(TAG_TRADE_LICENSE_NO);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            i.putExtra("TRADE_LICENSE_NO", trade_license_no);
                            i.putExtra("logged", logged);

                            startActivity(i);
                        }
                    }
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class sugResTask extends AsyncTask<String, String, String > {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("http://10.0.3.2/suggestedRes.php");

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
                        .appendQueryParameter("latitude", params[0])
                        .appendQueryParameter("longitude", params[1]);
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
