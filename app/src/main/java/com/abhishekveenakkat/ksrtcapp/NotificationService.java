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
        import android.content.*;
        import android.location.Address;
        import android.location.Geocoder;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.*;
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


public class NotificationService extends Service {

    public Context context = this;
    public Handler handler = null;
    String Name,Via,Type,Source,Dest,Dtime,Stime,Rid,lat,lng;
    Double la,lo;
    int i=0,rid;
    public static Runnable runnable = null;
    SharedPreferences prefs = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        //notifyThis(from,"Is now at : ");
        //Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                String rspipaddress = sharedPref.getString("ip", "");
                //String url = "http://192.168.56.1/pswd.php?u=\""+u+"\"&p=\""+p+"\"";
                String url = "http://"+rspipaddress+"/getlocation.php?rid="+Rid;
                String newurl = url.replaceAll(" ","%20");

                new JSONtask().execute(newurl);


                //notifyThis(from,"Is now at : "+ i++ );
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 15000);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        clearnotification();
        handler.removeCallbacks(runnable);
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Bundle bundle = intent.getExtras();
        Name = bundle.getString("Bname");
        Via = bundle.getString("Bvia");
        Type = bundle.getString("Btype");
        Source = bundle.getString("Bsource");
        Dest = bundle.getString("Bdest");
        Dtime = bundle.getString("Bdtime");
        Stime = bundle.getString("Bstime");
        Rid = bundle.getString("Routeid");


        rid = Integer.parseInt(Rid);
        notifyThis(Name,"Wait for a moment for location !");
       // Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }
    public void notifyThis(String title, String message) {

        prefs = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("Notification",true).commit();

        Intent notificationIntent = new Intent(this, SingleBusInfo.class); // Change it any activity, you want it to open.
        notificationIntent.putExtra("Bname",Name.toString());
        notificationIntent.putExtra("Bvia",Via.toString());
        notificationIntent.putExtra("Btype",Type.toString());
        notificationIntent.putExtra("Bsource",Source.toString());
        notificationIntent.putExtra("Bdest",Dest.toString());
        notificationIntent.putExtra("Routeid",Rid.toString());
        notificationIntent.putExtra("Bstime",Stime.toString());
        notificationIntent.putExtra("Bdtime",Dtime.toString());
        notificationIntent.putExtra("Kill",true);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notificationIntentTwo = new Intent(this, MainActivity.class); // Change it any activity, you want it to open.
        PendingIntent piTwo = PendingIntent.getActivity(this, 0, notificationIntentTwo, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.about)
                .setContentTitle(title)
                .setAutoCancel(false)
                .setContentText(message)
                .setContentInfo(Type)
                .setOngoing(false)
                .addAction(0,"DISABLE",pi)
                .addAction(0,"HOME",piTwo);


        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(5, b.build());
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

                    //finalBufferedData.append(lat + lng);
                }

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                la = Double.parseDouble(lat.toString());
                lo = Double.parseDouble(lng.toString());
                List<Address> addresses = geocoder.getFromLocation(la, lo, 1);
                String Location = addresses.get(0).getAddressLine(1);

                finalBufferedData.append(Location);
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
                Toast.makeText(getApplicationContext(),"No Location Update !",Toast.LENGTH_SHORT).show();
            }
            else {
                notifyThis(Name,"Is now at : "+ result );
                //Toast.makeText(getApplicationContext(),"Location : " + result,Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void clearnotification (){
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(5);
    }
}
