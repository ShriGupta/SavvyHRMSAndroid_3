package com.savvy.hrmsnewapp.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    public int id, notifyCount;

    public String USER_ID, PASSWORD, IPADDRESS, EmpoyeeId, EmpoyeeName, UserName, GroupId, AccessModules, EmpPhotoPath, localDataInsertInterval,
            serverDataUploadInterval, EmployeeCode, LastPasswordChange, regularExpressionAndroid, EMPLOYEE_ID_FINAL, TOKEN;
    public boolean LOGIN_STATUS;
    public static final String MY_PREFS_NAME = "SavyyHRMSUserData";


    public User(Context activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        USER_ID = prefs.getString("USER_ID", "");
        PASSWORD = prefs.getString("PASSWORD", "");
        IPADDRESS = prefs.getString("IPADDRESS", "");
        EmpoyeeId = prefs.getString("EmpoyeeId", "");
        EmpoyeeName = prefs.getString("EmpoyeeName", "");
        UserName = prefs.getString("UserName", "");
        GroupId = prefs.getString("GroupId", "");
        AccessModules = prefs.getString("AccessModules", "");
        EmpPhotoPath = prefs.getString("EmpPhotoPath", "");
        localDataInsertInterval = prefs.getString("localDataInsertInterval", "");
        serverDataUploadInterval = prefs.getString("serverDataUploadInterval", "");
        EmployeeCode = prefs.getString("EmployeeCode", "");
        LastPasswordChange = prefs.getString("LastPasswordChange", "");
        regularExpressionAndroid = prefs.getString("regularExpressionAndroid", "");
        LOGIN_STATUS = prefs.getBoolean("LOGIN_STATUS", false);
        EMPLOYEE_ID_FINAL = prefs.getString("EMPLOYEE_ID_FINAL", "");
        TOKEN = prefs.getString("TOKEN", "");

    }

    public void update(Context activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("USER_ID", USER_ID);
        editor.putString("PASSWORD", PASSWORD);
        editor.putString("IPADDRESS", IPADDRESS);
        editor.putString("EmpoyeeId", EmpoyeeId);
        editor.putString("EmpoyeeName", EmpoyeeName);
        editor.putString("UserName", UserName);
        editor.putString("GroupId", GroupId);
        editor.putString("AccessModules", AccessModules);
        editor.putString("EmpPhotoPath", EmpPhotoPath);
        editor.putString("localDataInsertInterval", localDataInsertInterval);
        editor.putString("serverDataUploadInterval", serverDataUploadInterval);
        editor.putString("EmployeeCode", EmployeeCode);
        editor.putString("LastPasswordChange", LastPasswordChange);
        editor.putString("regularExpressionAndroid", regularExpressionAndroid);
        editor.putBoolean("LOGIN_STATUS", LOGIN_STATUS);
        editor.putString("EMPLOYEE_ID_FINAL", EMPLOYEE_ID_FINAL);
        editor.putString("TOKEN", TOKEN);
        editor.apply();
    }

    public void remove(Context activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("USER_ID", "");
        editor.putString("PASSWORD", "");
        editor.putString("IPADDRESS", "");
        editor.putString("EmpoyeeId", "");
        editor.putString("EmpoyeeName", "");
        editor.putString("UserName", "");
        editor.putString("GroupId", "");
        editor.putString("AccessModules", "");
        editor.putString("EmpPhotoPath", "");
        editor.putString("localDataInsertInterval", "");
        editor.putString("serverDataUploadInterval", "");
        editor.putString("EmployeeCode", "");
        editor.putString("LastPasswordChange", "");
        editor.putString("regularExpressionAndroid", "");
        editor.putBoolean("LOGIN_STATUS", false);
        editor.putString("EMPLOYEE_ID_FINAL", "");
        editor.putString("TOKEN", "");
        editor.apply();
    }
}
