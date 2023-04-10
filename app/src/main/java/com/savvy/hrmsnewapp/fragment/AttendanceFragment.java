package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.attendanceMark.MarkAttendance;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.utils.Constants;

public class AttendanceFragment extends BaseFragment {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    CustomTextView txv_currentDate, txv_currentTime;
    Button btn_submit;
    EditText edt_messagefeedback;
    String token = "", empoyeeId = "", username = "";
    String latlongFlag = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coordinatorLayout = requireActivity().findViewById(R.id.coordinatorLayout);

        shared = requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);
        txv_currentDate = rootView.findViewById(R.id.txv_currentDate);
        txv_currentTime = rootView.findViewById(R.id.txv_currentTime);
        edt_messagefeedback = rootView.findViewById(R.id.edt_messagefeedback);
        btn_submit = rootView.findViewById(R.id.btn_submit);

        edt_messagefeedback.setVisibility(View.GONE);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(requireActivity(), MarkAttendance.class);
                Bundle bundle = new Bundle();
                bundle.putString("latlongFlag", latlongFlag);
                intent.putExtras(bundle);
                startActivity(intent);
                requireActivity().overridePendingTransition(0, 0);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentDateTime();
    }

    private void getCurrentDateTime() {
        APIServiceClass.getInstance().sendDateTimeRequest(shared.getString(Constants.EMPLOYEE_ID_FINAL, ""), new ResultHandler<ServerDateTimeModel>() {
            @Override
            public void onSuccess(ServerDateTimeModel data) {
                latlongFlag = data.getLatelongFlag();
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

