package com.abhishekveenakkat.ksrtcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.ArrayList;

/**
 * Created by Abhishek Veenakkat on 23-03-2017.
 */

public class PanicMsg extends AppCompatActivity {
    EditText busc;
    String a;
    RelativeLayout L;
    TextView pm,sendnum;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_message);
        busc = (EditText)findViewById(R.id.input_buscode);
        L = (RelativeLayout)findViewById(R.id.msgpart);
        L.setVisibility(View.INVISIBLE);
        pm = (TextView)findViewById(R.id.panicmessagedraft);
        sendnum = (TextView)findViewById(R.id.phonenumbers);
    }
    public void GoUserDetails (View v){
        Intent intent = new Intent(PanicMsg.this, UserDetail.class);
        startActivity(intent);
    }
    public void GoFetch (View v){
        String code = busc.getText().toString();

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String rspipaddress = sharedPref.getString("ip", "");

        if (code.equals(""))
        {
            Toast.makeText(PanicMsg.this,"Enter bus code !",Toast.LENGTH_SHORT).show();
        }
        else {
            //String url = "http://192.168.56.1/pswd.php?u=\""+u+"\"&p=\""+p+"\"";
            String url = "http://"+rspipaddress+"/panic.php?bid=\"" + code + "\"";
            String newurl = url.replaceAll(" ", "%20");

            new JSONtask().execute(newurl);
        }
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

                    finalBufferedData.append("Bus details \n"+"Bus code: "+busid+"\nReg Num: "+regnum+"\nRoute: "+routename+" "+description+"\nDriver: "+driver+"\nConductor: "+conductor+"\nOperating Depot: "+operatingdepot);
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
                L.setVisibility(View.INVISIBLE);
                Toast.makeText(PanicMsg.this,"No such bus code !",Toast.LENGTH_SHORT).show();
            }
            else {
                SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                String name = sharedPref.getString("name", "");
                String phone = sharedPref.getString("phone", "");
                String sphone = sharedPref.getString("sphone", "");
                String address = sharedPref.getString("address", "");
                a = "I "+name+" traveling in KSRTC bus has met an accident. Kindly do something.\nMy Address: "+address+"\n"+result;
                pm.setText(a);
                sendnum.setText("Send to: Police helpline, "+phone+", "+sphone);
                L.setVisibility(View.VISIBLE);
                //Toast.makeText(PanicMsg.this,result,Toast.LENGTH_LONG).show();
            }
        }
    }
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        GoFetch(busc);
    }
    public void SendPanic(View v){
       sendSMS("8893124420",a);
    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
