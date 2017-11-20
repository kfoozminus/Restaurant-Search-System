package com.example.root.restaurant;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowEntireDatabase extends AppCompatActivity {

    private static final String TAG = "jennyMsg";

    String myJSON;
    JSONArray restaurants = null;
    ArrayList<HashMap<String, String>> restaurantList;
    ListView resList;
    TextView tv;

    private static final String TAG_RESULTS="result";
    private static final String TAG_TRADE_LICENSE_NO = "TRADE_LICENSE_NO";
    private static final String TAG_NAME = "NAME";
    private static final String TAG_ADDRESS = "ADDRESS";
    String logged = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_entire_database);

        Log.i(TAG, "On Create");

        Bundle locData = getIntent().getExtras();
        if(locData == null) {
            return ;
        }

        logged = locData.getString("logged");

        restaurantList = new ArrayList<>();
        resList = (ListView) findViewById(R.id.entireList);

        new JSONTask().execute("http://10.0.3.2/selectRes.php");


    }

    protected void show() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            restaurants = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<restaurants.length();i++){

                JSONObject c = restaurants.getJSONObject(i);
                String trade_license_no = c.getString(TAG_TRADE_LICENSE_NO);
                String name = c.getString(TAG_NAME);
                String address = c.getString(TAG_ADDRESS);

                HashMap<String,String> rests = new HashMap<>();

                rests.put(TAG_TRADE_LICENSE_NO,trade_license_no);
                rests.put(TAG_NAME,name);
                rests.put(TAG_ADDRESS,address);

                restaurantList.add(rests);
            }

            ListAdapter adapter = new SimpleAdapter(ShowEntireDatabase.this, restaurantList,R.layout.res_custom_list,
                    new String[]{TAG_NAME,TAG_ADDRESS},
                    new int[]{R.id.resName, R.id.resAddress});
            resList.setAdapter(adapter);

            resList.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent i = new Intent(ShowEntireDatabase.this, ItemsList.class);

                            JSONObject c = null;
                            String trade_license_no = null;
                            String pay = null;

                            try {
                                c = restaurants.getJSONObject(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                trade_license_no = c.getString(TAG_TRADE_LICENSE_NO);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            i.putExtra("TRADE_LICENSE_NO", trade_license_no);

                            startActivity(i);
                        }
                    }
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class JSONTask extends AsyncTask<String, String, String > {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) connection.disconnect();
                try {
                    if(reader != null) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            myJSON = result;
            show();
        }
    }
}

