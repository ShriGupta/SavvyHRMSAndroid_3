package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.attendanceMark.MarkTeamAttendanceNew;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.utils.Constants;

public class MarkTeamAttendance extends BaseFragment implements View.OnClickListener {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    CustomTextView txv_currentDate, txv_currentTime;
    Button btn_submit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        getCurrentDateTime();
    }

    private void getCurrentDateTime() {
        APIServiceClass.getInstance().sendDateTimeRequest(shared.getString(Constants.EMPLOYEE_ID_FINAL, ""), new ResultHandler<ServerDateTimeModel>() {
            @Override
            public void onSuccess(ServerDateTimeModel data) {
                Log.e("TAG", "onSuccess: " + data);

                String[] serverDateSplit = data.getServerDateDDMMYYYYY().split(" ");
                String replacecurrDate = serverDateSplit[0].replace("\\", "");
                txv_currentTime.setText(data.getServerTime());
                txv_currentDate.setText(replacecurrDate);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mark_team_attendance, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        txv_currentDate = rootView.findViewById(R.id.txv_currentDate);
        txv_currentTime = rootView.findViewById(R.id.txv_currentTime);
        btn_submit = rootView.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            requireActivity().startActivity(new Intent(getActivity(), MarkTeamAttendanceNew.class));
            requireActivity().overridePendingTransition(0, 0);
        }
    }
}
