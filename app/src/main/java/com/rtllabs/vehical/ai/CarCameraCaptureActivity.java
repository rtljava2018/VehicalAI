package com.rtllabs.vehical.ai;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Preview;
import com.rtllabs.vehical.ai.base.BaseActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rtllabs.vehical.ai.utils.AppConstants.myPermissions;


public class CarCameraCaptureActivity extends BaseActivity {


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final String IMAGE_PATH = "IMAGE_PATH";
    public static final String RETURN_DATA = "return-data";
    @BindView(R.id.tv_instruction)
    TextView tvInfo;
    @BindView(R.id.txt_hint1)
    TextView tv_camera_hint;
    @BindView(R.id.iv_camera_button)
    ImageView iv_camera_button;
    @BindView(R.id.placeholder)
    ImageView homeCardImage;
    @BindView(R.id.placeholderinfo)
    ImageView placeholderinfo;
    @BindView(R.id.watermark)
    ImageView placeoverlayimage;

   /* @BindView(R.id.watermark)
    ImageView watermark;*/

    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.ll_view_point)
    LinearLayout ll_view_point;
    @BindView(R.id.tv_camera_hint)
    TextView txt_hint1;


    private Uri mCapturedImageURI = null;
    public static final int REQUEST_CAMERA_PERMISSION = 102;
    public static final int REQUEST_CAMERA_IMAGE_SELFIE = 192;
    public static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 101;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 110;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The current location.
     */
    private Location mLocation;

    public static final int REQUEST_FILE = 112;
    private String filepath = "";
    private String filename = "";
    private String TAG = "location";
    private long mCaptureTime;
    private String uriString = "";
    private int carPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_capture_car_photo);
        ButterKnife.bind(this);
        if (getIntent().getExtras()!=null)
        carPosition = getIntent().getExtras().getInt("carposition", 1);

        homeCardImage.setVisibility(View.GONE);
        placeoverlayimage.setVisibility(View.VISIBLE);
        placeholderinfo.setVisibility(View.VISIBLE);
        ll_view_point.setVisibility(View.GONE);
        camera.setLifecycleOwner(this);
        camera.addCameraListener(new Listener());

        switch (carPosition) {
            case 1:
                placeholderinfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_frontview));
                placeoverlayimage.setBackgroundResource( R.drawable.ic_carfront);
                tvInfo.setText("Please take a picture of the font side view of your vehicle");
                tv_camera_hint.setText("Front View");
                break;
            case 2:
                placeholderinfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_sideview));
                placeoverlayimage.setBackgroundResource( R.drawable.ic_carside_view);
                tvInfo.setText("Please take a picture of the side view of your vehicle");
                placeoverlayimage.setVisibility(View.VISIBLE);
               // txt_hint1.setVisibility(View.VISIBLE);
               // ll_view_point.setVisibility(View.GONE);
                tv_camera_hint.setText("Side View");
                break;
            case 3:
                placeholderinfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_backview));
                placeoverlayimage.setBackgroundResource( R.drawable.ic_carfront);
                tvInfo.setText("Please take a picture of the back side view of your vehicle, focus as per screen");
                tv_camera_hint.setText("Back View");
                break;
            case 4:
                placeholderinfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_steering));
                placeoverlayimage.setBackgroundResource( R.drawable.ic_tyre);
                tvInfo.setText("Please take a picture of the steering view of your vehicle");
                tv_camera_hint.setText("Steering View");
                break;
            case 5:
                placeholderinfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_tyer));
                placeoverlayimage.setBackgroundResource( R.drawable.ic_tyre);
                tvInfo.setText("Please take a picture of the Tyres view of your vehicle");
                tv_camera_hint.setText("Tyres View");
                break;
            case 6:
                placeholderinfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_odometer));
                placeoverlayimage.setBackgroundResource( R.drawable.ic_odometer);
                tvInfo.setText("if required later will do");
                tv_camera_hint.setText("oddometer");
                break;

        }

        iv_camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureVideoSnapshot();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        super.onBackPressed();
    }

    private void toggleCamera() {
        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        switch (camera.toggleFacing()) {
            case BACK:
                // message("Switched to back camera!", false);
                //camera.setFacing();
                break;

            case FRONT:
                //message("Switched to front camera!", false);
                break;
        }
    }

    private class Listener extends CameraListener {

        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {
        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            // message("Got CameraException #" + exception.getReason(), true);
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            if (camera.isTakingVideo()) {
                //message("Captured while taking video. Size=" + result.getSize(), false);
                return;
            }

            // This can happen if picture was taken with a gesture.
            long callbackTime = System.currentTimeMillis();
            if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
            Log.e("onPictureTaken called! Launching activity. Delay:", "" + (callbackTime - mCaptureTime));
            Bitmap bitmap = BitmapFactory.decodeByteArray(result.getData(), 0, result.getData().length);
            homeCardImage.setVisibility(View.VISIBLE);
            placeholderinfo.setVisibility(View.GONE);
            homeCardImage.setImageBitmap(bitmap);
            mCaptureTime = 0;

        }

        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            super.onVideoTaken(result);
        }

        @Override
        public void onVideoRecordingStart() {
            super.onVideoRecordingStart();
        }

        @Override
        public void onVideoRecordingEnd() {
            super.onVideoRecordingEnd();
            // message("Video taken. Processing...", false);
            //LOG.w("onVideoRecordingEnd!");
        }

        @Override
        public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers);
            // message("Exposure correction:" + newValue, false);
        }

        @Override
        public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);
            //  message("Zoom:" + newValue, false);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    private void captureVideoSnapshot() {
        if (camera.isTakingPicture()) {
            //message("Already taking video.", false);
            return;
        }
        if (camera.getPreview() != Preview.GL_SURFACE) {
            //message("Video snapshots are only allowed with the GL_SURFACE preview.", true);
            return;
        }
        //message("Recording snapshot for 5 seconds...", false);
