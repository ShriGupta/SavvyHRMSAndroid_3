package com.savvy.hrmsnewapp.activity;

import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.room_database.CarDetailsModel;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.utils.Utilities;

public class AddCarPickUpActivity extends AppCompatActivity implements View.OnClickListener,MenuItem.OnMenuItemClickListener {

    Button carpickupTime, carReleaseTime, pickupDate, addButton, addcloseButton, closeButton;
    EditText edt_Pickupat, edt_DropAt, edt_Comment;
    CalanderHRMS calanderHRMS;
    CoordinatorLayout coordinatorLayout;
    String FROM_DATE = "";
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_pic_up);

        setTitle("Car PickUp Details");
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calanderHRMS = new CalanderHRMS(AddCarPickUpActivity.this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        handler = new Handler();


        edt_Pickupat = (EditText) findViewById(R.id.edt_pickupAt);
        edt_DropAt = (EditText) findViewById(R.id.edt_dropAt);
        edt_Comment = (EditText) findViewById(R.id.edt_Comment);


        carpickupTime = (Button) findViewById(R.id.carPickupTime);
        carReleaseTime = (Button) findViewById(R.id.carReleaseTime);

        pickupDate = (Button) findViewById(R.id.carpickupDate);
        addButton = (Button) findViewById(R.id.carpickupAddButton);
        addcloseButton = (Button) findViewById(R.id.addcloseCarpickupButton);
        closeButton = (Button) findViewById(R.id.close_addCarButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String dateValue = bundle.getString("From_Date");
            pickupDate.setText(dateValue);
        }


        pickupDate.setOnClickListener(this);
        addButton.setOnClickListener(this);
        addcloseButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        carpickupTime.setOnClickListener(this);
        carReleaseTime.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.carpickupDate:
                calanderHRMS.datePicker(pickupDate);
                break;

            case R.id.carPickupTime:

                calanderHRMS.timePicker(carpickupTime);
                break;
            case R.id.carReleaseTime:

                calanderHRMS.timePicker(carReleaseTime);
                break;

            case R.id.carpickupAddButton:
                if (pickupDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please select Pick Up Date");
                } else if (edt_Pickupat.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter car pick up detail");
                } else if (edt_DropAt.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter car drop at detail");
                } else if (carpickupTime.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Pickup time");
                } else if (carReleaseTime.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Release time");
                } else if (edt_Comment.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter comment");
                } else {
                    saveData(pickupDate.getText().toString(),
                            edt_Pickupat.getText().toString().trim(),
                            edt_DropAt.getText().toString().trim(),
                            carpickupTime.getText().toString(),
                            carReleaseTime.getText().toString(),
                            edt_Comment.getText().toString().trim(), v);
                }


                break;

            case R.id.addcloseCarpickupButton:

                if (pickupDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please select Pick Up Date");
                } else if (edt_Pickupat.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter car pick up detail");
                } else if (edt_DropAt.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter car drop at detail");
                } else if (carpickupTime.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Pickup time");
                } else if (carReleaseTime.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Release time");
                } else if (edt_Comment.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter comment");
                } else {
                    saveData(pickupDate.getText().toString(),
                            edt_Pickupat.getText().toString().trim(),
                            edt_DropAt.getText().toString().trim(),
                            carpickupTime.getText().toString(),
                            carReleaseTime.getText().toString(),
                            edt_Comment.getText().toString().trim(), v);
                }
                break;

            case R.id.close_addCarButton:
                finish();
                break;
        }
    }

    private void saveData(String pickupdate, String pickupat, String dropat, String pickuptime, String releasetime, String comment, final View v) {

        final CarDetailsModel carDetailsModel = new CarDetailsModel();
        carDetailsModel.setPickupdate(pickupdate);
        carDetailsModel.setPickupat(pickupat);
        carDetailsModel.setDropat(dropat);
        carDetailsModel.setPickuptime(pickuptime);
        carDetailsModel.setReleasetime(releasetime);
        carDetailsModel.setComment(comment);

        android.os.AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long value = DatabaseClient.getInstance(AddCarPickUpActivity.this).getAppDatabase().passengerDao().insertCarDetails(carDetailsModel);
                Log.d("TAG", "run: " + value);
            }
        });

        pickupDate.setText("");
        edt_Pickupat.setText("");
        edt_DropAt.setText("");
        carpickupTime.setText("");
        carReleaseTime.setText("");
        edt_Comment.setText("");

        if (v.getId() == R.id.addcloseCarpickupButton) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
        if (v.getId() == R.id.carpickupAddButton) {
            Utilities.showDialog(coordinatorLayout, "Record insert successfully..");
        }

    } @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }
}
