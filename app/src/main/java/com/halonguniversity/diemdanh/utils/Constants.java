package com.halonguniversity.diemdanh.utils;

import com.halonguniversity.diemdanh.entities.LoginResponse;
import com.halonguniversity.diemdanh.entities.SinhVien;

import java.util.List;

public class Constants {
    public static List<LoginResponse> loginResponses;
    public static int maloptc;
    public static SinhVien sinhVien;
    public static String url;
    public static float[] string2array(String embFace) {
        String[] emb = embFace.split(" ");
        int n = emb.length;
        float[] embFloat = new float[n];
        for (int i = 0; i < n; i++) {
            embFloat[i] = Float.parseFloat(emb[i]);
        }
        return embFloat;
    }

    public static String array2string(float[] emb) {
        String embFace = "";
        int n = emb.length;
        for (int i = 0; i < n; i++) {
            embFace += (emb[i] + " ");
        }
        return embFace.trim();
    }

    public static double cosineSimilarity(float[] vectorA, float[] vectorB) {
        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
