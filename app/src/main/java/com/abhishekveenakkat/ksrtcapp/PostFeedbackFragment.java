package com.abhishekveenakkat.ksrtcapp;

/**
 * Created by Abhishek Veenakkat on 14-02-2017.
 */

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

public class PostFeedbackFragment extends DialogFragment {
    Button btn;
    OnItemSelectedListener onItemSelectedListener;
    Button send;
    RatingBar c,s,p;
    int a,b,x;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView=inflater.inflate(R.layout.fragment_postreview, null);
        //SET TITLE DIALOG TITLE
        getDialog().setTitle("Send Failure Report");
        //BUTTON,LISTVIEW,SEARCHVIEW INITIALIZATIONS
        btn=(Button) rootView.findViewById(R.id.close);
        c = (RatingBar) rootView.findViewById(R.id.ratingBar1);
        s = (RatingBar) rootView.findViewById(R.id.ratingBar2);
        p = (RatingBar) rootView.findViewById(R.id.ratingBar3);

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
                a = (int)c.getRating();
                b = (int)s.getRating();
                x = (int)p.getRating();
                 onItemSelectedListener.itemselected(a,b,x);
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
        public void itemselected(int r1,int r2,int r3);
    }

}