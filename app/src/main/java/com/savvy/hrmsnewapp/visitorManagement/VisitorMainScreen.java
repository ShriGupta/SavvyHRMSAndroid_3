package com.savvy.hrmsnewapp.visitorManagement;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.savvy.hrmsnewapp.R;

public class VisitorMainScreen extends Fragment {

    Button btn_scheduled, btn_nonScheduled;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitor_main_screen,container,false);

        btn_scheduled = view.findViewById(R.id.btn_scheduled);
        btn_nonScheduled = view.findViewById(R.id.btn_nonScheduled);

        btn_scheduled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Scheduled Visitor", Toast.LENGTH_SHORT).show();
            }
        });

        btn_nonScheduled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Non-Scheduled Visitor", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
