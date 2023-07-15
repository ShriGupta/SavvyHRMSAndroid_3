package com.savvy.hrmsnewapp.retrofit;

import android.provider.ContactsContract;

import com.google.gson.JsonObject;
import com.savvy.hrmsnewapp.fragment.CompanyDirectory.model.CompanyDirectoryModel;
import com.savvy.hrmsnewapp.retrofitModel.EmployeeProfilePostDynamicResult;
import com.savvy.hrmsnewapp.retrofitModel.MenuModule;
import com.savvy.hrmsnewapp.retrofitModel.ProfileDataModel;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET(URLConstant.URL_MENU_DATA_REQUEST + "{empid}/{auth-token}")
    Call<List<MenuModule>> getMenuList(
            @Path("empid") String epid,
            @Path("auth-token") String authToken);

    @Headers({"Accept:application/json", "Content-Type:application/json;"})
    @POST(URLConstant.URL_PROFILE_DATA_REQUEST)
    Call<List<ProfileDataModel>> getProfileData(
            @Header("securityToken") String token,
            @Body JSONObject jsonObject);

    @GET(URLConstant.URL_SERVER_DATE_TIME_REQUEST)
    Call<ServerDateTimeModel> getServerDateTime(
            @Header("auth-token") String authToken);

    @GET(URLConstant.URL_SERVER_DATE_TIME_WITH_BUTTON+"/{empId}")
    Call<ServerDateTimeModel> getServerDateTimeWithEnableButton(
            @Header("auth-token") String authToken,
            @Path("empId") String empId);



    @GET(URLConstant.URL_SHORT_LEAVE_APPROVAL_URL)
    Call<ServerDateTimeModel> getSLApprovalData(
            @Header("auth-token") String authToken,
            @Path(value="empId") String name
            );


    @Headers({"Accept:application/json", "Content-Type:application/json;"})
    @POST(URLConstant.URL_COMPANY_DIRECTORY_REQUEST)
    Call<CompanyDirectoryModel> getCompanyDirectoryList(
            @Header("securityToken") String token,
            @Body JsonObject jsonObject);
}