//        camera.takeVideoSnapshot(new File(getFilesDir(), "video.mp4"), 5000);
        String name = "vid_" + System.currentTimeMillis() + "" + "video.mp4";

        /*camera.takeVideoSnapshot(new File(getAlbumStorageDir("vediocapture", name), name), 5000);*/
        camera.takePicture();
        // startCounterTimer();
    }

    private File getAlbumStorageDir(String signaturePad, String filename1) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/RISK/VIRUS", signaturePad);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String mImageName = filename1;
        uriString = (mediaStorageDir.getAbsolutePath() + "/" + mImageName);
        filename = filename1;
        filepath = uriString;
        return mediaStorageDir;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void goToNextStep() {
        Bundle bundle = new Bundle();

       /* bundle.putString("selfiname", filename);
        bundle.putString("selfipath", filepath);
        getCommonApi().openNewScreen(SignActivity.class, bundle);*/
        Intent intent = new Intent();
        String message = uriString;
        intent.putExtra("name", filename);
        intent.putExtra("path", message);
        // intent.putExtra("name_id", item.getKsrac_word_code());
        //  setResult(AppConstants.SELFICAMERAVEDIO, intent);
        finish();
    }

    private void getSelfieImage() {
        //cardSelfie.setVisibility(View.GONE);
//        selfieLayoutTaken.setVisibility(View.VISIBLE);
        //bottomLayout.setVisibility(View.VISIBLE);
    }

   /* @OnClick({R.id.tv_page, R.id.imgnotification, R.id.imgswitch, R.id.retake_selfie, R.id.ctn_btn_vedio, R.id.skip_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {


            case R.id.ctn_btn_vedio:
                captureVideoSnapshot();
              //  generateNumber();
              //  frmBtn.setVisibility(View.GONE);
               // llProgress.setVisibility(View.VISIBLE);
                // goToNextStep();
                break;
            case R.id.skip_txt:
                break;
        }
    }*/


    private void callforselfi() {
        if (!checkPermissionAvl()) {
            getCommonApi().checkPermissions(myPermissions);
            //Toast.makeText(getApplicationContext(), "Please check Permission", Toast.LENGTH_SHORT).show();
        } else {
            //launchCameraActionselfie();
        }
    }

    public boolean checkPermissionAvl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean resultPermission = true;
            int myPermFlag = 0;
            for (String myPermission : myPermissions) {
                if ((ActivityCompat.checkSelfPermission(this, myPermission) != PackageManager.PERMISSION_GRANTED)) {
                    myPermFlag = 1;
                    break;
                }
            }
            switch (myPermFlag) {
                case 0:
                    //continueLoading();
                    resultPermission = true;
                    break;
                case 1:
                    resultPermission = false;

                    break;
            }
            return resultPermission;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 34: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // getCurrentLOc();

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 5101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 199:
                int flag = 0;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == -1) {
                        flag = -1;
                        break;
                    }
                }
                if (flag == -1)
                    new AlertDialog.Builder(this)
                            .setTitle("Permissions needed.")
                            .setMessage("Please allow "
                                    + getResources().getString(R.string.app_name)
                                    + " to access location, storage, phone call and camera.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    // checkPermissionFortarck();
                                }
                            })
                            .create()
                            .show();
        }
    }

    private boolean isImageSupported(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        if (actualHeight == 0 || actualWidth == 0) {

            Toast.makeText(getApplicationContext(), "We are sorry, the file format is not supported, Please select valid file", Toast.LENGTH_SHORT).show();

            return false;
        } else {
            return true;
        }
        //return false;
    }

    public static String getFilename(String filename) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/RISK/VIRUS");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String mImageName = filename;
        String uriString = (mediaStorageDir.getAbsolutePath() + "/" + mImageName);
        return uriString;
    }
}