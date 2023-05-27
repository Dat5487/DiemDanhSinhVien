package com.halonguniversity.diemdanh.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.halonguniversity.diemdanh.R;
import com.halonguniversity.diemdanh.camera.CameraSourcePreview;
import com.halonguniversity.diemdanh.camera.GraphicFaceTrackerFactory;
import com.halonguniversity.diemdanh.camera.GraphicOverlay;
import com.halonguniversity.diemdanh.entities.SinhVien;
import com.halonguniversity.diemdanh.ml.Facenet;
import com.halonguniversity.diemdanh.ml.Model;
import com.halonguniversity.diemdanh.ml.ModelLiveNess;
import com.halonguniversity.diemdanh.service.ApiService;
import com.halonguniversity.diemdanh.utils.Constants;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiemDanhActivity extends AppCompatActivity {
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    int DIM_BATCH_SIZE = 1;
    int DIM_IMG_SIZE_X = 160;
    int DIM_IMG_SIZE_Y = 160;
    int DIM_PIXEL_SIZE = 3;
    private static final float IMAGE_MEAN = 127.5f;
    private static final float IMAGE_STD = 127.5f;
    Facenet model;
    ModelLiveNess modelLiveNess;
    List<SinhVien> sinhVienList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diem_danh);
        initView();
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        faceDetector = new FaceDetector.Builder(DiemDanhActivity.this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(DiemDanhActivity.this)
                    .setMessage("Face Detector could not be set up on your device :(")
                    .show();
        }
        loadDSSV();
        predict();
    }

    TextView tv_hs;

    private void predict() {
        ProgressDialog dialog = new ProgressDialog(DiemDanhActivity.this);
        dialog.setTitle("Đang nhận diện");
        findViewById(R.id.btn_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(@NonNull byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (detectLiveness(bitmap, 0.5f)) {
                            Bitmap face = detectFace(bitmap);
                            TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_X, 3}, DataType.FLOAT32);
                            ByteBuffer byteBuffer = bitmapToBuffer(face);
                            inputFeature.loadBuffer(byteBuffer);
                            Facenet.Outputs outputs = model.process(inputFeature);
                            float[] embFace = outputs.getOutputFeature0AsTensorBuffer().getFloatArray();
                            SinhVien sinhVien = findSinhVien(embFace);
                            if (sinhVien != null) {
                                tv_hs.setText("Sinh viên " + sinhVien.getHoten());
                                ApiService.api.diemDanh(sinhVien.getMasv(), Constants.maloptc).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), sinhVien.getHoten() + " đã được điểm danh", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "điểm danh " + sinhVien.getHoten() + "thất bại", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(DiemDanhActivity.this, "Phát hiện gian lận", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                });
            }
        });

    }

    private boolean detectLiveness(Bitmap bitmap, float threshold) {
        TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{DIM_BATCH_SIZE, 224, 224, 3}, DataType.FLOAT32);
        ByteBuffer byteBuffer = bitmapToBufferLiveness(bitmap);
        inputFeature.loadBuffer(byteBuffer);
        ModelLiveNess.Outputs outputs = modelLiveNess.process(inputFeature);
        float[] results = outputs.getOutputFeature0AsTensorBuffer().getFloatArray();
        if (results[0] < threshold)
            return true;
        return false;
    }

    float THRESHOLD_FACE = 0.9f;

    private SinhVien findSinhVien(float[] embFace) {
        SinhVien result = null;
        if(sinhVienList!=null){
            for (SinhVien sinhVien : sinhVienList
            ) {
                if (sinhVien.getEmbFace() != null) {
                    float[] embSV = Constants.string2array(sinhVien.getEmbFace());
                    Log.e("cosine", "findSinhVien: " + Constants.cosineSimilarity(embFace, embSV));
                    if (Constants.cosineSimilarity(embFace, embSV) >= THRESHOLD_FACE)
                        result = sinhVien;
                }

            }
        }

        return result;
    }

    FaceDetector faceDetector;

    private Bitmap detectFace(Bitmap bitmap) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;
        Bitmap defaultBitmap = bitmap;
        Paint rectPaint = new Paint();
        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.CYAN);
        rectPaint.setStyle(Paint.Style.STROKE);

        Bitmap temporaryBitmap = Bitmap.createBitmap(defaultBitmap.getWidth(), defaultBitmap
                .getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(temporaryBitmap);
        canvas.drawBitmap(defaultBitmap, 0, 0, null);


        Frame frame = new Frame.Builder().setBitmap(defaultBitmap).build();
        SparseArray<Face> sparseArray = faceDetector.detect(frame);
        float left = 0, right = 0, top = 0, bottom = 0;
        for (int i = 0; i < sparseArray.size(); i++) {
            Face face = sparseArray.valueAt(i);
            left = face.getPosition().x;
            top = face.getPosition().y;
            right = left + face.getWidth();
            bottom = top + face.getHeight();
            float cornerRadius = 2.0f;
            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, rectPaint);
        }
        if (left < 0 || top < 0 || bottom > bitmap.getHeight() || right > bitmap.getWidth() || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {

            return bitmap;
        } else {
            if (right - left <= 0 || bottom - top <= 0) {

                return bitmap;
            } else {
                Bitmap result = Bitmap.createBitmap(bitmap, (int) left, (int) top, (int) right - (int) left, (int) bottom - (int) top);

                return result;
            }
        }
    }
