package com.savvy.hrmsnewapp.activity;

import android.content.Context;
import android.graphics.Typeface;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

/**
 * Created by Hari Om on 1/20/2017.
 */
public class SavvyHRMSApplication extends MultiDexApplication{

    private static Context context;
    private static SavvyHRMSApplication mInstance;
    private RequestQueue mRequestQueue;
    public static final String TAG = SavvyHRMSApplication.class.getSimpleName();
   // public ProfileModel profileModel;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mInstance = this;
    }

    public static Context getContext() {
        return context;
    }

    public static synchronized SavvyHRMSApplication getInstance(){
        if(mInstance == null){
            mInstance = new SavvyHRMSApplication();
        }
        return mInstance;
    }

    public void setTextViewFont(TextView textView, String fontName){
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    public void setEditTextFont(TextView textView, String fontName){
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    public void setButtonFont(TextView textView, String fontName){
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        RetryPolicy policy = new DefaultRetryPolicy(3000000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(tag);
        }
    }

   /* public ProfileModel getProfileModel() {
        return profileModel;
    }

    public void setProfileModel(ProfileModel profileModel) {
        this.profileModel = profileModel;
    }*/

}
