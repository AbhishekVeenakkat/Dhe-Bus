package com.abhishekveenakkat.ksrtcapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

/**
 * Created by Abhishek Veenakkat on 24-03-2017.
 */

public class SmDash extends AppCompatActivity{
    String name;
    Button enabletracking;
    Button disabletracking;
    TextView busnameline;
    TextView username;
    EditText busc;
    Button tick;
    TextView disabledtext;
    TextView infotext;
    ImageView trackingImage;
    String rspipaddress;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_dash);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("Smname");

        busc = (EditText) findViewById(R.id.input_buscode);
        tick = (Button) findViewById(R.id.tick);
        disabledtext = (TextView) findViewById(R.id.disavledtext);
        infotext = (TextView) findViewById(R.id.infotext);
        trackingImage = (ImageView) findViewById(R.id.trackingimage);

        enabletracking = (Button)findViewById(R.id.enabletracking);
        enabletracking.setVisibility(View.INVISIBLE);

        disabletracking = (Button)findViewById(R.id.disabletracking);
        disabletracking.setVisibility(View.INVISIBLE);

        busnameline = (TextView) findViewById(R.id.trackingbusname);
        busnameline.setVisibility(View.INVISIBLE);

        username = (TextView) findViewById(R.id.username);
        username.setText(name.toString());

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("istracking", true)){
            enabletracking.setVisibility(View.INVISIBLE);
            disabletracking.setVisibility(View.VISIBLE);
            busc.setVisibility(View.INVISIBLE);
            tick.setVisibility(View.INVISIBLE);
            infotext.setText("Click on disable tracking to turn of tracking.");
            trackingImage.setImageResource(R.drawable.tracking);
            busnameline.setVisibility(View.VISIBLE);
            busnameline.setText(sharedPref.getString("busnameline", "").toString());
            disabledtext.setText("Tracking for "+sharedPref.getString("buscode", "").toString());
        }

    }
    public void onBackPressed() {
        Intent intent = new Intent(SmDash.this, ConductorDash.class);
        startActivity(intent);
    }
    public void GoFetch (View v){
        String code = busc.getText().toString();

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        rspipaddress = sharedPref.getString("ip", "");

        if (code.equals(""))
        {
            Toast.makeText(SmDash.this,"Enter bus code !",Toast.LENGTH_SHORT).show();
        }
        else {
            //String url = "http://192.168.56.1/pswd.php?u=\""+u+"\"&p=\""+p+"\"";
            String url = "http://"+rspipaddress+"/panic.php?bid=\"" + code + "\"";
            String newurl = url.replaceAll(" ", "%20");

            new JSONtask().execute(newurl);
        }
    }

    //enable track
    public void GoTrack(View v){

        String android_id = Settings.Secure.getString(SmDash.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        enabletracking.setVisibility(View.INVISIBLE);
        disabletracking.setVisibility(View.VISIBLE);
        busc.setVisibility(View.INVISIBLE);
        tick.setVisibility(View.INVISIBLE);
        infotext.setText("Click on disable tracking to turn of tracking.");
        trackingImage.setImageResource(R.drawable.tracking);

        disabledtext.setText("Tracking for "+busc.getText().toString());

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("istracking",true);
        editor.apply();

        notifyThis("Location sending process","Tracking for "+busc.getText().toString());
        String urltwo = "http://"+rspipaddress+"/makeentry.php?busid="+busc.getText().toString();
        String newurltwo = urltwo.replaceAll(" ", "%20");

        new JSONtaskTwo().execute(newurltwo);

        Intent i = new Intent(this,TempAct.class);
        startActivity(i);
    }

    public void OffTrack(View v){

        Intent i = new Intent(getApplicationContext(),GPSS_Service.class);

        stopService(i);
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("istracking",false);
        editor.apply();

        clearnotification();
        Toast.makeText(SmDash.this,"Tracking Disabled !",Toast.LENGTH_LONG).show();
        rspipaddress = sharedPref.getString("ip", "");
        String urltwo = "http://"+rspipaddress+"/deleteentry.php?busid="+sharedPref.getString("buscode", "").toString();
        String newurltwo = urltwo.replaceAll(" ", "%20");

        new JSONtaskTwo().execute(newurltwo);
        Intent intent = new Intent(SmDash.this, ConductorDash.class);
        startActivity(intent);
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
                JSONArray parentArray = parentObject.getJSONArray("bus");

                StringBuffer finalBufferedData = new StringBuffer();


                if (parentArray.length()==0)
                {
                    return "";
                }


                for (int i=0 ; i<parentArray.length() ; i++)
                {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String busid = finalObject.getString("busid");
                    String regnum = finalObject.getString("regnum");
                    String driver = finalObject.getString("driver");
                    String conductor = finalObject.getString("conductor");
                    String operatingdepot = finalObject.getString("operatingdepot");
                    String routename = finalObject.getString("routename");
                    String description = finalObject.getString("description");

                    finalBufferedData.append("Bus Reg Num: "+regnum+"\nRoute: "+routename+" "+description);
                }



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

                Toast.makeText(SmDash.this,"No such bus code !",Toast.LENGTH_SHORT).show();
                busnameline.setVisibility(View.INVISIBLE);
                enabletracking.setVisibility(View.INVISIBLE);
            }
            else {

                busnameline.setText(result.toString());
                busnameline.setVisibility(View.VISIBLE);
                enabletracking.setVisibility(View.VISIBLE);

                SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("buscode",busc.getText().toString());
                editor.putString("busnameline",result.toString());
                editor.apply();
                //Toast.makeText(PanicMsg.this,result,Toast.LENGTH_LONG).show();
            }
        }
    }
    public class JSONtaskTwo extends AsyncTask<String, String, String> {

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
    public void notifyThis(String title, String message) {
        Intent notificationIntent = new Intent(this, MainActivity.class); // Change it any activity, you want it to open.


        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(SmDash.this);
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.panic)
                .setContentTitle(title)
                .setAutoCancel(false)
                .setContentText(message)
                .setContentInfo("TOBENAMED")
                .setContentIntent(pi)
        .setOngoing(true);




        NotificationManager nm = (NotificationManager) SmDash.this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }
    public void clearnotification (){
        NotificationManager nm = (NotificationManager) SmDash.this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1);
    }



    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){

            }else {
                runtime_permissions();
            }
        }
    }
}
