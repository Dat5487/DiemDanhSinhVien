package com.halonguniversity.diemdanh.camera;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;


/**
 * Created by redcarpet on 4/18/17.
 */

public class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
    GraphicOverlay overlay;
    Context context;

    public GraphicFaceTrackerFactory(GraphicOverlay graphicOverlay, Context context) {
        this.context = context;
        this.overlay = graphicOverlay;

    }

    @Override
    public Tracker<Face> create(Face face) {
        return new GraphicFaceTracker(overlay, context);
    }
}
