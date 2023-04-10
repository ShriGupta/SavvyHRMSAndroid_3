package com.savvy.hrmsnewapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import androidx.annotation.RequiresApi;

/**
 * Created by orapc7 on 15/11/2017.
 */

public class Db_Helper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SavvyHrms.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "TRACK_ME";

    private static final String KEY_TRACK_ID = "_id";
    private static final String KEY_TRACK_EMPLOYEE_ID = "emp_id";
    private static final String KEY_NETWORK_STATUS = "net_status";
    private static final String KEY_IS_SYNC = "is_sync";
    private static final String KEY_DATE = "date";
    private static final String KEY_FIELD2 = "FIELD2";
    private static final String KEY_FIELD3 = "FIELD3";
    private static final String KEY_FIELD4 = "FIELD4";
    private static final String KEY_FIELD5 = "FIELD5";


    public static final String TableName = "AddPassenger";
    public static final String PASSENGER_ID = "id";
    public static final String FIRSTNAME = "firstname";
    public static final String MIDDLE_NAME = "middlename";
    public static final String LASTNAME = "lastname";
    public static final String CONTACT_NUMBER = "contact";
    public static final String ADDRESS = "address";
    public static final String AGE = "age";
    public static final String GENDER = "gender";


    private String CREATE_TRACK_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_TRACK_ID + " INTEGER AUTOINCREMENT PRIMARY KEY," + KEY_TRACK_EMPLOYEE_ID + " TEXT," + KEY_NETWORK_STATUS + " TEXT," + KEY_IS_SYNC + " TEXT,"
            + KEY_DATE + " TEXT," + KEY_FIELD2 + " TEXT," + KEY_FIELD3 + " TEXT," + KEY_FIELD4 + " TEXT,"
            + KEY_FIELD5 + " TEXT" + ")";

   /* private String CREATE_PASSENGER_TABLE = "CREATE TABLE " + TableName + " ("
            + PASSENGER_ID + " INTEGER PRIMARY KEY, " +
            FIRSTNAME + " TEXT, " +
            MIDDLE_NAME + " TEXT, " +
            LASTNAME + " TEXT, " +
            CONTACT_NUMBER + " TEXT," +
            ADDRESS + " TEXT," +
            AGE + " INTEGER," +
            GENDER + " TEXT" +
            ")";*/

    public Db_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TRACK_TABLE);
          //  db.execSQL(CREATE_PASSENGER_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        // Creating tables again
        onCreate(db);
    }

    public boolean insertData(String empId, String netStatus, String date) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TRACK_EMPLOYEE_ID, empId);
        contentValues.put(KEY_NETWORK_STATUS, netStatus);
        contentValues.put(KEY_DATE, date);
        contentValues.put(KEY_IS_SYNC, "N");

        long res = db.insert(TABLE_NAME, null, contentValues);
        return res != -1;
    }

    /*public boolean insertData(PassengerModel passengerModel) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues passengerValue = new ContentValues();
        passengerValue.put(FIRSTNAME, passengerModel.getFirstname());
        passengerValue.put(MIDDLE_NAME, passengerModel.getMiddlename());
        passengerValue.put(LASTNAME, passengerModel.getLastname());
        passengerValue.put(CONTACT_NUMBER, passengerModel.getContctnumber());
        passengerValue.put(ADDRESS, passengerModel.getAddress());
        passengerValue.put(AGE, passengerModel.getAge());
        passengerValue.put(GENDER, passengerModel.getGender());

        long res = db.insert(TableName, null, passengerValue);
        return res != -1;
    }*/

    public void DefaultinsertData() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TRACK_EMPLOYEE_ID, " ");
        contentValues.put(KEY_NETWORK_STATUS, " ");
        contentValues.put(KEY_DATE, " ");
        contentValues.put(KEY_IS_SYNC, " ");

        db.insert(TABLE_NAME, null, contentValues);

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Cursor selectData(String s) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "select emp_id,net_status,date from " + TABLE_NAME + " where is_sync = " + s;
        Cursor cursor = db.rawQuery(query, null, null);
        return cursor;
    }
/*
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Cursor getPassengerList() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TableName;
        Cursor cursor = db.rawQuery(query, null, null);
        return cursor;
    }*/
}
