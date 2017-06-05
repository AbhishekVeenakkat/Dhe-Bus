package com.abhishekveenakkat.ksrtcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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

/**
 * Created by Abhishek Veenakkat on 22-03-2017.
 */

public class LoginConductor extends AppCompatActivity {
    EditText username;
    EditText password;
    String u;
    String p;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acctivity_login_conductor);
        username= (EditText)findViewById(R.id.input_email);
        password = (EditText)findViewById(R.id.input_password);
    }
    public void GoLogin (View v){
        u = username.getText().toString();
        p = password.getText().toString();

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String rspipaddress = sharedPref.getString("ip", "");
        //String url = "http://192.168.56.1/pswd.php?u=\""+u+"\"&p=\""+p+"\"";
        String url = "http://"+rspipaddress+"/pswd.php?u=\""+u+"\"&p=\""+p+"\"";
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

                String finalJSON = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJSON);
                JSONArray parentArray = parentObject.getJSONArray("pswd");

                StringBuffer finalBufferedData = new StringBuffer();


                if (parentArray.length()==0)
                {
                    return "";
                }


                for (int i=0 ; i<parentArray.length() ; i++)
                {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String uname = finalObject.getString("userid");
                    finalBufferedData.append(uname);
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
                Toast.makeText(LoginConductor.this,"Wrong username or password !",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LoginConductor.this, "Login Successful !", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("clogin",true).commit();

                editor.putString("conductorname",u.toString()).apply();

                Intent intent = new Intent(LoginConductor.this, ConductorDash.class);
                startActivity(intent);
            }
            }
    }
}

