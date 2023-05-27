package com.halonguniversity.diemdanh.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.halonguniversity.diemdanh.R;
import com.halonguniversity.diemdanh.adapters.SinhVienTCAdapter;
import com.halonguniversity.diemdanh.camera.CameraSourcePreview;
import com.halonguniversity.diemdanh.camera.GraphicFaceTrackerFactory;
import com.halonguniversity.diemdanh.camera.GraphicOverlay;
import com.halonguniversity.diemdanh.entities.SinhVien;
import com.halonguniversity.diemdanh.entities.SinhVienLopTC;
import com.halonguniversity.diemdanh.ml.Facenet;
import com.halonguniversity.diemdanh.ml.Model;
import com.halonguniversity.diemdanh.service.ApiService;
import com.halonguniversity.diemdanh.utils.Constants;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CapNhatFaceActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cap_nhat_face);
        initView();
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        faceDetector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(this)
                    .setMessage("Face Detector could not be set up on your device :(")
                    .show();
        }
        findViewById(R.id.btn_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                predict();
            }
        });
    }

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


    FaceDetector faceDetector;

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

    private void predict() {
        ProgressDialog dialog = new ProgressDialog(CapNhatFaceActivity.this);
        dialog.setTitle("Đang xử lý");
        dialog.show();
        mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(@NonNull byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap face = detectFace(bitmap);
                TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_X, 3}, DataType.FLOAT32);
                ByteBuffer byteBuffer = bitmapToBuffer(face);
                inputFeature.loadBuffer(byteBuffer);
                Facenet.Outputs outputs = model.process(inputFeature);
                float[] embFace = outputs.getOutputFeature0AsTensorBuffer().getFloatArray();
                Constants.sinhVien.setEmbFace(Constants.array2string(embFace));
                Log.e("TAG", "onPictureTaken: " + Constants.sinhVien.getEmbFace());
                Map<String, Object> map = new HashMap<>();
                map.put("masv", Constants.sinhVien.getMasv());
                map.put("EmbFace", Constants.sinhVien.getEmbFace());
                ApiService.api.updateSinhVien(map).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Thất bại", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Kết nối thất bại", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }
        });
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


    private void initView() {
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        try {
            model = Facenet.newInstance(CapNhatFaceActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory(mGraphicOverlay, CapNhatFaceActivity.this))
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