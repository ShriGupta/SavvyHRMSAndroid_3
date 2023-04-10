package com.savvy.hrmsnewapp.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.savvy.hrmsnewapp.volleyMultipart.MultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static android.content.ContentValues.TAG;

public class FileUploadHelper {

    Context context;
    ProgressDialog progressDialog;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;

    OnFileUploadListener onFileUploadListener;
    String IMG_URL;


    public FileUploadHelper(Context context, OnFileUploadListener onFileUploadListener, String IMG_URL) {
        this.context = context;
        this.onFileUploadListener = onFileUploadListener;
        this.IMG_URL = IMG_URL;

    }

    public void uploadPicture(Bitmap bmp, String picName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        imageProcessRequest(byteArray, picName);
    }

    public void imageProcessRequest(byte[] fileData1, String filename) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Verifying your face...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {
            buildPart(dos, fileData1, filename);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            multipartBody = bos.toByteArray();
            MultipartRequest multipartRequest = new MultipartRequest(IMG_URL, null, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        progressDialog.dismiss();
                        String result = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        onFileUploadListener.onFileUploadSuccess(result);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                progressDialog.dismiss();
                Toast.makeText(context, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
            }, (percentage, totalSize) -> Log.e(TAG, "onUpdateProgress: "));

            RetryPolicy policy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            multipartRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(multipartRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        dataOutputStream.writeBytes(lineEnd);
    }

}
