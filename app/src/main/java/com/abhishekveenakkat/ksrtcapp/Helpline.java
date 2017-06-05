package com.abhishekveenakkat.ksrtcapp;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.abhishekveenakkat.ksrtcapp.models.ScheduleModel;

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
import java.util.List;

public class Helpline extends AppCompatActivity {

    private ListView lvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvSchedule = (ListView) findViewById(R.id.schedules);


        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String rspipaddress = sharedPref.getString("ip", "");

        String url = "http://" + rspipaddress + "/getphonenumbers.php";

        String newurl = url.replaceAll(" ", "%20");

        new JSONtask().execute(newurl);

    }

    public void back(View v) {
        onBackPressed();
    }

    public void home(View v) {
        Intent intent = new Intent(Helpline.this, MainActivity.class);
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
                JSONArray parentArray = parentObject.getJSONArray("phonenumbers");

                List<ScheduleModel> scheduleModelList = new ArrayList<>();
                // StringBuffer finalBufferedData = new StringBuffer();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    ScheduleModel scheduleModel = new ScheduleModel();

                    scheduleModel.setStationname(finalObject.getString("contactname"));
                    scheduleModel.setArrivaltime(finalObject.getString("phonenumber"));

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
            return null;

        }

        @Override
        protected void onPostExecute(List<ScheduleModel> result) {
            super.onPostExecute(result);
            ScheduleAdapter adapter = new ScheduleAdapter(getApplicationContext(), R.layout.phonenumberrow, result);
            lvSchedule.setAdapter(adapter);

            lvSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //String food = adapterView.getItemAtPosition(i).toString();
                    //Toast.makeText(SearchList.this,food,Toast.LENGTH_SHORT).show();
//                    Cursor mycursor = (Cursor) adapterView.getItemAtPosition(i);
//                    Toast.makeText(SearchList.this,mycursor.getCount() +"   ",Toast.LENGTH_SHORT).show();
                    TextView number = (TextView) view.findViewById(R.id.timearrival);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number.getText().toString(), null));
                    startActivity(intent);

                    //Toast.makeText(Helpline.this,number.getText().toString()+"",Toast.LENGTH_SHORT).show();
                    //to display route id selected
                    //Toast.makeText(SearchList.this,"Route id : " + textvalue,Toast.LENGTH_SHORT).show();



                }
            });

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
                convertView = inflater.inflate(R.layout.phonenumberrow, null);
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


}
