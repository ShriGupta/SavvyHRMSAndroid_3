package com.savvy.hrmsnewapp.saleForce;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.BaseFragment;

public class Activity_Notes_Contact_Holder extends BaseFragment implements View.OnClickListener {

    CustomTextView visit_activity, visit_contact, visit_notes, undoActivity;
//    ImageView visit_activity_line, visit_contact_line, visit_notes_line;
    LinearLayout visit_activity_linear, visit_contact_linear, visit_notes_linear, undoActivityLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity__notes__contact__holder,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        visit_activity = getActivity().findViewById(R.id.visit_activity);
        visit_contact = getActivity().findViewById(R.id.visit_contact);
        visit_notes = getActivity().findViewById(R.id.visit_notes);
        undoActivity = getActivity().findViewById(R.id.undoActivity);

//        visit_activity_line = (ImageView)getActivity().findViewById(R.id.visit_activity_line1);
//        visit_contact_line = (ImageView)getActivity().findViewById(R.id.visit_contact_line);
//        visit_notes_line = (ImageView)getActivity().findViewById(R.id.visit_notes_line);

        visit_activity_linear = getActivity().findViewById(R.id.visit_activity_linear);
        visit_contact_linear = getActivity().findViewById(R.id.visit_contact_linear);
        visit_notes_linear = getActivity().findViewById(R.id.visit_notes_linear);
        undoActivityLayout = getActivity().findViewById(R.id.undoActivityLayout);

        visit_activity_linear.setOnClickListener(this);
        visit_contact_linear.setOnClickListener(this);
        visit_notes_linear.setOnClickListener(this);
        undoActivityLayout.setOnClickListener(this);

        try {

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            sale_activity saleActivity = new sale_activity();
            transaction.replace(R.id.frame_activity_notes, saleActivity);
            transaction.addToBackStack(null);
            transaction.commit();

            visit_activity.setTextColor(Color.parseColor("#277ddb"));
            visit_contact.setTextColor(Color.parseColor("#ffffff"));
            visit_notes.setTextColor(Color.parseColor("#ffffff"));

            visit_activity.setBackgroundColor(Color.parseColor("#ffffff"));
            visit_contact.setBackgroundColor(Color.parseColor("#277ddb"));
            visit_notes.setBackgroundColor(Color.parseColor("#277ddb"));


//            visit_activity_line.setVisibility(View.VISIBLE);
//            visit_contact_line.setVisibility(View.GONE);
//            visit_notes_line.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        if(v.getId()==R.id.undoActivityLayout){

            AllCustomerDetail saleActivity = new AllCustomerDetail();
            transaction.replace(R.id.container_body, saleActivity);
            transaction.addToBackStack(null);
            transaction.commit();

//            visit_activity.setTextColor(Color.parseColor("#277ddb"));
//            visit_contact.setTextColor(Color.parseColor("#ffffff"));
//            visit_notes.setTextColor(Color.parseColor("#ffffff"));
//
//            visit_activity.setBackgroundColor(Color.parseColor("#ffffff"));
//            visit_contact.setBackgroundColor(Color.parseColor("#277ddb"));
//            visit_notes.setBackgroundColor(Color.parseColor("#277ddb"));
//            visit_activity_line.setVisibility(View.VISIBLE);
//            visit_contact_line.setVisibility(View.GONE);
//            visit_notes_line.setVisibility(View.GONE);
        }

        if(v.getId()==R.id.visit_activity_linear){

            sale_activity saleActivity = new sale_activity();
            transaction.replace(R.id.frame_activity_notes, saleActivity);
            transaction.addToBackStack(null);
            transaction.commit();

            visit_activity.setTextColor(Color.parseColor("#277ddb"));
            visit_contact.setTextColor(Color.parseColor("#ffffff"));
            visit_notes.setTextColor(Color.parseColor("#ffffff"));

            visit_activity.setBackgroundColor(Color.parseColor("#ffffff"));
            visit_contact.setBackgroundColor(Color.parseColor("#277ddb"));
            visit_notes.setBackgroundColor(Color.parseColor("#277ddb"));
//            visit_activity_line.setVisibility(View.VISIBLE);
//            visit_contact_line.setVisibility(View.GONE);
//            visit_notes_line.setVisibility(View.GONE);
        }

        if(v.getId()==R.id.visit_contact_linear){
            Sale_Contact_Activity sale_contact_activity = new Sale_Contact_Activity();
            transaction.replace(R.id.frame_activity_notes, sale_contact_activity);
            transaction.addToBackStack(null);
            transaction.commit();

            visit_activity.setTextColor(Color.parseColor("#ffffff"));
            visit_contact.setTextColor(Color.parseColor("#277ddb"));
            visit_notes.setTextColor(Color.parseColor("#ffffff"));

            visit_activity.setBackgroundColor(Color.parseColor("#277ddb"));
            visit_contact.setBackgroundColor(Color.parseColor("#ffffff"));
            visit_notes.setBackgroundColor(Color.parseColor("#277ddb"));
//
//            visit_activity_line.setVisibility(View.GONE);
//            visit_contact_line.setVisibility(View.VISIBLE);
//            visit_notes_line.setVisibility(View.GONE);
        }

        if(v.getId()==R.id.visit_notes_linear){
            Sale_notes_activity sale_notes_activity = new Sale_notes_activity();
            transaction.replace(R.id.frame_activity_notes, sale_notes_activity);
            transaction.addToBackStack(null);
            transaction.commit();

            visit_activity.setTextColor(Color.parseColor("#ffffff"));
            visit_contact.setTextColor(Color.parseColor("#ffffff"));
            visit_notes.setTextColor(Color.parseColor("#277ddb"));

            visit_activity.setBackgroundColor(Color.parseColor("#277ddb"));
            visit_contact.setBackgroundColor(Color.parseColor("#277ddb"));
            visit_notes.setBackgroundColor(Color.parseColor("#ffffff"));

//            visit_activity_line.setVisibility(View.GONE);
//            visit_contact_line.setVisibility(View.GONE);
//            visit_notes_line.setVisibility(View.VISIBLE);
        }
    }
}
