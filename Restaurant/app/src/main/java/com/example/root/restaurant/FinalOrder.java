package com.example.root.restaurant;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import java.security.PrivateKey;

public class FinalOrder extends AppCompatActivity {

    private String pay = null;
    private Integer cnt;
    private String totalPrice;
    private String tradeString;
    String orderId;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String TAG = "jennysMsg";
    EditText enterCard, enterPin;
    private String getCard, getPin;
    Bundle orderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_order);

        orderData = getIntent().getExtras();
        if(orderData == null) {
            return ;
        }

        pay = orderData.getString("pay");
        tradeString = orderData.getString("tradeString");
        cnt = Integer.parseInt(orderData.getString("cnt"));
        totalPrice = orderData.getString("totalPrice");

        enterCard = (EditText) findViewById(R.id.enterCard);
        enterPin = (EditText) findViewById(R.id.enterPin);

        getCard = null;
        getPin = null;

        //if final, go to suggested restaurant list
    }

    public void onOrder(View v) {

        getCard = enterCard.getText().toString();
        getPin = enterPin.getText().toString();

        if(getCard != null && !getCard.isEmpty() && getPin != null && !getPin.isEmpty()) {

            new cardTask().execute(getCard, getPin);
        }
        else {
            Toast.makeText(getApplicationContext(), "Please Enter Card Information", Toast.LENGTH_SHORT).show();
        }

    }

    private class orderAddTask extends AsyncTask<String, String, String > {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Ordering...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("http://10.0.3.2/orderAdd.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }


            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("trade_license_no", params[0])
                        .appendQueryParameter("totalPrice", params[1]);
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
                    return (result.toString());

                } else {

                    return ("unsuccessful");
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
            orderId = result;
            Log.i(TAG, result);
            for(int i = 0; i < cnt; i ++) {
                String itemName = orderData.getString("itemName" + String.valueOf(i));
                String itemQuan = orderData.getString("itemQuan" + String.valueOf(i));
                new orderDetailsTask().execute(orderId, tradeString, itemName, itemQuan);
            }
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    private class cardTask extends AsyncTask<String, String, String > {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Verifying Card...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("http://10.0.3.2/card.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("card", params[0])
                        .appendQueryParameter("pin", params[1]);
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
                    return (result.toString());

                } else {

                    return ("unsuccessful");
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

            Log.i(TAG, result);
            super.onPostExecute(result);
            if(result.equals("failure")) Toast.makeText(getApplicationContext(), "Card Information in Wrong", Toast.LENGTH_SHORT).show();
            else {
                Integer amount = Integer.parseInt(result);
                if(amount >= Integer.parseInt(totalPrice)) {
                    //Toast.makeText(getApplicationContext(), "You have enough balance", Toast.LENGTH_SHORT).show();
                    new deductAmountTask().execute(getCard, totalPrice);
                }
                else {
                    Toast.makeText(getApplicationContext(), "You don't have enough balance", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class deductAmountTask extends AsyncTask<String, String, String > {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Deducting Amount...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("http://10.0.3.2/deductAmount.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }


            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("card", params[0])
                        .appendQueryParameter("amount", params[1]);
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
                    return (result.toString());

                } else {

                    return ("unsuccessful");
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
            Toast.makeText(getApplicationContext(), totalPrice + " tk Deducted", Toast.LENGTH_SHORT).show();

            new orderAddTask().execute(tradeString, totalPrice);
        }
    }

    private class orderDetailsTask extends AsyncTask<String, String, String > {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Adding Order Details...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("http://10.0.3.2/orderDetails.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }


            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("orderId", params[0])
                        .appendQueryParameter("tradeString", params[1])
                        .appendQueryParameter("itemName", params[2])
                        .appendQueryParameter("itemQuan", params[3]);
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
                    return (result.toString());

                } else {

                    return ("unsuccessful");
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
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }
}
