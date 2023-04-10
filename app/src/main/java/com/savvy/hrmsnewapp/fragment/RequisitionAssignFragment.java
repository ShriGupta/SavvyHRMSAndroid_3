package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;

public class RequisitionAssignFragment extends Fragment {
    Button btnDate;
    CalanderHRMS calanderHRMS;
    public static Handler handler;
    public static Runnable runnable;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calanderHRMS = new CalanderHRMS(getActivity());
        btnDate = getActivity().findViewById(R.id.btn_assign_date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDate.setText("");
                calanderHRMS.datePicker(btnDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btnDate.getText().toString().trim();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });
    }
}
