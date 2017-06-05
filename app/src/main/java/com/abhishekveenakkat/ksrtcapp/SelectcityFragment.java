package com.abhishekveenakkat.ksrtcapp;

/**
 * Created by Abhishek Veenakkat on 05-02-2017.
 */

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class SelectcityFragment extends DialogFragment {
    OnItemSelectedListener onItemSelectedListener;
    Button btn;
    ListView lv;
    SearchView sv;
    ArrayAdapter<CharSequence> adapter;

    //String[] cities={"Kozhikode","Edappal","Thrissur","Ernakulam","Kasargode","Kannur"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView=inflater.inflate(R.layout.cities, null);
        //SET TITLE DIALOG TITLE
        getDialog().setTitle("Cities");
        //BUTTON,LISTVIEW,SEARCHVIEW INITIALIZATIONS
        lv=(ListView) rootView.findViewById(R.id.listView1);
        sv=(SearchView) rootView.findViewById(R.id.searchView1);
        btn=(Button) rootView.findViewById(R.id.dismiss);
        //CREATE AND SET ADAPTER TO LISTVIEW
        //adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,cities);
        adapter=ArrayAdapter.createFromResource(getActivity(),R.array.cities,android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);


        //SEARCH
        sv.setQueryHint("Search...");
        sv.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String txt) {
                // TODO Auto-generated method stub
                return false;
            }
            @Override
            public boolean onQueryTextChange(String txt) {
                // TODO Auto-generated method stub
                adapter.getFilter().filter(txt);
                return false;
            }
        });
        //BUTTON
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                dismiss();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l){


                onItemSelectedListener.itemselected(adapterView.getItemAtPosition(i)+"");
                dismiss();
            }
        });




        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onItemSelectedListener=(OnItemSelectedListener)activity;
        }catch (Exception e){}

    }

    public interface OnItemSelectedListener{
        public void itemselected(String city_name);
    }
}