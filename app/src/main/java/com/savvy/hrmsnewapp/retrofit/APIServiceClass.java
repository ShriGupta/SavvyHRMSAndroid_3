package com.savvy.hrmsnewapp.retrofit;

import com.savvy.hrmsnewapp.retrofitModel.EmployeeProfilePostDynamicResult;
import com.savvy.hrmsnewapp.retrofitModel.MenuModule;
import com.savvy.hrmsnewapp.retrofitModel.ProfileDataModel;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIServiceClass {
    private static APIServiceClass mInstance;

    public static synchronized APIServiceClass getInstance() {
        if (mInstance == null) {
            mInstance = new APIServiceClass();
        }
        return mInstance;
    }

    public void sendMenuDataRequest(String employeeid,String authToken,ResultHandler<List<MenuModule>> handler) {

        Call<List<MenuModule>> call=RetrofitClient
                .getInstance()
                .getApi()
                .getMenuList(employeeid,authToken);
        call.enqueue(new Callback<List<MenuModule>>() {
            @Override
            public void onResponse(Call<List<MenuModule>> call, Response<List<MenuModule>> response) {
                if (response.isSuccessful()) {
                    handler.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<MenuModule>> call, Throwable t) {
                handler.onFailure(t.getLocalizedMessage());
            }
        });
    }


    public void sendProfileDataRequest(String securityToken,JSONObject jsonObject, ResultHandler<List<ProfileDataModel> > handler) {

        Call<List<ProfileDataModel> > call=RetrofitClient
                .getInstance()
                .getApi()
                .getProfileData(securityToken,jsonObject);
        call.enqueue(new Callback<List<ProfileDataModel> >() {
            @Override
            public void onResponse(Call<List<ProfileDataModel>> call, Response<List<ProfileDataModel>> response) {
                if (response.isSuccessful()) {
                    handler.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ProfileDataModel> > call, Throwable t) {
                handler.onFailure(t.getLocalizedMessage());
            }
        });

    }

    public void sendDateTimeRequest(String authToken,ResultHandler<ServerDateTimeModel> handler) {

        Call<ServerDateTimeModel> call=RetrofitClient
                .getInstance()
                .getApi()
                .getServerDateTime(authToken);
        call.enqueue(new Callback<ServerDateTimeModel>() {
            @Override
            public void onResponse(Call<ServerDateTimeModel> call, Response<ServerDateTimeModel> response) {
                if (response.isSuccessful()) {
                    handler.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ServerDateTimeModel> call, Throwable t) {
                handler.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public void getSLApprovalData(String authToken,String empId,ResultHandler<ServerDateTimeModel> handler) {

        Call<ServerDateTimeModel> call=RetrofitClient
                .getInstance()
                .getApi()
                .getSLApprovalData(authToken,empId);
        call.enqueue(new Callback<ServerDateTimeModel>() {
            @Override
            public void onResponse(Call<ServerDateTimeModel> call, Response<ServerDateTimeModel> response) {
                if (response.isSuccessful()) {
                    handler.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ServerDateTimeModel> call, Throwable t) {
                handler.onFailure(t.getLocalizedMessage());
            }
        });
    }

}




