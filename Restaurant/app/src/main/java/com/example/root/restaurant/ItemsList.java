package com.example.root.restaurant;

import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

public class ItemsList extends AppCompatActivity {

    String myJSON;
    JSONArray items = null;
    ArrayList<HashMap<String, String>> itemList;
    ListView itList;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    String tradeString = null;
    TextView test;

    private static final String TAG_RESULTS="result";
    private static final String TAG_NAME = "NAME";
    private static final String TAG = "jennysMsg";
    String logged = "0";

    private Integer cnt;
    private Integer totalPrice;
    Vector<String> vItemName, vItemQuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        Bundle resData = getIntent().getExtras();
        if(resData == null) {
            return ;
        }

        logged = resData.getString("logged");

        cnt = totalPrice = 0;
        vItemName = new Vector<String>(2);
        vItemQuan = new Vector<String>(2);

        itemList = new ArrayList<>();
        itList = (ListView) findViewById(R.id.itList);

        tradeString = resData.getString("TRADE_LICENSE_NO");

        new itemTask().execute(tradeString);
    }

    public void onResInfo(View v) {
        Intent i = new Intent(ItemsList.this, ResInfo.class);
        i.putExtra("logged", logged);
        i.putExtra("tradeString", tradeString);
        startActivity(i);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String quanItem = data.getStringExtra("quanItem");
                String itemName = data.getStringExtra("itemName");
                String itemPrice = data.getStringExtra("itemPrice");
                Integer koyta = Integer.parseInt(quanItem);
                if(koyta > 0) {
                    cnt++;
                    Log.i(TAG, "before vector");
                    Log.i(TAG, itemPrice);
                    totalPrice += koyta * Integer.parseInt(itemPrice);
                    Log.i(TAG, "middle vector");
                    vItemName.addElement(itemName);
                    vItemQuan.addElement(quanItem);
                    Log.i(TAG, "after vector");
                }
            }
        }
    }

    public void onCheckOut(View v) {
        if(logged.equals("1")) {
            if(cnt > 0) {
                Intent i = new Intent(ItemsList.this, FinalOrder.class);
                i.putExtra("tradeString", tradeString);
                i.putExtra("cnt", String.valueOf(cnt));
                i.putExtra("totalPrice", String.valueOf(totalPrice));

                int k = 0;
                Enumeration en = vItemName.elements();
                while(en.hasMoreElements()) {
                    i.putExtra("itemName" + String.valueOf(k), (String) en.nextElement());
                    k ++;
                }

                k = 0;
                en = vItemQuan.elements();
                while(en.hasMoreElements()) {
                    i.putExtra("itemQuan" + String.valueOf(k), (String) en.nextElement());
                    k ++;
                }

                startActivity(i);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "You are not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    protected void show() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            items = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<items.length();i++){

                JSONObject c = items.getJSONObject(i);
                String name = c.getString(TAG_NAME);

                HashMap<String,String> itlists = new HashMap<>();

                itlists.put(TAG_NAME,name);

                itemList.add(itlists);
            }

            ListAdapter adapter = new SimpleAdapter(ItemsList.this, itemList,R.layout.item_custom_list,
                    new String[]{TAG_NAME},
                    new int[]{R.id.itemName});
            itList.setAdapter(adapter);

            itList.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent i = new Intent(ItemsList.this, ItemInfo.class);

                            JSONObject c = null;
                            String itemName = null;

                            try {
                                c = items.getJSONObject(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                itemName = c.getString(TAG_NAME);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            i.putExtra("TRADE_LICENSE_NO", tradeString);
                            i.putExtra("logged", logged);
                            i.putExtra("ITEM_NAME", itemName);

                            //startActivity(i);
                            startActivityForResult(i, 1);
                        }
                    }
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class itemTask extends AsyncTask<String, String, String > {

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
                url = new URL("http://10.0.3.2/itemsList.php");

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
                        .appendQueryParameter("trade_license_no", params[0]);
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
