package com.abhishekveenakkat.ksrtcapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SelectcityFragment.OnItemSelectedListener {
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    TextView tv1;
    TextView tv2;
    ImageView im;
    int i=0,j=0;
    TextView navName;
    TextView navNum;
    SharedPreferences prefs = null;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //First run check aadyayt run cheyyanenki work avm
        prefs = getSharedPreferences("com.abhishekveenakkat.ksrtcapp", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            Toast.makeText(MainActivity.this, "First time run !", Toast.LENGTH_SHORT).show();
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            //Initializations ivde ahn cheydirkkne
            //Conductor login made false
            SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("clogin",false).commit();
            editor.putBoolean("istracking",false).commit();
            editor.putString("name", "Username");
            editor.putString("ip", "0.0.0.0");
            editor.putString("address", "Your Address");
            editor.putString("phone", "Your Phone Number");
            editor.putString("sphone", "Secondary Phone Number");
            prefs.edit().putBoolean("firstrun", false).commit();
            editor.apply();

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String name = sharedPref.getString("name", "");
        String phone = sharedPref.getString("phone", "");

        NavigationView nnavigationView = (NavigationView) findViewById(R.id.nav_view);
        nnavigationView.setNavigationItemSelectedListener(this);
        View header=nnavigationView.getHeaderView(0);
        navName = (TextView)header.findViewById(R.id.navname);
        navName.setText(name);
        navNum = (TextView)header.findViewById(R.id.textViewNum);
        navNum.setText(phone);

        final FragmentManager fm=getFragmentManager();
        final SelectcityFragment p=new SelectcityFragment();
        tv1=(TextView) findViewById(R.id.fromtext);

        im=(ImageView)findViewById(R.id.imageView2);
        im.setVisibility(View.INVISIBLE);


        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                i=0;
                j=1;
                p.show(fm, "Select Cities");
            }
        });

        tv2=(TextView) findViewById(R.id.totext);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                i=1;
                j=0;
                p.show(fm, "Select Cities");
            }
        });





        TextView day = (TextView)findViewById(R.id.day);
        TextView daytext = (TextView)findViewById(R.id.day2);
        TextView monthtext = (TextView)findViewById(R.id.day3);
        TextView year = (TextView)findViewById(R.id.day4);

        Calendar cal =Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMMM/dd/E", Locale.US);
        String strdate = sdf.format(cal.getTime());
        String[] values = strdate.split("/",0);

        day.setText(values[2]);
        daytext.setText(values[3]);
        monthtext.setText(values[1]);
        year.setText(values[0]);

        spinner =(Spinner) findViewById(R.id.spinnerone);
        adapter=ArrayAdapter.createFromResource(this,R.array.bustypes,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              if(i!=0){
                  im.setVisibility(View.VISIBLE);
              }
                Snackbar.make(im, adapterView.getItemAtPosition(i)+" Selected", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String name = sharedPref.getString("name", "");
        String phone = sharedPref.getString("phone", "");

        NavigationView nnnavigationView = (NavigationView) findViewById(R.id.nav_view);
        nnnavigationView.setNavigationItemSelectedListener(this);
        View header=nnnavigationView.getHeaderView(0);

        navName = (TextView)header.findViewById(R.id.navname);
        navName.setText(name);
        navNum = (TextView)header.findViewById(R.id.textViewNum);
        navNum.setText(phone);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the searchbus action

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(MainActivity.this, Helpline.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {

            //Intent i = new Intent(MainActivity.this,GPS_Service.class);
            //startService(i);
            Intent intent = new Intent(MainActivity.this, PanicMsg.class);
            startActivity(intent);
            /*Panic message to :-
            Intent intent = new Intent(MainActivity.this, PanicMsg.class);
            startActivity(intent);

            */
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);

        } else if (id == R.id.nav_condutor) {
            SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            if(!sharedPref.getBoolean("clogin",true)) {
                Intent intent = new Intent(MainActivity.this, LoginConductor.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(MainActivity.this, ConductorDash.class);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onPressed(View view) {
        if (tv1.getText().equals("From") && tv2.getText().equals("To")) {
        }else if (tv1.getText().equals("From")&&!(tv2.getText().equals("To"))){
            tv1.setText(tv2.getText());
            tv2.setText("To");
        }else if ((tv2.getText().equals("To"))&&!(tv1.getText().equals("From"))){
            tv2.setText(tv1.getText());
            tv1.setText("From");
        }
        else {
            String temp = tv1.getText().toString();
            tv1.setText(tv2.getText());
            tv2.setText(temp);
        }
    }

    public void goUDetail (View view){
        Intent intent = new Intent(MainActivity.this, UserDetail.class);

        startActivity(intent);
    }
    public void goSearch (View view){
        Intent intent = new Intent(MainActivity.this, SearchList.class);
        intent.putExtra("From", tv1.getText().toString());
        intent.putExtra("To", tv2.getText().toString());
        intent.putExtra("Type", spinner.getSelectedItem().toString());
        if (tv1.getText().equals("From") || tv2.getText().equals("To")) {
            Snackbar.make(tv1, "Choose a source and destination !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }else if(tv1.getText().equals(tv2.getText())){
            Snackbar.make(tv1, "Source and destination are same !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }else
        //Toast.makeText(MainActivity.this,"Will be showing results for "+spinner.getSelectedItem() + " type of buses from "+tv1.getText()+" to "+tv2.getText(),Toast.LENGTH_SHORT).show();


        startActivity(intent);
    }

    public void resetBox(View view){
        tv1.setText("From");
        tv2.setText("To");
        spinner.setSelection(0);
        im.setVisibility(View.INVISIBLE);
    }


    @Override
    public void itemselected(String city_name) {

        if(i==0&&j==1) {
            tv1.setText(city_name);
            Snackbar.make(tv1, "Source : "+city_name, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            i=0;
            j=0;
        }
        else if (i==1&&j==0) {
            tv2.setText(city_name);
            Snackbar.make(tv2, "Destination : "+city_name, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            i=0;
            j=0;
        }
        im.setVisibility(View.VISIBLE);
    }
}
