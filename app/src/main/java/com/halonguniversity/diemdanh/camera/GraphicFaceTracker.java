package com.halonguniversity.diemdanh.camera;

import android.content.Context;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by redcarpet on 4/17/17.
 */

public class GraphicFaceTracker extends Tracker<Face> {
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;

    GraphicFaceTracker(GraphicOverlay overlay, Context context) {
        mOverlay = overlay;
        mFaceGraphic = new FaceGraphic(overlay, context);
    }

    @Override
    public void onNewItem(int faceId, Face item) {
        mFaceGraphic.setId(faceId);
    }

    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mFaceGraphic);
        mFaceGraphic.updateFace(face);
//        logFaceData(face);
    }

    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mFaceGraphic);
    }

    @Override
    public void onDone() {
        mOverlay.remove(mFaceGraphic);
    }
}