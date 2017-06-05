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
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static com.abhishekveenakkat.ksrtcapp.NotificationService.runnable;


public class LocationFetcher extends Service {

    public Context context = this;
    public Handler handler = null;
    String Rid,lat,lng,newurl,url,rspipaddress;
    Double la,lo;
    int routeid;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        //notifyThis(from,"Is now at : ");
        //Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        rspipaddress = sharedPref.getString("ip", "");
        //String url = "http://192.168.56.1/pswd.php?u=\""+u+"\"&p=\""+p+"\"";





        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                new JSONtask().execute(newurl);

                Intent i = new Intent("location_update");
                i.putExtra("longitude",lo);
                i.putExtra("latitude",la);
                sendBroadcast(i);
                //notifyThis(from,"Is now at : "+ i++ );
                handler.postDelayed(runnable, 2000);
            }
        };

        handler.postDelayed(runnable, 2000);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Bundle bundle = intent.getExtras();
        Rid = bundle.getString("RouteID");
        routeid = Integer.parseInt(Rid);
        Toast.makeText(this, "Wait a moment for precise location." + Rid, Toast.LENGTH_LONG).show();
        url = "http://"+rspipaddress+"/getlocation.php?rid="+routeid;
        newurl = url.replaceAll(" ","%20");
        new JSONtask().execute(newurl);
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

                String finalJSON = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJSON);
                JSONArray parentArray = parentObject.getJSONArray("location");

                StringBuffer finalBufferedData = new StringBuffer();


                if (parentArray.length()==0)
                {
                    return "";
                }


                for (int i=0 ; i<parentArray.length() ; i++)
                {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    lat = finalObject.getString("latitude");
                    lng = finalObject.getString("longitude");

                    finalBufferedData.append(lat + lng);
                }


                la = Double.parseDouble(lat.toString());
                lo = Double.parseDouble(lng.toString());

                return finalBufferedData.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
            if(result.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Error obtaining Location",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
