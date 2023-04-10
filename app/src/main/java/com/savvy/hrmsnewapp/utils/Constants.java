package com.savvy.hrmsnewapp.utils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hari Om on 1/20/2017.
 */
public class Constants {

    public static final String INTENT_EXTRA_LIMIT = "INTENT_EXTRA_LIMIT";
    public static int DEFAULT_TIME_OUT = 15000; // 10 Seconds

    public static String LOGIN_STATUS = "LOGIN_STATUS";
    public static String DASHBOARD_STATUS = "DASHBOARD_STATUS";
    public static String DEVICE_TOKEN = "";
    public static String IP_ADDRESS = "";
    public static boolean IP_ADDRESS_STATUS = false;
    public static String EMPLOYEE_ID_FINAL = "EMPLOYEE_ID_FINAL";
    public static String TOKEN = "TOKEN";
    public static String LastPasswordChange = "LastPasswordChange";
    public static String regularExpressionAndroid = "regularExpressionAndroid";
    public static String GPS_MENDATORY = "GPS_MENDATORY";


    public static long res = 123456789134567L;

    public static final String CHANNEL_ID = "my_channel_01";
    public static final String CHANNEL_NAME = "SavvyHRMS_Notifications";
    public static final String CHANNEL_DESCRIPTION = "www.orasis.in";

    public static int MY_TEAM_MEMBERS = 0;

    public static ArrayList<HashMap<String, String>> My_Team_Members = new ArrayList<HashMap<String, String>>();

    //    public static String COMPANY_STATUS = "OKAYA";
    public static String COMPANY_STATUS = "SNG";

    public static String COMPANY_STATUS_PHOTO_CODE = "ORASIS";
//    public static String COMPANY_STATUS_PHOTO_CODE = "GENERAL";

    public static String PHOTO_CODE_IP_ADDRESS = "savvyhrms.com";

    public static int LEAVE_APPLY_ACTIVITY = 1;
    public static String VOUCHER_DETAIL = "1";

    public static int Birthday_Count = 0;
    public static int Anniversary_Count = 0;
    public static int Join_Anniversary_Count = 0;
    public static int Announcement_Count = 0;
    public static int Thought_Count = 0;
    public static int Holiday_Count = 0;

    public static String COMPARE_DATE = "";
    public static boolean COMPARE_DATE_API = true;

    public static boolean NO_NETWORK = false;

    public static boolean COMPARE_DATE_MAIN_TRAVEL = true;
    public static boolean COMPARE_DATE_START_TRAVEL = true;
    public static boolean COMPARE_DATE_BOARDING_TRAVEL = true;
    public static boolean COMPARE_DATE_ARRIVAL_TRAVEL = true;

    public static int TRACK_ME_START_SERVICE = 1;

//    public static String CONVEYANCE_XMLDATA       =   "";

    public static String EXPENSE_XMLDATA = "";

    public static String TRAVEL_XMLDATA = "";

    public static String TRAVEL_START_DATE = "";
    public static String TRAVEL_END_DATE = "";

    public static RequestQueue requestQueue = null;
    public static JsonObjectRequest jsonObjectRequest = null;

    public static String CONVEYANCE_REQUEST_ID = "";
    public static boolean CONVEYANCE_REQUEST_ID_STATUS = false;

    public static String EXPENSE_REQUEST_ID = "";
    public static boolean EXPENSE_REQUEST_ID_STATUS = false;

    public static String CONVEYANCE_APPROVED_AMOUNT = "";

    public static String EXPENSE_APPROVED_AMOUNT = "";

    public static HashMap<Integer, String> hashMap1 = new HashMap<Integer, String>();

    public static HashMap<Integer, String> hashMap2 = new HashMap<Integer, String>();

    public static HashMap<Integer, String> CONVEYANCE_SPINEER = new HashMap<Integer, String>();

    public static HashMap<Integer, String> EXPENSE_SPINNER = new HashMap<Integer, String>();

    public static HashMap<Integer, HashMap<String, String>> CONVEYANCE_EDIT = new HashMap<Integer, HashMap<String, String>>();

    public static String getDeviceToken() {
        return DEVICE_TOKEN;
    }

    public static void setDeviceToken(String deviceToken) {
        DEVICE_TOKEN = deviceToken;
    }

    public static String getIpAddress() {
        return IP_ADDRESS;
    }

    public static boolean getIpAddresssTATUS() {
        return IP_ADDRESS_STATUS;
    }

    public static void setIpAddress(String ipAddress) {
        IP_ADDRESS = ipAddress;
    }

    public static void setIpAddresssTATUS(boolean ipAddressStatus) {
        IP_ADDRESS_STATUS = ipAddressStatus;
    }

    //Customer ID change
    public static String CUSTOMER_ID = "";

    //Travel Boarding and Arrival Values
    public static String TRAVEL_BOARDING_ID = "";

    public static String TRAVEL_BOARDING_NAME = "";

    public static String TRAVEL_ARRIVING_ID = "";

    public static String TRAVEL_ARRIVING_NAME = "";

    public static String EMPLOYEE_CODE = "";

    public static final String URL_TRACK_ME_REQUEST = "SaveTrackMePostInBulk";

    public static boolean checkTrackingStatus;

    public static String REFRESH_LIST = "";

    public static String FACE_TYPE = "";
}
