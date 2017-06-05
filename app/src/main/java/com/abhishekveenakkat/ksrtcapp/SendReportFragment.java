package com.abhishekveenakkat.ksrtcapp;

/**
 * Created by Abhishek Veenakkat on 05-02-2017.
 */

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class SendReportFragment extends DialogFragment {
    Button btn;
    OnItemSelectedListener onItemSelectedListener;
    Button send;
    RadioGroup rg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView=inflater.inflate(R.layout.fragment_send_report, null);
        //SET TITLE DIALOG TITLE
        getDialog().setTitle("Send Failure Report");
        //BUTTON,LISTVIEW,SEARCHVIEW INITIALIZATIONS
        rg = (RadioGroup) rootView.findViewById(R.id.rgroup);
        btn=(Button) rootView.findViewById(R.id.close);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                dismiss();
            }
        });
        send=(Button) rootView.findViewById(R.id.send);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int radioButtonID = rg.getCheckedRadioButtonId();
                View radioButton = rg.findViewById(radioButtonID);
                int idx = rg.indexOfChild(radioButton);

                if(idx==-1){

                    Toast.makeText(getActivity(),"Nothing Selected",Toast.LENGTH_SHORT).show();
                }else {
                    RadioButton r = (RadioButton) radioButton;
                    String selectedtext = r.getText().toString();

                    onItemSelectedListener.itemselected(selectedtext);
                    //Toast.makeText(getActivity(),idx + "Selected",Toast.LENGTH_SHORT).show();
                    dismiss();
                }

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
        public void itemselected(String failure);
    }

}