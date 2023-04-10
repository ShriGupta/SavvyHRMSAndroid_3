package com.savvy.hrmsnewapp.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by orapc7 on 6/1/2017.
 */

public class MyService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(MyService.this,"Service Start", Toast.LENGTH_LONG).show();
//        Thread thread = new Thread(new TheThread(startId));
//        thread.start();

        synchronized (this) {
            try {
                wait(8000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(MyService.this,"Service Stop",Toast.LENGTH_LONG).show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
