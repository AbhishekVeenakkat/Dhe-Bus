package com.abhishekveenakkat.ksrtcapp;

/**
 * Created by Abhishek Veenakkat on 09-04-2017.
 */


/**
 * Created by Abhishek Veenakkat on 09-04-2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class GPSS_Service extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    String newurl;
    String buscode;
    String android_id;
    String rspipaddress;
    Double lat,log;
    String url;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        //notifyThis(from,"Is now at : ");
        //Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        //Toast.makeText(this, "Sending Co-ordinates to server !", Toast.LENGTH_LONG).show();


        //look wheather this shared pref work. if not try inside the loop. matte sm dashlu cheydapole ccheyyendyerm
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        buscode = sharedPref.getString("buscode", "").toString();
        rspipaddress = sharedPref.getString("ip", "");
        android_id = Settings.Secure.getString(GPSS_Service.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        lat=10.830177;
        log=76.023408;
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                Intent i = new Intent("location_update");
                i.putExtra("coordinates",log+" "+lat);
                sendBroadcast(i);

                url = "http://"+rspipaddress+"/pushlocation.php?deviceid="+android_id+"&busid="+buscode+"&lon="+log+"&lat="+lat;
                String newurl = url.replaceAll(" ","%20");

                //Toast.makeText(getApplicationContext(), "Lat : "+lat+"\nLng : "+log, Toast.LENGTH_LONG).show();
                new GPSS_Service.JSONtask().execute(newurl);
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 15000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Location update stopped !", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Sending Co-ordinates to server !", Toast.LENGTH_LONG).show();
    }
    public class JSONtask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            BufferedReader reader = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  "";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //  if(result.isEmpty())
            //  {
            //  Toast.makeText(LoginConductor.this,"Wrong username or password !",Toast.LENGTH_SHORT).show();
            //  }
            //  else {
            //  Toast.makeText(LoginConductor.this, "Login Successful !", Toast.LENGTH_SHORT).show();
            //  }
        }
    }

}
