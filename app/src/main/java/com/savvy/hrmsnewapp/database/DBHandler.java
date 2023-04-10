package com.savvy.hrmsnewapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.savvy.hrmsnewapp.model.ProfileModel;
import com.savvy.hrmsnewapp.model.UploadImageModel;


public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "StudentInfo";

    // Contacts table name
    private static final String TABLE_STUDENT = "student";

    // STUDENT Table Columns names
//    private static final String KEY_ID          = "id";
    private static final String KEY_STU_ID      = "userid";
    private static final String KEY_STU_NAME    = "name";
    private static final String KEY_STU_EMAIL   = "email_id";
    private static final String KEY_USER_TYPE   = "user_type";
    private static final String KEY_STU_CODE    = "student_code";
    private static final String KEY_FATHER_NAME = "father_name";
    private static final String KEY_MOTHER_NAME = "mother_name";
    private static final String KEY_CENTER      = "center";
    private static final String KEY_ADDRESS     = "address";
    private static final String KEY_MOBILE      = "mobile_number";
    private static final String KEY_COURSE      = "course";
    private static final String KEY_BATCH       = "batch";
    private static final String KEY_DOB         = "dob";
    private static final String KEY_COURSE_ID   = "course_id";
    private static final String KEY_AGE         = "age";
    private static final String KEY_BLOOD_GROUP = "blood_group";
    private static final String KEY_PRO_URL     = "imageUrl";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } //  KEY_ID + " INTEGER PRIMARY KEY,"

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_STUDENT + "("
                 + KEY_STU_ID + " INTEGER,"  + KEY_STU_NAME + " TEXT,"  + KEY_STU_EMAIL + " TEXT," + KEY_USER_TYPE + " TEXT,"
                + KEY_STU_CODE + " TEXT,"  + KEY_FATHER_NAME + " TEXT,"  + KEY_MOTHER_NAME + " TEXT,"  + KEY_CENTER + " TEXT,"
                + KEY_ADDRESS + " TEXT," + KEY_MOBILE + " TEXT," + KEY_COURSE + " TEXT," + KEY_BATCH + " TEXT," + KEY_DOB + " TEXT,"
                + KEY_COURSE_ID + " INTEGER," + KEY_AGE + " TEXT," + KEY_BLOOD_GROUP + " TEXT," + KEY_PRO_URL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        // Creating tables again
        onCreate(db);
    }

    // Adding new shop
    public void addProfileData(ProfileModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STU_ID, String.valueOf(model.getUserid()));
        values.put(KEY_STU_NAME, model.getName());
        values.put(KEY_STU_EMAIL, model.getEmail_id());
        values.put(KEY_USER_TYPE, model.getUser_type());
        values.put(KEY_STU_CODE, model.getStudent_code());
        values.put(KEY_FATHER_NAME, model.getFather_name());
        values.put(KEY_MOTHER_NAME, model.getMother_name());
        values.put(KEY_CENTER, model.getCenter());
        values.put(KEY_ADDRESS, model.getAddress());
        values.put(KEY_MOBILE, model.getMobile_number());
        values.put(KEY_COURSE, model.getCourse());
        values.put(KEY_BATCH, model.getBatch());
        values.put(KEY_DOB, model.getDate_of_birth());
        values.put(KEY_COURSE_ID, model.getCourse_id());
        values.put(KEY_AGE, model.getAge());
        values.put(KEY_BLOOD_GROUP, model.getBlood_group());
        values.put(KEY_PRO_URL, model.getImageUrl());

        // Inserting Row
        db.insert(TABLE_STUDENT, null, values);
        db.close(); // Closing database connection
    }

    // Getting All data
    public ProfileModel getAllData() {
        ProfileModel model = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor= db.query(TABLE_STUDENT, new String[] { KEY_STU_ID, KEY_STU_NAME,
                    KEY_STU_EMAIL, KEY_USER_TYPE, KEY_STU_CODE, KEY_FATHER_NAME,  KEY_MOTHER_NAME, KEY_CENTER, KEY_ADDRESS, KEY_MOBILE , KEY_COURSE, KEY_BATCH, KEY_DOB, KEY_COURSE_ID, KEY_AGE, KEY_BLOOD_GROUP, KEY_PRO_URL}, null, null, null, null, null);
            model = new ProfileModel();
            if (cursor.moveToFirst()) {
                model.setUserid(Integer.parseInt(cursor.getString(0)));
                model.setName(cursor.getString(1));
                model.setEmail_id(cursor.getString(2));
                model.setUser_type(cursor.getString(3));
                model.setStudent_code(cursor.getString(4));
                model.setFather_name(cursor.getString(5));
                model.setMother_name(cursor.getString(6));
                model.setCenter(cursor.getString(7));
                model.setAddress(cursor.getString(8));
                model.setMobile_number(cursor.getString(9));
                model.setCourse(cursor.getString(10));
                model.setBatch(cursor.getString(11));
                model.setDate_of_birth(cursor.getString(12));
                model.setCourse_id(Integer.parseInt(cursor.getString(13)));
                model.setAge(cursor.getString(14));
                model.setBlood_group(cursor.getString(15));
                model.setImageUrl(cursor.getString(16));
            }
        } catch (SQLiteException se ) {

        }
        return model;
    }


    // Updating a data
    public int updateProfileData(UploadImageModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRO_URL, model.getImageUrl());

        // updating row
        return db.update(TABLE_STUDENT, values, KEY_STU_ID + " = ?",
                new String[]{String.valueOf(model.getUserid())});
    }

    // Updating a data
    public int getProfileImage(UploadImageModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRO_URL, model.getImageUrl());

        // updating row
        return db.update(TABLE_STUDENT, values, KEY_STU_ID + " = ?",
                new String[]{String.valueOf(model.getUserid())});
    }

    // Deleting a data
    public void deleteProfileData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT, null, null);
        db.close();
    }
}

