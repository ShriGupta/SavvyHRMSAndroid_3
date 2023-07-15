package com.savvy.hrmsnewapp.fragment.CompanyDirectory;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.DashBoardActivity.MY_PREFS_NAME;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.fragment.CompanyDirectory.model.CompanyDirectoryModel;
import com.savvy.hrmsnewapp.fragment.CompanyDirectory.model.MyHierarchyBasedOnRolePostResult;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class CompanyDirectoryFragment extends Fragment {
    public static final String TAG = "savvyhrmslog";

    SharedPreferences shared;
    String employeeId;
    RecyclerView recyclerView;
    Boolean isLoading=false;
    List<MyHierarchyBasedOnRolePostResult> list=new ArrayList<>();
    int PageNo=1;
    CompanyDirectryAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        adapter=new CompanyDirectryAdapter(list,requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_company_directory, container, false);
        recyclerView=view.findViewById(R.id.rv_company_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(Utilities.isNetworkAvailable(requireActivity())){
            getDirectoryData(PageNo,"");
        }else {
            Toast.makeText(requireActivity(), "Please check your internet.", Toast.LENGTH_SHORT).show();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == list.size() - 1) {
                        //bottom of list!
                        getDirectoryData(PageNo,"");
                        isLoading = true;
                    }
                }
            }
        });

    }

    private void getDirectoryData(int pageNo,String empSearch) {
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("employeeId",employeeId);
        jsonObject.addProperty("pageno",pageNo);
        jsonObject.addProperty("securityToken",shared.getString("Token", ""));
        jsonObject.addProperty("empsearch",empSearch);
        APIServiceClass.getInstance().getCompanyDirectoryList(shared.getString(Constants.EMPLOYEE_ID_FINAL, ""),jsonObject ,new ResultHandler<CompanyDirectoryModel>() {
            @Override
            public void onSuccess(CompanyDirectoryModel data) {
                if(data.getMyHierarchyBasedOnRolePostResult()!=null && data.getMyHierarchyBasedOnRolePostResult().size()>0){
                    list.addAll(data.getMyHierarchyBasedOnRolePostResult());
                    adapter.notifyDataSetChanged();
                    isLoading=false;
                    PageNo++;
                }
            }

            @Override
            public void onFailure(String message) {
            }
        });
    }


}
