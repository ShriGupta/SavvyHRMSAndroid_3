package com.savvy.hrmsnewapp.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.NewCustomSpinnerAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleyMultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.fragment.TravelStatusFicciFragment.MY_PREFS_NAME;

public class ReimbursementRequestFragment extends BaseFragment {

    SharedPreferences sharedPreferences;
    String employeeId, token, groupID;
    CustomTextView tvMonth, tvYear;
    Button btnSend;
    Spinner spReimburseSpinner;
    String isApplicableforReimbursement, isDocRequired = "";
    ArrayList<HashMap<String, String>> spinnerList;
    NewCustomSpinnerAdapter customSpinnerAdapter;
    String reimbursementID;
    TextView tvMonthlyBalance, tvYearlyBalance, tvDocumentsRequired;
    LinearLayout llChooseFileLayout;
    ImageView imguploadicon;
    AlertDialog alertD;
    String actualFileName = "";
    String displayFileName = "";
    CustomTextView tvNoFileChoosen;
    LinearLayout llButtonLayout;
    EditText edtNonTaxableAmount, edtTaxableAmount, edtComment;
    String month, year;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        employeeId = (sharedPreferences.getString("EmpoyeeId", ""));
        token = (sharedPreferences.getString("Token", ""));
        groupID = (sharedPreferences.getString("GroupId", ""));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reimbursement_request, container, false);

        tvMonth = view.findViewById(R.id.tv_month);
        tvYear = view.findViewById(R.id.tv_year);
        btnSend = view.findViewById(R.id.btn_send);

        edtNonTaxableAmount = view.findViewById(R.id.edt_non_taxable_amount);
        edtTaxableAmount = view.findViewById(R.id.edt_taxable_amount);
        edtComment = view.findViewById(R.id.edt_comment);
        llButtonLayout = view.findViewById(R.id.ll_button_layout);
        spReimburseSpinner = view.findViewById(R.id.sp_reim_spinner);
        tvMonthlyBalance = view.findViewById(R.id.tv_monthly_balance);
        tvYearlyBalance = view.findViewById(R.id.tv_yearly_balance);
        tvDocumentsRequired = view.findViewById(R.id.tv_documents_required);

        llChooseFileLayout = view.findViewById(R.id.chooseFileLayout);
        imguploadicon = view.findViewById(R.id.imguploadIcon);
        tvNoFileChoosen = view.findViewById(R.id.tv_no_file_choosen);
        llChooseFileLayout.setOnClickListener(view1 -> uploadFile());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

        getReimbursementData(employeeId, token);
        getReimburseType();
        btnSend.setOnClickListener(view1 -> {
            sendFinalRequest(employeeId, edtNonTaxableAmount.getText().toString().trim()
                    , edtTaxableAmount.getText().toString().trim(), edtComment.getText().toString().trim(),
                    reimbursementID, month, year, actualFileName);
        });
        spReimburseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    reimbursementID = "";
                    tvMonthlyBalance.setText("");
                    tvYearlyBalance.setText("");
                    tvDocumentsRequired.setText("");
                } else {
                    reimbursementID = spinnerList.get(i - 1).get("ReimbursementID");
                    getConfigDataRequest();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void sendFinalRequest(String employeeId, String nontaxableAmt, String taxableAmt, String comment, String reimbursementID, String month, String year, String actualFileName) {
        if (applyValidation()) {
            if (isNetworkAvailable(requireContext())) {
                showProgressDialog();
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendReimbursementRequest";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("employeeId", employeeId);
                    jsonObject.put("requestID", "0");
                    jsonObject.put("taxableAmount", taxableAmt);
                    jsonObject.put("notaxableAmount", nontaxableAmt);
                    jsonObject.put("Comment", comment);
                    jsonObject.put("PolicyID", isApplicableforReimbursement);
                    jsonObject.put("ReimbursementType", reimbursementID);
                    jsonObject.put("RequestMonth", month);
                    jsonObject.put("RequestYear", year);
                    jsonObject.put("filePath", actualFileName);

                    Log.e("TAG", "getReimburseType: " + jsonObject.toString());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dismissProgressDialog();
                            try {
                                if (Integer.parseInt(response.getString("SendReimbursementRequestResult")) > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Reimbursement request sent successfully.");
                                    clearData();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, error -> {
                        dismissProgressDialog();
                        Utilities.showDialog(coordinatorLayout, error.getMessage());
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
                            return params;
                        }


                        @Override
                        public String getBodyContentType() {
                            return "application/json";
                        }
                    };
                    RetryPolicy policy = new DefaultRetryPolicy(3000000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    jsonObjectRequest.setRetryPolicy(policy);
                    VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean applyValidation() {
        if (isDocRequired.equals("True") && actualFileName.equals("")) {
            Utilities.showDialog(coordinatorLayout, "Please attach documents!");
            return false;
        } else if (reimbursementID.equals("")) {
            Utilities.showDialog(coordinatorLayout, "Please select RE Type");
            return false;
        } else if (edtNonTaxableAmount.getText().toString().trim().equals("")) {
            Utilities.showDialog(coordinatorLayout, "Please Enter Non taxable amount.");
            return false;
        } else if (edtTaxableAmount.getText().toString().trim().equals("")) {
            Utilities.showDialog(coordinatorLayout, "Please Enter taxable amount.");
            return false;
        }
        return true;
    }

    private void getConfigDataRequest() {
        if (isNetworkAvailable(requireContext())) {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeReimbursementConfiguration";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("employeeId", employeeId);
                jsonObject.put("reimbursementID", reimbursementID);
                jsonObject.put("groupId", groupID);
                jsonObject.put("securityToken", token);
                Log.e("TAG", "getReimburseType: " + jsonObject.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            try {
                                String monthlyBalance = response.getJSONObject("GetEmployeeReimbursementConfigurationResult").getString("MonthlyBalance");
                                String yearlyBalance = response.getJSONObject("GetEmployeeReimbursementConfigurationResult").getString("RunningBalance");
                                isDocRequired = response.getJSONObject("GetEmployeeReimbursementConfigurationResult").getString("ReimbursmentDocumentCheckAllowed");

                                if (monthlyBalance.equals("") || monthlyBalance.equals("null")) {
                                    tvMonthlyBalance.setText("");
                                } else {
                                    tvMonthlyBalance.setText(monthlyBalance);
                                }

                                if (yearlyBalance.equals("") || yearlyBalance.equals("null")) {
                                    tvYearlyBalance.setText("");
                                } else {
                                    tvYearlyBalance.setText(yearlyBalance);
                                }

                                if (isDocRequired.equals("") || isDocRequired.equals("null")) {
                                    tvDocumentsRequired.setText("");
                                } else {
                                    tvDocumentsRequired.setText(isDocRequired);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, error -> {
                    Utilities.showDialog(coordinatorLayout, error.getMessage());
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
                        return params;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                RetryPolicy policy = new DefaultRetryPolicy(3000000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getReimburseType() {
        //set spinner data
        if (isNetworkAvailable(requireContext())) {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeReimbursementPolicy";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("employeeId", employeeId);
                jsonObject.put("securityToken", token);
                Log.e("TAG", "getReimburseType: " + jsonObject.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            spinnerList = new ArrayList<>();
                            HashMap<String, String> hashMap;
                            try {
                                JSONArray jsonArray = response.getJSONArray("GetEmployeeReimbursementPolicyResult");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    hashMap = new HashMap<>();
                                    hashMap.put("ReimbursementID", jsonArray.getJSONObject(i).getString("ReimbursementID"));
                                    hashMap.put("VALUE", jsonArray.getJSONObject(i).getString("ReimbursementName"));
                                    hashMap.put("ReimbursmentDocumentCheckAllowed", jsonArray.getJSONObject(i).getString("ReimbursmentDocumentCheckAllowed"));
                                    spinnerList.add(hashMap);
                                }
                                customSpinnerAdapter = new NewCustomSpinnerAdapter(requireActivity(), spinnerList);
                                spReimburseSpinner.setAdapter(customSpinnerAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, error -> {
                    Utilities.showDialog(coordinatorLayout, error.getMessage());
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
                        return params;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                RetryPolicy policy = new DefaultRetryPolicy(3000000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getReimbursementData(String employeeId, String token) {
        if (isNetworkAvailable(requireContext())) {
            showProgressDialog();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetReimbursementRequestMonthYear";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("employeeId", employeeId);
                jsonObject.put("securityToken", token);
                Log.e("TAG", "getReimburseType: " + jsonObject.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissProgressDialog();
                        try {
                            month = response.getJSONObject("GetReimbursementRequestMonthYearResult").getString("Month");
                            year = response.getJSONObject("GetReimbursementRequestMonthYearResult").getString("Year");
                            isApplicableforReimbursement = response.getJSONObject("GetReimbursementRequestMonthYearResult").getString("ISApplicable");
                            tvMonth.setText(response.getJSONObject("GetReimbursementRequestMonthYearResult").getString("MonthName"));
                            tvYear.setText(year);
                            if (isApplicableforReimbursement != null) {
                                if (isApplicableforReimbursement.equals("0")) {
                                    Utilities.showDialog(coordinatorLayout, "Your not eligible for Reimbursement!");
                                } else {
                                    llButtonLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
                    dismissProgressDialog();
                    Utilities.showDialog(coordinatorLayout, error.getMessage());
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
                        return params;
                    }


                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                RetryPolicy policy = new DefaultRetryPolicy(3000000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void uploadFile() {
        LayoutInflater layoutInflater = LayoutInflater.from(requireActivity());
        View promptView = layoutInflater.inflate(R.layout.leave_request_custom_dialog_mmt, null);
        alertD = new AlertDialog.Builder(requireActivity()).create();
        ImageButton btnAdd1 = promptView.findViewById(R.id.galleryButton);
        ImageButton btnAdd2 = promptView.findViewById(R.id.cameraButton);
        btnAdd1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 999);
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 999);
                }


            }
        });
        btnAdd2.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1000);
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 1000);
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                    if (bitmap != null) {
                        uploadImage(bitmap);
                    } else {
                        getFileData(Objects.requireNonNull(uri));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), "Failed!", Toast.LENGTH_SHORT).show();
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
        }
    }

    private void getFileData(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
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
            iStream = requireActivity().getContentResolver().openInputStream(pdffile);
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
                                tvNoFileChoosen.setText(displayFileName);
                            }
                            actualFileName = list.get(1);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgressDialog();
                            tvNoFileChoosen.setText("");
                            imguploadicon.setVisibility(View.GONE);
                            Toast.makeText(requireActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
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
            VolleySingleton.getInstance(requireActivity()).addToRequestQueue(volleyMultipartRequest);
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
        String IMG_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId + "&" + "Type=Reimbursement";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, IMG_URL,
                response -> {
                    dismissProgressDialog();

                    try {
                        List<String> list = Arrays.asList(new String(response.data).split("@"));
                        int value = Integer.parseInt(list.get(0));
                        if (value == 1) {
                            Utilities.showDialog(coordinatorLayout, "Image uploaded Successfully");
                            imguploadicon.setVisibility(View.VISIBLE);
                            tvNoFileChoosen.setText(displayFileName);
                        }
                        actualFileName = list.get(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast(e.getMessage());
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        tvNoFileChoosen.setText("No File Choosen");
                        imguploadicon.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(requireActivity()).addToRequestQueue(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        if (bitmap != null) {
            int imgquality = 70;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, imgquality, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } else {
            return null;
        }

    }

    private String fileName(Uri uri) {
        Cursor returnCursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    public void clearData() {
        spReimburseSpinner.setSelection(0);
        edtNonTaxableAmount.setText("");
        edtTaxableAmount.setText("");
        edtComment.setText("");
        tvNoFileChoosen.setText("choose file");
        imguploadicon.setVisibility(View.GONE);
    }
}
