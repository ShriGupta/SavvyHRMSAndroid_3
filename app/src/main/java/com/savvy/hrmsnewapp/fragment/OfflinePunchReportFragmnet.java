package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.OfflinePunchAdapter;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.OfflinePunchInModel;

import java.util.List;

public class OfflinePunchReportFragmnet extends Fragment {

    RecyclerView rvOfflinePunchRecyclerView;
    OfflinePunchAdapter offlinePunchAdapter;
    TextView tvNoData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_offline_punch_report, container, false);

        rvOfflinePunchRecyclerView = view.findViewById(R.id.rv_offlinePunch_recyclerview);
        tvNoData = view.findViewById(R.id.tv_no_data);
        rvOfflinePunchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        offlinePunchAdapter = new OfflinePunchAdapter();
        rvOfflinePunchRecyclerView.setAdapter(offlinePunchAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getOffinlePunchData();
    }

    private void getOffinlePunchData() {

        android.os.AsyncTask.execute(() -> {
            List<OfflinePunchInModel> list = DatabaseClient.getInstance(requireActivity()).getAppDatabase().passengerDao().getAllOfflinePunch();
            if (list.size() > 0) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        offlinePunchAdapter.addItems(list);
                    }
                });

            } else {
                rvOfflinePunchRecyclerView.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
        });
    }
}
