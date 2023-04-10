package com.savvy.hrmsnewapp.fragment;

import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.savvy.hrmsnewapp.BuildConfig;
import com.savvy.hrmsnewapp.attendanceMark.MarkAttendanceInOut;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.rahul.AddressDetails;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.service.TrackGPS;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.Objects;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;
import static com.savvy.hrmsnewapp.fragment.TravelRequestFicciFragment.MY_PREFS_NAME;

public class SaveMarkAttendanceWithInOut extends Fragment {
    CustomTextView txv_currentDate, txv_currentTime;
    Button btn_submit;
    EditText edt_messagefeedback;
    CoordinatorLayout coordinatorLayout;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
      //  getCurrentDateTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_save_mark_attendance_with_in_out, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = requireActivity().findViewById(R.id.coordinatorLayout);
        txv_currentDate = requireActivity().findViewById(R.id.txv_currentDate);
        txv_currentTime = requireActivity().findViewById(R.id.txv_currentTime);
        edt_messagefeedback = requireActivity().findViewById(R.id.edt_messagefeedback);
        btn_submit = requireActivity().findViewById(R.id.btn_submit);
        edt_messagefeedback.setVisibility(View.GONE);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentDateTime();
                requireActivity().startActivity(new Intent(requireActivity(), MarkAttendanceInOut.class));
            }
        });
    }

    private void getCurrentDateTime() {

        APIServiceClass.getInstance().sendDateTimeRequest(shared.getString(Constants.EMPLOYEE_ID_FINAL, ""), new ResultHandler<ServerDateTimeModel>() {
            @Override
            public void onSuccess(ServerDateTimeModel data) {
                Log.e(TAG, "onSuccess: " + data);
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

}
