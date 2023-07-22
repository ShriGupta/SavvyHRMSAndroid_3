package com.savvy.hrmsnewapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.helper.DownloadImageTask;
import com.savvy.hrmsnewapp.helper.OkHttpFileUploadHelper;
import com.savvy.hrmsnewapp.helper.OnFileUploadListener;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class FaceRegistrationFragment extends Fragment implements OnFileUploadListener {
    RoundedImageView roundImageView, selectCameraImage;
    ImageView newImageView;
    Button send_Approval_Button;

    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1400;
    String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME = "FACE_IMAGES";
    SharedPreferences sharedPreferences;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String FACE_REGISTRATION_URL;
    //FileUploadHelper fileUploadHelper;
    OkHttpFileUploadHelper okHttpFileUploadHelper;
    Bitmap finalbitmap;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_registration, container, false);

        // roundImageView = view.findViewById(R.id.roundImageView);
        newImageView = view.findViewById(R.id.newImageView);
        selectCameraImage = view.findViewById(R.id.selectCameraImage);
        send_Approval_Button = view.findViewById(R.id.send_Approval_Button);

        String employeeCode = (sharedPreferences.getString("EmployeeCode", ""));
        String empPhoto = (sharedPreferences.getString("EmpPhotoPath", ""));
        FACE_REGISTRATION_URL = Constants.IP_ADDRESS + "/FaceRegistration.ashx?EMPCODE=" + employeeCode;
        Log.e(TAG, "Face Url : " + FACE_REGISTRATION_URL);
        //fileUploadHelper = new FileUploadHelper(requireActivity(), this, FACE_REGISTRATION_URL);
        okHttpFileUploadHelper = new OkHttpFileUploadHelper(requireActivity(), this);
        Log.e(TAG, "EmpPhotoPath: " + empPhoto);
        if (empPhoto.equals("")) {
            roundImageView.setImageResource(R.drawable.trans_profile_image);
        } else {
            empPhoto = empPhoto + "?" + System.currentTimeMillis();
            // new DownloadImageTask(roundImageView).execute(empPhoto);
            new DownloadImageTask(newImageView).execute(empPhoto);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        send_Approval_Button.setOnClickListener(view -> {
            if (finalbitmap != null) {
                //fileUploadHelper.uploadPicture(finalbitmap, "MyImage.jpg");
                okHttpFileUploadHelper.uploadFileWithOKHttp(finalbitmap, FACE_REGISTRATION_URL, "Uploading file..");
            } else {
                Toast.makeText(requireActivity(), "Image not captured!", Toast.LENGTH_SHORT).show();
            }
        });
        selectCameraImage.setImageResource(R.drawable.face_camera_icon_new);
        selectCameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(requireActivity());
            }
        });
    }

    private void captureImage() {

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FaceRegistrationFragment.this.requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                try {

                    photoFile = createImageFile();
                    Log.i("TAG", photoFile.getAbsolutePath());
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.savvy.hrmsnewapp.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex) {
                    displayMessage(getActivity(), ex.getMessage());
                }
            } else {
                displayMessage(getActivity(), "Nullll");
            }
        }


    }

    private void captureImage2() {

        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile4();
            if (photoFile != null) {
                Log.i("TAG", photoFile.getAbsolutePath());
                Uri photoURI = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            displayMessage(getActivity(), "Camera is not available." + e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAPTURE_IMAGE_REQUEST && grantResults[0] > 0) {
            captureImage();
        } else {
            displayMessage(getActivity(), "Request cancelled or something went wrong.");
        }
    }


    private File createImageFile4() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                displayMessage(getActivity(), "Unable to create directory.");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void displayMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap myBitmap = decodeSampledBitmapFromFile(photoFile.getAbsolutePath(), 600, 550);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert ei != null;
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(myBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(myBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(myBitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotatedBitmap = rotateImage(myBitmap, 360);
                default:
                    rotatedBitmap = myBitmap;
            }
            finalbitmap = getScaledBitmap(rotatedBitmap);
            int nh = (int) (finalbitmap.getHeight() * (512.0 / finalbitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(finalbitmap, 512, nh, true);
            // roundImageView.setImageBitmap(scaled);
            newImageView.setImageBitmap(scaled);
            return;
        }else {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (selectedImage != null) {
                    Cursor cursor = requireActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        finalbitmap=BitmapFactory.decodeFile(picturePath);
                        newImageView.setImageBitmap(finalbitmap);
                        cursor.close();
                    }
                }
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private Bitmap getScaledBitmap(Bitmap bitmap) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        float scaleFactor = (float) width / (float) bitmap.getWidth();
        int newHeight = (int) (bitmap.getHeight() * scaleFactor);
        return Bitmap.createScaledBitmap(bitmap, width, newHeight, true);
    }

    public static Bitmap decodeSampledBitmapFromFile(String file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);// w w w .ja  v  a 2  s .co m

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width
                    / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        return inSampleSize;
    }
/*
    @Override
    public void onFileUploadSuccess(List<String> list) {
        int value = Integer.parseInt(list.get(0));
        if (value == 1) {
            showFaceResponse(list.get(1));
        } else if (value == 0) {
            showFaceResponse(list.get(1));
        } else {
            showFaceResponse(list.get(1));
        }
    }*/

    private void showFaceResponse(String message) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.face_alert_layout, null);
        dialogBuilder.setView(dialogView);

        CustomTextView tv_faceMessage = dialogView.findViewById(R.id.tv_faceMessage);
        Button face_alertButton = dialogView.findViewById(R.id.face_alertButton);
        tv_faceMessage.setText(message);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        face_alertButton.setOnClickListener(view -> alertDialog.dismiss());
    }


    @Override
    public void onFileUploadSuccess(String result) {
        List<String> list = Arrays.asList(result.split(","));
        requireActivity().runOnUiThread(() -> {
            int value = Integer.parseInt(list.get(0));
            if (value == 1) {
                showFaceResponse(list.get(1));
            } else if (value == 0) {
                showFaceResponse(list.get(1));
            } else if (value == -1) {
                showFaceResponse(list.get(1));
            } else {
                showFaceResponse(list.get(1));
            }
        });
    }

    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    captureImage();
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1500);
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }
}
