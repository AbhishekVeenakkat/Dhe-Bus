package com.abhishekveenakkat.ksrtcapp;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.abhishekveenakkat.ksrtcapp.models.ScheduleModel;
import org.json.JSONArray;
import org.json.JSONException
;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SingleBusInfo extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener , PostFeedbackFragment.OnItemSelectedListener{
TextView Bname;
    TextView Bvia;
    TextView Btype;
    TextView Bsource;
    TextView Bdest;
    TextView Bstime;
    TextView Bdtime;
    Switch notification;
    int rats,ratp,ratc,ratcount;
    String  Name,Via,Type,Source,Dest,Dtime,Stime,Rid,temp;
    ImageView c,s,p;
    private ListView lvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_bus_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvSchedule = (ListView)findViewById(R.id.schedules);

        notification = (Switch) findViewById(R.id.switch1);
        notification.setOnCheckedChangeListener(this);

        Bname = (TextView) findViewById(R.id.routename);
        Bvia = (TextView) findViewById(R.id.viatext);
        Btype = (TextView) findViewById(R.id.typetext);
        Bsource = (TextView) findViewById(R.id.places);
        Bdest = (TextView) findViewById(R.id.placed);
        Bstime = (TextView) findViewById(R.id.times);
        Bdtime = (TextView) findViewById(R.id.timed);

        c = (ImageView) findViewById(R.id.imageView16);
        s = (ImageView) findViewById(R.id.imageView15);
        p = (ImageView) findViewById(R.id.imageView10);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        Bundle bundle = getIntent().getExtras();
        Name = bundle.getString("Bname");
        Via = bundle.getString("Bvia");
        Type = bundle.getString("Btype");
        Source = bundle.getString("Bsource");
        Dest = bundle.getString("Bdest");
        Dtime = bundle.getString("Bdtime");
        Stime = bundle.getString("Bstime");
        Rid = bundle.getString("Routeid");

        Bsource.setText(Source);
        Bname.setText(Name);
        Bvia.setText(Via);
        Btype.setText(Type);
        Bdest.setText(Dest);
        Bstime.setText(Stime);
        Bdtime.setText(Dtime);





        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Tracking task to be opened !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                 */
                SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                String rspipaddress = sharedPref.getString("ip", "");
                //String url = "http://192.168.56.1/pswd.php?u=\""+u+"\"&p=\""+p+"\"";
                String url = "http://"+rspipaddress+"/getlocation.php?rid="+Rid.toString();
                String newurl = url.replaceAll(" ","%20");
                new JSONtaskThree().execute(newurl);

            }
        });

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("Notification",false)){
            Boolean flag = bundle.getBoolean("Kill");
            if(flag.equals(true)) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("Notification", false).commit();
                Snackbar.make(notification, "Notification Disabled !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                stopService(new Intent(this, NotificationService.class));
            }
        }




        String rspipaddress = sharedPref.getString("ip", "");

        String url = "http://"+rspipaddress+"/getrating.php?rid="+Rid;

        String newurl = url.replaceAll(" ","%20");

        new JSONtaskFour().execute(newurl);



        url = "http://"+rspipaddress+"/schedule.php?id="+Rid+"";

        newurl = url.replaceAll(" ","%20");

        new JSONtask().execute(newurl);

    }
    public void back(View v) {
        onBackPressed();
    }
    public void home(View v) {
        Intent intent = new Intent(SingleBusInfo.this, MainActivity.class);
        startActivity(intent);
    }

    public class JSONtask extends AsyncTask<String, String, List<ScheduleModel>> {

        @Override
        protected List<ScheduleModel> doInBackground(String... params) {

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
                JSONArray parentArray = parentObject.getJSONArray("schedule");

                List<ScheduleModel> scheduleModelList = new ArrayList<>();
                // StringBuffer finalBufferedData = new StringBuffer();
                for(int i = 0;i<parentArray.length();i++){
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    ScheduleModel scheduleModel = new ScheduleModel();
                    scheduleModel.setRouteid(finalObject.getString("routeid"));

                    scheduleModel.setStationname(finalObject.getString("stationname"));
                    scheduleModel.setArrivaltime(finalObject.getString("time"));

                    scheduleModelList.add(scheduleModel);
                }
                return scheduleModelList;


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
            return  null;

        }

        @Override
        protected void onPostExecute(List<ScheduleModel> result) {
            super.onPostExecute(result);
            ScheduleAdapter adapter = new ScheduleAdapter(getApplicationContext(),R.layout.shedulerow,result);
            lvSchedule.setAdapter(adapter);

        }
    }

    public class ScheduleAdapter extends ArrayAdapter {
        private List<ScheduleModel> scheduleModelList;
        private int resource;
        private LayoutInflater inflater;
        public ScheduleAdapter(Context context, int resource, List<ScheduleModel> objects) {
            super(context, resource, objects);
            scheduleModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.shedulerow, null);
            }

            TextView stationname;
            TextView arrivaltime;

            stationname = (TextView) convertView.findViewById(R.id.stationnametext);
            arrivaltime = (TextView) convertView.findViewById(R.id.timearrival);

            arrivaltime.setText(scheduleModelList.get(position).getArrivaltime()+"");
            stationname.setText(scheduleModelList.get(position).getStationname()+"");

            return convertView;
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
                    String lat = finalObject.getString("latitude");

                    finalBufferedData.append(lat);
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
                Snackbar.make(notification, "No traking available !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                notification.setChecked(false);
            }
            else {
                Snackbar.make(notification, "Notification Enabled !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                intent.putExtra("Bname",Name.toString());
                intent.putExtra("Bvia",Via.toString());
                intent.putExtra("Btype",Type.toString());
                intent.putExtra("Bsource",Source.toString());
                intent.putExtra("Bdest",Dest.toString());
                intent.putExtra("Routeid",Rid.toString());
                intent.putExtra("Bstime",Stime.toString());
                intent.putExtra("Bdtime",Dtime.toString());
                startService(intent);
            }
        }
    }
    public class JSONtaskThree extends AsyncTask<String, String, String> {

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
                    String lat = finalObject.getString("latitude");

                    finalBufferedData.append(lat);
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
                Snackbar.make(notification, "No traking available !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                notification.setChecked(false);
            }
            else {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("RouteID",Rid.toString());
                i.putExtra("Bname",Name + " " + Type + " " + Via);
                startActivity(i);
            }
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            String rspipaddress = sharedPref.getString("ip", "");
            //String url = "http://192.168.56.1/pswd.php?u=\""+u+"\"&p=\""+p+"\"";
            String url = "http://"+rspipaddress+"/getlocation.php?rid="+Rid.toString();
            String newurl = url.replaceAll(" ","%20");
            new JSONtaskTwo().execute(newurl);


        } else {
            Snackbar.make(notification, "Notification Disabled !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            stopService(new Intent(this, NotificationService.class));
        }
    }
    public void OpenFrag (View v){

        final FragmentManager fm=getFragmentManager();
        final PostFeedbackFragment p=new PostFeedbackFragment();
        p.show(fm, "Post Feedback");
    }
    @Override
    public void itemselected(int r1,int r2,int r3) {
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);

        String rspipaddress = sharedPref.getString("ip", "");

        String url = "http://"+rspipaddress+"/postreview.php?rid="+Rid+"&c="+r1+"&s="+r2+"&p="+r3;

        String newurl = url.replaceAll(" ","%20");

        new JSONtaskFive().execute(newurl);
        Snackbar.make(notification, "Review posted !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        finish();
        startActivity(getIntent());
    }
    public class JSONtaskFour extends AsyncTask<String, String, String> {

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
                JSONArray parentArray = parentObject.getJSONArray("rating");

                StringBuffer finalBufferedData = new StringBuffer();


                if (parentArray.length()==0)
                {
                    return "";
                }


                for (int i=0 ; i<parentArray.length() ; i++)
                {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    temp = finalObject.getString("ratingcount");
                    ratcount = Integer.parseInt(temp);
                    if(ratcount==0){
                        ratcount=1;
                    }
                    temp = finalObject.getString("crating");
                    ratc = Integer.parseInt(temp)/ratcount;
                    temp = finalObject.getString("srating");
                    rats = Integer.parseInt(temp)/ratcount;
                    temp = finalObject.getString("prating");
                    ratp = Integer.parseInt(temp)/ratcount;

                    finalBufferedData.append(temp);
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
                Snackbar.make(notification, "Error !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            else {
                switch (ratc){
                    case 0:{
                        c.setImageResource(R.drawable.rone);
                        break;
                    }
                    case 1:{
                        c.setImageResource(R.drawable.rone);
                        break;
                    }
                    case 2:{
                        c.setImageResource(R.drawable.rtwo);
                        break;
                    }
                    case 3:{
                        c.setImageResource(R.drawable.rthree);
                        break;
                    }
                    case 4:{
                        c.setImageResource(R.drawable.rfour);
                        break;
                    }
                    case 5:{
                        c.setImageResource(R.drawable.rfive);
                        break;
                    }
                }
                switch (rats){
                    case 0:{
                        s.setImageResource(R.drawable.rone);
                        break;
                    }
                    case 1:{
                        s.setImageResource(R.drawable.rone);
                        break;
                    }
                    case 2:{
                        s.setImageResource(R.drawable.rtwo);
                        break;
                    }
                    case 3:{
                        s.setImageResource(R.drawable.rthree);
                        break;
                    }
                    case 4:{
                        s.setImageResource(R.drawable.rfour);
                        break;
                    }
                    case 5:{
                        s.setImageResource(R.drawable.rfive);
                        break;
                    }

                }
                switch (ratp){
                    case 0:{
                        p.setImageResource(R.drawable.rone);
                        break;
                    }
                    case 1:{
                        p.setImageResource(R.drawable.rone);
                        break;
                    }
                    case 2:{
                        p.setImageResource(R.drawable.rtwo);
                        break;
                    }
                    case 3:{
                        p.setImageResource(R.drawable.rthree);
                        break;
                    }
                    case 4:{
                        p.setImageResource(R.drawable.rfour);
                        break;
                    }
                    case 5:{
                        p.setImageResource(R.drawable.rfive);
                        break;
                    }
                }

                //Snackbar.make(notification, "Success !", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        }
    }
    public class JSONtaskFive extends AsyncTask<String, String, String> {

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
