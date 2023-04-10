package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;

/**
 * Created by Hari Om on 5/10/2017.
 */
public class LeaveApplyFragment  extends BaseFragment {
    SharedPreferences shared;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "",empoyeeId = "";
    public LeaveApplyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));

        //getFinancialYear();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Toast.makeText(getActivity(), "On Create View Called ", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_leave_apply, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        //Toast.makeText(getActivity(), "onActivityCreated Called ", Toast.LENGTH_SHORT).show();
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
       /* txv_YearValue           = (CustomTextView)view.findViewById(R.id.txv_YearValue);
        txv_financ_year_value       = (CustomTextView)view.findViewById(R.id.txv_financ_year_value);

        recyclerView        = (RecyclerView)getActivity().findViewById(R.id.leaveBalanceList);
        mAdapter = new LeaveBalanceListAdapter(getActivity(),arlData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());*/

      /*  syllabusList    = (RecyclerView)view.findViewById(R.id.syllabusList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        syllabusList.setLayoutManager(layoutManager);

        if (Utilities.isNetworkAvailable(getActivity())) {
            new SyllabusDataAsyncTask(getActivity()).execute(URLConstants.dash_syllabus);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }*/
    }
}
