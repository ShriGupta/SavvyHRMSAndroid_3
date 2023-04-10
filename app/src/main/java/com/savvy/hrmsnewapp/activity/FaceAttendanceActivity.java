package com.savvy.hrmsnewapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.FaceAttendanceFragment;
import com.savvy.hrmsnewapp.helper.OkHttpFileUploadHelper;

import static com.savvy.hrmsnewapp.utils.Constants.FACE_TYPE;

public class FaceAttendanceActivity extends AppCompatActivity {

    Button backButton;
    CustomTextView tv_eyeBlink;
    public static final String TAG = "Face Fragmnet";
    Bundle bundle;
    static int cameraid;
    FaceAttendanceFragment fragment;
    ImageView reverseCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_attendance);
        setTitle("Face Verification");
        reverseCamera = findViewById(R.id.iv_reverse_camera);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            String faceValue = bundle.getString("SIMPLE_FACE");
            assert faceValue != null;
            if (faceValue.equals("")) {
                FACE_TYPE = "SIMPLE_FACE";
                reverseCamera.setVisibility(View.GONE);
            } else {
                FACE_TYPE = "TEAM_FACE";
            }
        }
        tv_eyeBlink = findViewById(R.id.tv_eyeBlinkText);
        cameraid = 1;
        fragment = new FaceAttendanceFragment(cameraid);
        fragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            new OkHttpFileUploadHelper().checkIfDialogDismiss();
            finish();
            overridePendingTransition(0, 0);
        });

        reverseCamera.setOnClickListener(view -> {
            if (cameraid == 1) {
                cameraid = 0;
                fragment = new FaceAttendanceFragment(cameraid);
            } else {
                cameraid = 1;
                fragment = new FaceAttendanceFragment(cameraid);
            }

            fragment.setArguments(bundle);
            FragmentManager manager1 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction1 = manager1.beginTransaction();
            fragmentTransaction1.replace(R.id.frame, fragment);
            fragmentTransaction1.commit();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " + "");
    }
}