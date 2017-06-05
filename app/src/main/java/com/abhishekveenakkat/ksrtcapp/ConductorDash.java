package com.abhishekveenakkat.ksrtcapp;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
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

public class ConductorDash extends AppCompatActivity implements SendReportFragment.OnItemSelectedListener {
    String Name;
    TextView Nameofc;
    TextView disabledtext;
    ImageView trackimage;
    ImageView exit;
    String buscode;
    Button failure;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductor_dash);

        disabledtext = (TextView) findViewById(R.id.disavledtext);
        trackimage = (ImageView) findViewById(R.id.trackingimage);
        exit =(ImageView) findViewById(R.id.exit);
        failure = (Button) findViewById(R.id.failure);

        failure.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        Name = sharedPref.getString("conductorname", "");
        Nameofc = (TextView)findViewById(R.id.username);
        Nameofc.setText(Name.toString());
        buscode = sharedPref.getString("buscode", "").toString();
        if(sharedPref.getBoolean("istracking", true)){
            trackimage.setImageResource(R.drawable.tracking);
            exit.setVisibility(View.INVISIBLE);
            failure.setVisibility(View.VISIBLE);
            disabledtext.setText("Tracking for "+buscode);
        }

    }
    public void onBackPressed() {
        Intent intent = new Intent(ConductorDash.this, MainActivity.class);
        startActivity(intent);
    }
    public void LogOut(View v){
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("clogin",false).commit();
        Intent intent = new Intent(ConductorDash.this, MainActivity.class);
        startActivity(intent);
    }
    public void GoSmLogin (View v){
        Intent intent = new Intent(ConductorDash.this, LoginSm.class);
        startActivity(intent);
    }
    public void OpenFrag (View v){

        final FragmentManager fm=getFragmentManager();
        final SendReportFragment p=new SendReportFragment();
        p.show(fm, "Send Report");
    }
    @Override
    public void itemselected(String failure) {
        Snackbar.make(findViewById(R.id.failure),failure+" reported !", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String rspipaddress = sharedPref.getString("ip", "");
        //String url = "http://192.168.56.1/pswd.php?u=\""+u+"\"&p=\""+p+"\"";
       // http://localhost/postfailure.php?b=%22kkd%22&c=%22sa%22&f=%22poi%22

        String url = "http://"+rspipaddress+"/postfailure.php?b=\""+buscode+"\"&c=\""+Name+"\"&f=\""+failure+"\"";
        String newurl = url.replaceAll(" ","%20");

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
