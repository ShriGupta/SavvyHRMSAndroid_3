package com.savvy.hrmsnewapp.fragment.CompanyDirectory;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;
import static com.savvy.hrmsnewapp.activity.DashBoardActivity.MY_PREFS_NAME;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.EmployeeDynamicProfileAdapter;
import com.savvy.hrmsnewapp.fragment.CompanyDirectory.model.CompanyDirectoryModel;
import com.savvy.hrmsnewapp.fragment.CompanyDirectory.model.MyHierarchyBasedOnRolePostResult;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyDirectoryFragment extends Fragment implements ItemClickListener {
    public static final String TAG = "savvyhrmslog";

    SharedPreferences shared;
    String employeeId;
    RecyclerView recyclerView;
    Boolean isLoading=false;
    List<MyHierarchyBasedOnRolePostResult> list=new ArrayList<>();
    int PageNo=1;
    CompanyDirectryAdapter adapter;

    ArrayList<HashMap<String, String>> dynamicArraylist;
    EmployeeDynamicProfileAdapter profileAdapter;
    String PHOTO_CODE="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        adapter=new CompanyDirectryAdapter(list,requireActivity(),this);
        dynamicArraylist=new ArrayList<>();
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

    private void showEmpDetailDialog(ArrayList<HashMap<String, String>> dynamicArraylist){

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.emp_detail_dialog, null, false);
        RecyclerView list=dialogView.findViewById(R.id.rv_emp_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));
        ImageView imageview=dialogView.findViewById(R.id.iv_profile);
        if (PHOTO_CODE.equals("")) {
            imageview.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.get().load(PHOTO_CODE).error(R.drawable.profile_image).into(imageview);
        }
        profileAdapter = new EmployeeDynamicProfileAdapter(requireActivity(), dynamicArraylist);
        list.setAdapter(profileAdapter);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }


    @Override
    public void onClickItem(int position, String data) {
        String empCode;
        dynamicArraylist.clear();
        if(data.contains("@")){
            String[] result=data.split("@");
            if(result.length>1){
                empCode=result[0];
                PHOTO_CODE=result[1];
            }else {
                empCode=result[0];
                PHOTO_CODE="";
            }
            getEmployeeDetail(empCode);

        }
    }
    private void getEmployeeDetail(String emp_id) {
        if (Utilities.isNetworkAvailable(requireActivity())) {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/EmployeeDetailsPostDynamic";
            JSONObject params_final = new JSONObject();

            try {
                params_final.put("employeeId", emp_id);
                params_final.put("securityToken", shared.getString("Token", ""));
            } catch (JSONException e) {
                Log.e("TAG", "getEmployeeDetail: ",e );
            }
            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    response -> {
                        HashMap<String, String> hashMap;
                        try {
                            JSONArray jarray = response.getJSONArray("EmployeeDetailsPostDynamicResult");

                            if (jarray.length() > 0) {
                                for (int i = 0; i < jarray.length(); i++) {
                                    hashMap = new HashMap<>();
                                    hashMap.put("CaptionKey", jarray.getJSONObject(i).getString("CaptionName"));
                                    hashMap.put("CaptionValue", jarray.getJSONObject(i).getString("CaptionValue"));
                                    dynamicArraylist.add(hashMap);
                                }
                                showEmpDetailDialog(dynamicArraylist);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.e("Error In", "" + ex.getMessage());
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d("Error", "" + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", shared.getString("EMPLOYEE_ID_FINAL", ""));
                    return params;
                }


                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
        }

    }
}
