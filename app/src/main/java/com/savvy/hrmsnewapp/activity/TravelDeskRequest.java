package com.savvy.hrmsnewapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleyMultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class TravelDeskRequest extends BaseActivity implements View.OnClickListener {

    CustomTextView nofilechosen;
    EditText edtAmount, edtRemarks;
    LinearLayout chooseFileLayout;
    Button send, close;
    CoordinatorLayout coordinatorLayout;
    AlertDialog alertD;
    String actualFileName = "";
    String displayFileName = "";
    String employeeId = "";
    String itineraryId = "", requestId = "";
    ImageView imguploadicon;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_desk_request);


        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        edtAmount = findViewById(R.id.travelDeskAmount);
        nofilechosen = findViewById(R.id.txtNoFileChosen);
        imguploadicon = findViewById(R.id.imguploadIcon);
        edtRemarks = findViewById(R.id.travelDeskremarks);
        chooseFileLayout = findViewById(R.id.chooseFileLayout);
        send = findViewById(R.id.travelDeskSendVButton);
        close = findViewById(R.id.travelDeskCloseButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itineraryId = bundle.getString("ITINERARY_ID");
            requestId = bundle.getString("TRF_TRAVEL_REQUEST_ID");
        }
        chooseFileLayout.setOnClickListener(this);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtAmount.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Amount");
                } else if (actualFileName.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Attach File");
                } else if (edtRemarks.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Remarks");
                } else {
                    sendRequest(employeeId, edtRemarks.getText().toString().trim().replaceAll("\\s", ""), itineraryId, actualFileName, edtAmount.getText().toString());
                }

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
    }

    private void sendRequest(String employeeId, String remarks, String itineraryId, String actualFileName, String amount) {
        if (Utilities.isNetworkAvailable(TravelDeskRequest.this)) {
            showProgressDialog();

            final RequestQueue requestQueue = Volley.newRequestQueue(TravelDeskRequest.this);
            final String GET_TRAVELDESK_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTicketFicci/" + employeeId + "/" + remarks + "/" + itineraryId + "/" + actualFileName + "/" + amount;
            Log.d(TAG, "getTravelDeskData: " + GET_TRAVELDESK_URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_TRAVELDESK_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgressDialog();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        if (Integer.parseInt(response.replaceAll("^\"|\"$", "")) > 0) {
                            Utilities.showDialog(coordinatorLayout, "Ticket Successfully addedd");
                            edtAmount.setText("");
                            edtRemarks.setText("");
                            nofilechosen.setText("file not attached!");
                            imguploadicon.setVisibility(View.GONE);
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Error while adding Ticket");
                        }
                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", shared.getString("EMPLOYEE_ID_FINAL", ""));
                    return params;
                }


                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void closeActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("TRF_TRAVEL_REQUEST_ID", requestId);
        Intent intent = new Intent(TravelDeskRequest.this, TravelDeskItineraryDetails.class);
        intent.putExtras(bundle);
        startActivity(intent);
        TravelDeskRequest.this.finish();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.chooseFileLayout) {
            uploadFile();
        }
    }

    private void uploadFile() {
        LayoutInflater layoutInflater = LayoutInflater.from(TravelDeskRequest.this);
        View promptView = layoutInflater.inflate(R.layout.leave_request_custom_dialog_mmt, null);
        alertD = new AlertDialog.Builder(TravelDeskRequest.this).create();
        ImageButton btnAdd1 = promptView.findViewById(R.id.galleryButton);
        ImageButton btnAdd2 = promptView.findViewById(R.id.cameraButton);
        btnAdd1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 999);
            }
        });
        btnAdd2.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(TravelDeskRequest.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1000);
            } else {
                ActivityCompat.requestPermissions(TravelDeskRequest.this, new String[]{Manifest.permission.CAMERA}, 1000);
            }
        });
        alertD.setView(promptView);
        alertD.show();
        Window window = alertD.getWindow();
        assert window != null;
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setAttributes(wlp);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        alertD.dismiss();
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                displayFileName = fileName(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(TravelDeskRequest.this.getContentResolver(), uri);
                    if (bitmap != null) {
                        uploadImage(bitmap);
                    } else {
                        getFileData(Objects.requireNonNull(uri));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TravelDeskRequest.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                imguploadicon.setVisibility(View.GONE);
            }
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            displayFileName = "CaptureImage.jpg";
            uploadImage(photo);
        } else {
            showToast("Please allow camera permission.");
        }
    }

    private void getFileData(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = TravelDeskRequest.this.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.d("nameeeee>>>>  ", displayFileName);

                    uploadPDF(displayFileName, uri);
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayFileName = myFile.getName();
        }
    }

    private void uploadPDF(final String pdfname, Uri pdffile) {
        InputStream iStream;
        try {
            showProgressDialog();
            iStream = TravelDeskRequest.this.getContentResolver().openInputStream(pdffile);
            assert iStream != null;
            final byte[] inputData = getBytes(iStream);
            String upload_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId;

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            dismissProgressDialog();
                            List<String> list = Arrays.asList(new String(response.data).split(","));
                            int value = Integer.parseInt(list.get(0));
                            if (value == 1) {
                                Utilities.showDialog(coordinatorLayout, "File Upload Successfully");
                                imguploadicon.setVisibility(View.VISIBLE);
                                nofilechosen.setText(displayFileName);
                            }
                            actualFileName = list.get(1);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgressDialog();
                            nofilechosen.setText("");
                            imguploadicon.setVisibility(View.GONE);
                            Toast.makeText(TravelDeskRequest.this, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return new HashMap<>();
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("filename", new DataPart(pdfname, inputData));
                    return params;
                }
            };
            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(TravelDeskRequest.this).addToRequestQueue(volleyMultipartRequest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void uploadImage(final Bitmap bitmap) {
        showProgressDialog();
        String IMG_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId;

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, IMG_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        dismissProgressDialog();

                        List<String> list = Arrays.asList(new String(response.data).split(","));
                        int value = Integer.parseInt(list.get(0));
                        if (value == 1) {
                            Utilities.showDialog(coordinatorLayout, "Image uploaded Successfully");
                            imguploadicon.setVisibility(View.VISIBLE);
                            nofilechosen.setText(displayFileName);
                        }
                        actualFileName = list.get(1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        nofilechosen.setText("");
                        imguploadicon.setVisibility(View.GONE);
                        Toast.makeText(TravelDeskRequest.this, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("filename", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(TravelDeskRequest.this).addToRequestQueue(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        if (bitmap != null) {
            int imgquality = 100;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, imgquality, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } else {
            return null;
        }

    }

    private String fileName(Uri uri) {
        Cursor returnCursor = TravelDeskRequest.this.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
}
