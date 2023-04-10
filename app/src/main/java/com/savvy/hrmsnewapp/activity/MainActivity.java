package com.savvy.hrmsnewapp.activity;

import android.content.Intent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.fragment.MyMessageFragment;

public class MainActivity extends AppCompatActivity {
    Intent intent = null;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.e("TAG", "Anubhav");
        MyMessageFragment fragment = new MyMessageFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
        // Log.e("TAG","Tripathi");

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Bundle bundle = new Bundle();
        //bundle.putString("PostionId", "32");
        //intent.putExtras(bundle);
        startActivity(intent);
        //startService(new Intent(BaseTrackMeActivity.this, VisService.class));
        finish();
        // moveTaskToBack(true);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //moveTaskToBack(true);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //Bundle bundle = new Bundle();
            //bundle.putString("PostionId", "32");
            //intent.putExtras(bundle);
            startActivity(intent);
            //startService(new Intent(BaseTrackMeActivity.this, VisService.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