//    private List<Bitmap> detectFace(Bitmap bitmap) {
//        List<Bitmap> faces = new ArrayList<>();
//        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//        SparseArray<Face> sparseArray = faceDetector.detect(frame);
//        for (int i = 0; i < sparseArray.size(); i++) {
//            Face face = sparseArray.valueAt(i);
//            float left = face.getPosition().x;
//            float top = face.getPosition().y;
//            if (left < 0) {
//                left = 0;
//            }
//            if (top < 0) {
//                top = 0;
//            }
//            Bitmap result = Bitmap.createBitmap(bitmap, (int) left, (int) top, (int) face.getWidth(), (int) face.getHeight());
//            faces.add(result);
//        }
//        return faces;
//
//    }



    private ByteBuffer bitmapToBufferLiveness(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, false);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * DIM_PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[224 * 224];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < 224; i++) {
            for (int j = 0; j < 224; j++) {
                int input = intValues[pixel++];
                byteBuffer.putFloat((((input >> 16 & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
                byteBuffer.putFloat((((input >> 8 & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
                byteBuffer.putFloat((((input & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
            }
        }
        return byteBuffer;
    }
    private ByteBuffer bitmapToBuffer(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, false);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; i++) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; j++) {
                int input = intValues[pixel++];
                byteBuffer.putFloat((((input >> 16 & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
                byteBuffer.putFloat((((input >> 8 & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
                byteBuffer.putFloat((((input & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
            }
        }
        return byteBuffer;
    }

    private void loadDSSV() {
        ApiService.api.getAllSV().enqueue(new Callback<List<SinhVien>>() {
            @Override
            public void onResponse(Call<List<SinhVien>> call, Response<List<SinhVien>> response) {
                if (response.isSuccessful()) {
                    sinhVienList = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<SinhVien>> call, Throwable t) {

            }
        });
    }

    private void initView() {
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        try {
            model = Facenet.newInstance(DiemDanhActivity.this);
            modelLiveNess = ModelLiveNess.newInstance(DiemDanhActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv_hs = findViewById(R.id.tv_hoc_sinh);
    }


    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory(mGraphicOverlay, DiemDanhActivity.this))
                        .build());
        mCameraSource = new CameraSource.Builder(context, detector)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedPreviewSize(1280, 720)
                .setRequestedFps(30.0f)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            createCameraSource();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage("Khong duoc cap quyen camera")
                .setPositiveButton("OK", listener)
                .show();
    }


    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

}