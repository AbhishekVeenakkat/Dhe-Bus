package com.abhishekveenakkat.ksrtcapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserDetail extends AppCompatActivity {
EditText name;
    EditText address;
    EditText phone;
    EditText sphone;
    EditText ip;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //reading sharedpreferences
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String rspipaddress = sharedPref.getString("ip", "");

        //mukalile line ip address read cheyyan
        String rspname = sharedPref.getString("name", "");
        String rspphone = sharedPref.getString("phone", "");
        String rspaddress = sharedPref.getString("address", "");
        String rspsphone = sharedPref.getString("sphone", "");


        name = (EditText) findViewById(R.id.nameedit);
        address = (EditText) findViewById(R.id.addressedit);
        phone = (EditText) findViewById(R.id.phoneedit);
        sphone = (EditText) findViewById(R.id.sphoneedit);
        ip = (EditText) findViewById(R.id.ipaddress);

        name.setText(rspname);
        phone.setText(rspphone);
        sphone.setText(rspsphone);
        address.setText(rspaddress);
        ip.setText(rspipaddress);
    }
    public void saveInfo(View view){
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name",name.getText().toString());
        editor.putString("address",address.getText().toString());
        editor.putString("phone",phone.getText().toString());
        editor.putString("sphone",sphone.getText().toString());
        editor.putString("ip", ip.getText().toString());
        editor.apply();

        Toast.makeText(this,"Saved !",Toast.LENGTH_SHORT).show();
    }
}
