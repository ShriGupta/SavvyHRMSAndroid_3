package com.savvy.hrmsnewapp.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class OkHttpFileUploadHelper {
    Context context;
    OnFileUploadListener onFileUploadListener;
    ProgressDialog progressDialog;

    public OkHttpFileUploadHelper() {

    }

    public OkHttpFileUploadHelper(Context context, OnFileUploadListener onFileUploadListener) {
        this.context = context;
        this.onFileUploadListener = onFileUploadListener;

    }

    public void uploadFileWithOKHttp(Bitmap bitmap, String serverUrl, String message) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, stream);
        final byte[] bitmapdata = stream.toByteArray();

        OkHttpClient  client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"apj.jpg\""),
                        RequestBody.create(MediaType.parse("image/jpg"), bitmapdata))
                .build();
        final Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                onFileUploadListener.onFileUploadSuccess("Face attendance not successful, please try again!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (response.body() != null) {
                        onFileUploadListener.onFileUploadSuccess(response.body().string());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void checkIfDialogDismiss() {
        Log.e(TAG, "checkIfDialogDismiss: ");
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
