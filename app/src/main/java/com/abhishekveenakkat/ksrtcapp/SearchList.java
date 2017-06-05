package com.abhishekveenakkat.ksrtcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhishekveenakkat.ksrtcapp.models.BusModel;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchList extends AppCompatActivity {
    private ListView lvBus;
    private String From;
    private String To;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        lvBus = (ListView)findViewById(R.id.listView1);
        Bundle bundle = getIntent().getExtras();
        From = bundle.getString("From");
        To = bundle.getString("To");
        String Type = bundle.getString("Type").trim();

        TextView s = (TextView) findViewById(R.id.source);
        TextView d = (TextView) findViewById(R.id.destination);

        s.setText(From);
        d.setText(To);

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String rspipaddress = sharedPref.getString("ip", "");
        String url;
        if(Type.equals("All")) {
            url = "http://"+rspipaddress+"/srnew.php?s=\""+From+"\"&d=\""+To+"\"";
        }else {
            String t = Type.toString();
            url = "http://" + rspipaddress + "/srnewtwo.php?s=\"" + From + "\"&d=\"" + To + "\"&t=\""+t+"\"";
        }
        String newurl = url.replaceAll(" ","%20");

        new JSONtask().execute(newurl);


    }
    public class JSONtask extends AsyncTask<String, String, List<BusModel>> {

        @Override
        protected List<BusModel> doInBackground(String... params) {

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
                JSONArray parentArray = parentObject.getJSONArray("movies");

                List<BusModel> busModelList = new ArrayList<>();
                // StringBuffer finalBufferedData = new StringBuffer();
                for(int i = 0;i<parentArray.length();i++){
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    BusModel busModel = new BusModel();
                    busModel.setRouteid(finalObject.getString("routeid"));
                    busModel.setTripcode(finalObject.getString("tripcode"));
                    busModel.setRoutename(finalObject.getString("routename"));
                    busModel.setDesc(finalObject.getString("description"));
                    busModel.setType(finalObject.getString("bustype"));
                    busModel.setUnitfare(finalObject.getInt("unitfare"));
                    busModel.setRating(finalObject.getInt("rating"));
                    busModel.setRatingcount(finalObject.getInt("ratingcount"));
                    busModel.setPrating(finalObject.getInt("prating"));
                    busModel.setSrating(finalObject.getInt("srating"));
                    busModel.setCrating(finalObject.getInt("crating"));
                    busModel.setStatus(finalObject.getString("status"));

                    busModel.setStime(finalObject.getString("time"));
                    busModel.setDtime(finalObject.getString("desttime"));
                    busModelList.add(busModel);
                }
                return busModelList;


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
        protected void onPostExecute(List<BusModel> result) {
            super.onPostExecute(result);
            if (result.size()==0) {
                //Toast.makeText(SearchList.this, "No bus with provided details", Toast.LENGTH_SHORT).show();
                Snackbar.make(lvBus, "No bus with provided details !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
                BusAdapter adapter = new BusAdapter(getApplicationContext(), R.layout.row, result);
                lvBus.setAdapter(adapter);


                lvBus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //String food = adapterView.getItemAtPosition(i).toString();
                        //Toast.makeText(SearchList.this,food,Toast.LENGTH_SHORT).show();
//                    Cursor mycursor = (Cursor) adapterView.getItemAtPosition(i);
//                    Toast.makeText(SearchList.this,mycursor.getCount() +"   ",Toast.LENGTH_SHORT).show();
                        TextView tv = (TextView) view.findViewById(R.id.rid);
                        TextView BusName = (TextView) view.findViewById(R.id.name);
                        TextView BusVia = (TextView) view.findViewById(R.id.via);
                        TextView BusType = (TextView) view.findViewById(R.id.type);
                        TextView Status = (TextView) view.findViewById(R.id.status);
                        TextView Stime = (TextView) view.findViewById(R.id.stime);
                        TextView Dtime = (TextView) view.findViewById(R.id.dtime);

                        String textvalue = tv.getText().toString();

                        //to display route id selected
                        //Toast.makeText(SearchList.this,"Route id : " + textvalue,Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SearchList.this, SingleBusInfo.class);
                        intent.putExtra("Bname", BusName.getText().toString());
                        intent.putExtra("Bvia", BusVia.getText().toString());
                        intent.putExtra("Btype", BusType.getText().toString());
                        intent.putExtra("Bsource", From);
                        intent.putExtra("Bdest", To);
                        intent.putExtra("Routeid", tv.getText().toString());
                        intent.putExtra("Bstatus", Status.getText().toString());
                        intent.putExtra("Bstime", Stime.getText().toString());
                        intent.putExtra("Bdtime", Dtime.getText().toString());

                        startActivity(intent);


                    }
                });
            }

    }
    public class BusAdapter extends ArrayAdapter{
        private List<BusModel> busModelList;
        private int resource;
        private LayoutInflater inflater;
        public BusAdapter(Context context, int resource, List<BusModel> objects) {
            super(context, resource, objects);
            busModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.row, null);
            }

            TextView starttime;
            TextView endtime;
            TextView name;
            TextView desc;
            TextView type;
            TextView charge;
            TextView rat;
            TextView rid;
            TextView crat;
            TextView srat;
            TextView rcount;
            TextView prat;
            TextView status;

            crat = (TextView) convertView.findViewById(R.id.crating);
            srat = (TextView) convertView.findViewById(R.id.srating);
            prat = (TextView) convertView.findViewById(R.id.prating);
            rcount = (TextView) convertView.findViewById(R.id.rcount);
            status = (TextView) convertView.findViewById(R.id.status);

            rid = (TextView) convertView.findViewById(R.id.rid);
            starttime = (TextView) convertView.findViewById(R.id.stime);
            endtime = (TextView) convertView.findViewById(R.id.dtime);
            name= (TextView) convertView.findViewById(R.id.name);
            desc= (TextView) convertView.findViewById(R.id.via);
            type = (TextView)convertView.findViewById(R.id.type);
            charge = (TextView)convertView.findViewById(R.id.money);
            rat = (TextView) convertView.findViewById(R.id.rat);

            rcount.setText(busModelList.get(position).getRatingcount()+"");
            prat.setText(busModelList.get(position).getPrating()+"");
            crat.setText(busModelList.get(position).getCrating()+"");
            srat.setText(busModelList.get(position).getSrating()+"");

            int rc = busModelList.get(position).getRatingcount();
            int p = busModelList.get(position).getPrating();
            int s = busModelList.get(position).getSrating();
            int c = busModelList.get(position).getCrating();
            int x;
            if(rc==0) {
                x=0;
            }else {
                x = (c+p+s)/(3*rc);
            }



            status.setText(busModelList.get(position).getStatus());

            rid.setText(busModelList.get(position).getRouteid());
            starttime.setText(busModelList.get(position).getStime());
            endtime.setText(busModelList.get(position).getDtime());
            name.setText(busModelList.get(position).getRoutename());
            desc.setText(busModelList.get(position).getDesc());
            type.setText(busModelList.get(position).getType());
            int j= busModelList.get(position).getUnitfare();
            charge.setText("Rs. "+j);
            rat.setText("Rating "+x+"/5");
            return convertView;
        }

    }

}