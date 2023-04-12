package com.savvy.hrmsnewapp.utils;

/**
 * Created by Devabrata Kakati on 24/11/15.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.model.SelectedAnswersModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Utilities {

    public static String[] PERMISSION_ALL = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CALENDAR};

    public static String[] PERMISSION_CAMERA = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    public static String[] PERMISSION_CALENDAR = {Manifest.permission.READ_CALENDAR};

    public static String[] PERMISSION_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    public static int PERMISSION_REQUEST_CODE = 100;

    public static String getBase64Image(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


//    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
//        try {
//            smallBitmap = RGB565toARGB888(smallBitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Bitmap bitmap = Bitmap.createBitmap(
//                smallBitmap.getWidth(), smallBitmap.getHeight(),
//                Bitmap.Config.ARGB_8888);
//
//        RenderScript renderScript = RenderScript.create(context);
//
//        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
//        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);
//
//        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
//                Element.U8_4(renderScript));
//        blur.setInput(blurInput);
//        blur.setRadius(radius); // radius must be 0 < r <= 25
//        blur.forEach(blurOutput);
//
//        blurOutput.copyTo(bitmap);
//        renderScript.destroy();
//
//        return bitmap;
//
//    }


    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    @SuppressLint("MissingPermission")
    private static String getMyPhoneNumber(Context context) {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    public static String getMy10DigitPhoneNumber(Context context) {
        String s = getMyPhoneNumber(context);
        return s != null && s.length() > 2 ? s.substring(0) : null;
    }

    // check for internet connectivity
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

    }

    // display a message dialog with custom message
    public static void showDialog(CoordinatorLayout coordinatorLayout, final String message) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, message, 3000);
        snackbar.show();

    }

//    //Try to undarstsnd about context--------------26_Feb-------Naman
//    private static Context getApplicationContext() {
//        return null;
    // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//    }


    public static String createJson(Map<String, String> values) {
        JSONObject outer = new JSONObject();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            try {
                outer.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return outer.toString();
    }

    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) first = false;
            else result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


//    public static void hideSoftKeyboard(View v, Context mContext){
//        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
//    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText, viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText, viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText, viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, ".. See More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    public static boolean contains(List<SelectedAnswersModel> list, String quesId) {
        for (SelectedAnswersModel item : list) {
            if (item.getQuesId().equals(quesId)) {
                return true;
            }
        }
        return false;
    }

    public static int getIndexOfListItem(List<SelectedAnswersModel> list, String quesId) {
        for (SelectedAnswersModel item : list) {
            if (item.getQuesId().endsWith(quesId)) {
                return list.indexOf(item);
            }

        }
        return 500;
    }

    public static ArrayList<SelectedAnswersModel> removeDuplicates(ArrayList<SelectedAnswersModel> list) {
        // Create a new ArrayList
        ArrayList<SelectedAnswersModel> newList = new ArrayList<>();
        // Traverse through the first list
        for (SelectedAnswersModel element : list) {
            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        // return the new list
        return newList;
    }

    public static String getAddressFromLocation(Context context, Location location) {
        String completeAddress = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            if (location != null) {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                completeAddress = address;
            } else {
                Toast.makeText(context, "Please wait while we are fetching location.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return completeAddress;
    }

    public static String getCountryFromLocation(Context context, Location location) {
        String completeAddress = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            if (location != null) {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    completeAddress = country;
                } else {
                    completeAddress = "";
                }

            } else {
                Toast.makeText(context, "Please wait while we are fetching location.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return completeAddress;
    }

    public static String getAddressFromLateLong(Context context, double latitude, double longitude) {
        String completeAddress = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() != 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                completeAddress = address; //+ ", " + city + ", " + state + ", " + country + ", " + postalCode;
            } else {
                completeAddress = "";
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return completeAddress;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {

        if (bitmap != null) {
            int imgquality = 100;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, imgquality, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } else {
            return null;
        }

    }

    public static String fileName(Context context, Uri uri) {
        Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    public static String handleVolleyError(VolleyError error) {
        String errorName = "";
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            errorName = "Time Out Error !";
        } else if (error instanceof AuthFailureError) {
            errorName = "Authenication Error !";
        } else if (error instanceof ServerError) {
            errorName = "Server Error !";
        } else if (error instanceof NetworkError) {
            errorName = "Network Error !";
        } else if (error instanceof ParseError) {
            errorName = "Parsing Error !";
        }
        return errorName;
    }


    public static boolean isMockSettingsON(Context context) {
        if (Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else return true;
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i].equals("android.permission.ACCESS_MOCK_LOCATION") && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception ", e.getMessage());
            }
        }

        if (count > 0) return true;
        return false;
    }

    public static boolean isGPSTurnedOn(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void moveToLocationSetting(Activity activity) {
        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivity(callGPSSettingIntent);
    }

    public static void showLocationErrorDialog(Activity context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_demo);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvMessage = dialog.findViewById(R.id.tv_error_textview);
        tvMessage.setText(message);
        Button btnDialogOk = dialog.findViewById(R.id.btn_ok);
        btnDialogOk.setOnClickListener(v -> {
            try {
                dialog.dismiss();
                if (!isGPSTurnedOn(context)) {
                    moveToLocationSetting(context);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialog.show();

    }


}