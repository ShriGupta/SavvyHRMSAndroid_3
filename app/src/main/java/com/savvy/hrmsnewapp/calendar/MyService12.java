package com.savvy.hrmsnewapp.calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by orapc7 on 6/15/2017.
 */

public class MyService12 extends Service {

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MyService12(Context context){
        this.context = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(context,"Start Service",Toast.LENGTH_LONG).show();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(context,"Stop Service",Toast.LENGTH_LONG).show();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
