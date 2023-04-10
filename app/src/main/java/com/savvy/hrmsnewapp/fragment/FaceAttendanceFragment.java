package com.savvy.hrmsnewapp.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.AttendanceSuccessActivity;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.helper.Exif;
import com.savvy.hrmsnewapp.helper.OkHttpFileUploadHelper;
import com.savvy.hrmsnewapp.helper.OnFileUploadListener;
import com.savvy.hrmsnewapp.utils.Constants;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class FaceAttendanceFragment extends BaseFragment implements OnFileUploadListener {
    SurfaceView surfaceView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    FaceDetector detector;
    ProgressDialog progressDialog;
    public int mOrientationDeg; //last rotation in degrees
    public int mOrientationRounded; //last orientation int from above
    private static final int _DATA_X = 0;
    private static final int _DATA_Y = 1;
    private static final int _DATA_Z = 2;
    private int ORIENTATION_UNKNOWN = -1;
    static int tempOrientRounded;

    private SensorManager mSensorManager;
    private Sensor mOrientation;

    String LocationString;
    String remark;
    String geoString;
    String latitude;
    String longitude;
    String PunchMode;
    String ATTDATE;
    String countryName;
    SharedPreferences sharedPreferences;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String IMG_URL;

    // FileUploadHelper fileUploadHelper;
    OkHttpFileUploadHelper okHttpFileUploadHelper;
    int cameraFacingId;
    int camerDirection;
    String employeeId;

    public FaceAttendanceFragment(int cameraFacingId) {
        this.cameraFacingId = cameraFacingId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getActivity() != null;
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sharedPreferences = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (sharedPreferences.getString("EmpoyeeId", ""));

        int rc = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(cameraFacingId);
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(requireActivity(), permissions, RequestCameraPermissionID);

    }

    private final SensorEventListener mOrientationSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y value
            if (magnitude * 4 >= Z * Z) {
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }

            if (orientation != mOrientationDeg) {
                mOrientationDeg = orientation;
                if (orientation <= 45 || orientation > 315) {//round to 0
                    tempOrientRounded = 1;//portrait
                } else if (orientation > 45 && orientation <= 135) {//round to 90
                    tempOrientRounded = 2; //lsleft
                } else if (orientation > 135 && orientation <= 225) {//round to 180
                    tempOrientRounded = 3; //upside down
                } else if (orientation > 225 && orientation <= 315) {//round to 270
                    tempOrientRounded = 4;//lsright
                }

            }

            if (mOrientationRounded != tempOrientRounded) {
                //Orientation changed, handle the change here
                mOrientationRounded = tempOrientRounded;
                Log.e(TAG, "onSensorChanged: " + mOrientationRounded);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_face_attendance, container, false);
        surfaceView = rootView.findViewById(R.id.surface_view);

        try {
            Bundle bundle = getArguments();
            assert bundle != null;
            LocationString = bundle.getString("LOCATION_ADDRESS");
            remark = bundle.getString("REMARK");
            geoString = bundle.getString("GEO_STRING");
            latitude = bundle.getString("LATITUDE");
            longitude = bundle.getString("LONGITUDE");
            PunchMode = bundle.getString("PUNCHMODE");
            ATTDATE = bundle.getString("ATTDATE");
            countryName = bundle.getString("COUNTRY_NAME");

            String faceType = bundle.getString("SIMPLE_FACE");
            String employeeCode;
            assert faceType != null;
            if (faceType.equals("")) {
                employeeCode = (sharedPreferences.getString("EmployeeCode", ""));
            } else {
                employeeCode = bundle.getString("EmployeeCode");
            }
            String employeeId = (sharedPreferences.getString("EmpoyeeId", ""));
            String securityToken = (sharedPreferences.getString("Token", ""));
            IMG_URL = Constants.IP_ADDRESS + "/FaceAPIController.ashx?EMPCODE=" + employeeCode + "&" + "EMPID=" + employeeId + "&" + "REMARKS=" + remark + "&" +
                    "SECURITYTOKEN=" + securityToken + "&" + "LATITUDE=" + latitude + "&" + "LONGITUDE=" + longitude + "&"
                    + "LOCATIONADDRESS=" + LocationString + "&" + "GEOSTRING=" + geoString + "&"
                    + "MODE=" + PunchMode + "&" + "ATTDATE=" + ATTDATE + "&" + "COUNTRY=" + countryName;
            Log.e(TAG, "onCreateView: " + IMG_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  fileUploadHelper = new FileUploadHelper(requireActivity(), this, IMG_URL);
        okHttpFileUploadHelper = new OkHttpFileUploadHelper(requireActivity(), this);
        return rootView;
    }

    public void createCameraSource(int cameraId) {
        if (cameraId == 1) {
            camerDirection = CameraSource.CAMERA_FACING_FRONT;
        } else {
            camerDirection = CameraSource.CAMERA_FACING_BACK;
        }
        if (mOrientation != null) {
            mSensorManager.registerListener(mOrientationSensorListener, mOrientation,
                    SensorManager.SENSOR_DELAY_UI);
        }

        detector = new FaceDetector.Builder(getActivity())
                .setProminentFaceOnly(true) // optimize for single, relatively large face
                .setTrackingEnabled(true) // enable face tracking
                .setClassificationType(/* eyes open and smile */ FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE) // for one face this is OK
                .build();


        if (!detector.isOperational()) {
            Log.w("MainActivity", "Detector Dependencies are not yet available");
        } else {
            cameraSource = new CameraSource.Builder(requireActivity(), detector)
                    .setFacing(camerDirection)
                    .setRequestedFps(2.0f)
                    .setRequestedPreviewSize(640, 420)
                    .setAutoFocusEnabled(true)
                    .build();

            detector.setProcessor(new LargestFaceFocusingProcessor(detector, new GraphicFaceTracker()));
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void showFaceResponse(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.face_alert_layout, null);
        dialogBuilder.setView(dialogView);

        CustomTextView tv_faceMessage = dialogView.findViewById(R.id.tv_faceMessage);
        Button face_alertButton = dialogView.findViewById(R.id.face_alertButton);
        tv_faceMessage.setText(message);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        face_alertButton.setOnClickListener(view -> {
            try {
                alertDialog.dismiss();
                requireActivity().finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void onFileUploadSuccess(String result) {
        try {
            Log.e(TAG, "onFileUploadSuccess: " + result);
            if (result.contains(",")) {
                List<String> list = Arrays.asList(result.split(","));
                if (list.size() > 1) {
                    int value = Integer.parseInt(list.get(0));
                    requireActivity().runOnUiThread(() -> {
                        if (value == 1) {
                            startActivity(new Intent(requireActivity(), AttendanceSuccessActivity.class));
                            requireActivity().finish();
                            requireActivity().overridePendingTransition(0, 0);
                        } else if (value == 0) {
                            showFaceResponse(list.get(1));
                        } else if (value == -1) {
                            showFaceResponse(list.get(1));
                        } else if (value == -2) {
                            showFaceResponse(list.get(1));
                        } else if (value == 2) {
                            showFaceResponse(list.get(1));
                        } else {
                            showFaceResponse("Face not clear!");
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(() -> showFaceResponse(result));
                }

            } else {
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(() -> showFaceResponse(result));
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorToServer(employeeId, " Exception in onFileUploadSuccess:  ", e.getMessage(), "Result :" + result, "", "");
        }

    }

    private class GraphicFaceTracker extends Tracker<Face> {

        private final float OPEN_THRESHOLD = 0.85f;
        private final float CLOSE_THRESHOLD = 0.30f;
        private int state = 0;


        private void captureImage() {
            if (cameraSource != null) {
                cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        try {

                            if (cameraSource != null) {
                                cameraSource.stop();
                            }
                            int mOrientation = Exif.getOrientation(bytes);
                            Log.e(TAG, "onPictureTaken: Exif.getOrientation " + mOrientation);
                            if (mOrientation == 0) {
                                int rotation;
                                switch (mOrientationRounded) {
                                    case 1: // This is display orientation
                                        rotation = 0;
                                        break;
                                    case 2:
                                        rotation = 90;
                                        break;
                                    case 3:
                                        rotation = 180;
                                        break;
                                    case 4:
                                        rotation = 270;
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + mOrientationRounded);
                                }

                                Bitmap bitmap = bytesToBitmap(bytes);
                                Bitmap newBitmap = RotateBitmap(bitmap, rotation);
                                //fileUploadHelper.uploadPicture(newBitmap, "MyImage.jpg");
                                okHttpFileUploadHelper.uploadFileWithOKHttp(newBitmap, IMG_URL, "Verifying face...");


                            } else if (mOrientation == 270) {
                                int rotation;
                                switch (mOrientationRounded) {
                                    case 1: // This is display orientation
                                        rotation = 90;
                                        break;
                                    case 2:
                                        rotation = 180;
                                        break;
                                    case 3:
                                        rotation = 270;
                                        break;
                                    case 4:
                                        rotation = 360;
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + mOrientationRounded);
                                }

                                Bitmap bitmap = bytesToBitmap(bytes);
                                Bitmap newBitmap = RotateBitmap(bitmap, rotation);
                                //fileUploadHelper.uploadPicture(newBitmap, "MyImage.jpg");
                                okHttpFileUploadHelper.uploadFileWithOKHttp(newBitmap, IMG_URL, "Verifying face...");
                            } else if (mOrientation == 90) {
                                int rotation;
                                switch (mOrientationRounded) {
                                    case 1: // This is display orientation
                                        rotation = -90;
                                        break;
                                    case 2:
                                        rotation = 90;
                                        break;
                                    case 3:
                                        rotation = 180;
                                        break;
                                    case 4:
                                        rotation = 270;
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + mOrientationRounded);
                                }

                                Bitmap bitmap = bytesToBitmap(bytes);
                                Bitmap newBitmap = RotateBitmap(bitmap, rotation);
                                //fileUploadHelper.uploadPicture(newBitmap, "MyImage.jpg");
                                okHttpFileUploadHelper.uploadFileWithOKHttp(newBitmap, IMG_URL, "Verifying face...");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            sendErrorToServer(employeeId, " Exception in onPictureTaken:  ", e.getMessage(), "", "", "");
                        }

                    }
                });
            }
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {

            float left = face.getIsLeftEyeOpenProbability();
            float right = face.getIsRightEyeOpenProbability();
            float headPosition = face.getEulerY();

            // TODO -  get the value of EulerY to check the rotation of head in order to remove pobject instance error
            face.getEulerY();
            Log.e(TAG, "onUpdate: " + "Set face according to the Euler Face" + face.getEulerY());

            if ((left == Face.UNCOMPUTED_PROBABILITY) || (right == Face.UNCOMPUTED_PROBABILITY)) {
                return;
            }
            //Log.e(TAG, "onUpdate: Left: " + left + " Right: " + right + " smmile probabilty " + face.getIsSmilingProbability() + " landmark " + face.getLandmarks());

            switch (state) {
                case 0:
                    if ((left > OPEN_THRESHOLD) && (right > OPEN_THRESHOLD)) {
                        Log.e(TAG, "onUpdate: " + "Both eye are open");

                        if ((headPosition < 20.0 && headPosition > -20.0))
                            state = 1;
                    }
                    break;

                case 1:
                    if ((left < CLOSE_THRESHOLD) && (right < CLOSE_THRESHOLD)) {
                        Log.e(TAG, "onUpdate: " + "Both eye are closed");

                        if ((headPosition < 20.0 && headPosition > -20.0))
                            state = 2;
                    }
                    break;

                case 2:
                    if ((left > OPEN_THRESHOLD) && (right > OPEN_THRESHOLD)) {
                        state = 0;
                        Log.e(TAG, "onUpdate: " + "face position =" + face.getEulerY());
                        Log.i("BlinkTracker", "blink occurred!");
                        if (progressDialog != null && progressDialog.isShowing()) {
                            Log.e(TAG, "uploading process is running ");
                        } else {
                            Log.e(TAG, "onUpdate: " + " Capture Image is called");
                            captureImage();
                        }
                    }
                    break;
            }

        }

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        startCameraSource();
    }

    public void startCameraSource() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                Log.e(TAG, "surfaceCreated: " + "");
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                Log.e(TAG, "surfaceChanged: " + "");
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                Log.e(TAG, "surfaceDestroyed: " + "");
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mOrientation != null) {
            mSensorManager.unregisterListener(mOrientationSensorListener);
        }
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    public static Bitmap bytesToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCameraPermissionID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraSource();
            } else {
                Toast.makeText(requireContext(), "Camera Permission not granred!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}