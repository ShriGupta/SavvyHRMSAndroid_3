package com.savvy.hrmsnewapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.OfflineCredentialModel;

import java.util.List;
import java.util.Objects;

public class OfflineLoginActivity extends AppCompatActivity {

    EditText edtUserName, edtPassword;
    Button btnLoginButton;
    String username, offlinePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_mark_attendance);
        edtUserName = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        btnLoginButton = findViewById(R.id.btn_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Offline Login ");

        android.os.AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getOfflineUser();
            }
        });

        btnLoginButton.setOnClickListener(view -> {

            String password = edtPassword.getText().toString().trim();
            if (password.equals("")) {
                Toast.makeText(this, "Please Enter Password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.equals(offlinePassword)) {
                startActivity(new Intent(OfflineLoginActivity.this, OfflineMarkAttendanceActivity.class));
            } else {
                Toast.makeText(this, "Password did not match!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOfflineUser() {
        List<OfflineCredentialModel> userList = DatabaseClient.getInstance(OfflineLoginActivity.this).getAppDatabase().passengerDao().getAllOfflineUser();
        if (!userList.isEmpty()) {
            username = userList.get(0).getUsername();
            offlinePassword = userList.get(0).getPassword();
            runOnUiThread(() -> edtUserName.setText(username));
            Log.e("TAG", "getOfflineUser: " + username + " , " + offlinePassword);
        }
    }
}