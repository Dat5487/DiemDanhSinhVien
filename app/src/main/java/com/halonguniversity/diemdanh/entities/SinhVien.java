package com.halonguniversity.diemdanh.entities;

public class SinhVien {
    private String masv;
    private String hoten;
    private String gioitinh;
    private int malophc;
    private String khoa;
    private String tenlophc;
    private String EmbFace;

    public SinhVien(String masv, String hoten, String gioitinh, int malophc, String khoa, String tenlophc, String embFace) {
        this.masv = masv;
        this.hoten = hoten;
        this.gioitinh = gioitinh;
        this.malophc = malophc;
        this.khoa = khoa;
        this.tenlophc = tenlophc;
        EmbFace = embFace;
    }

    public SinhVien(String masv, String hoten, String gioitinh, int malophc, String khoa, String embFace) {
        this.masv = masv;
        this.hoten = hoten;
        this.gioitinh = gioitinh;
        this.malophc = malophc;
        this.khoa = khoa;
        EmbFace = embFace;
    }

    public String getTenlophc() {
        return tenlophc;
    }

    public void setTenlophc(String tenlophc) {
        this.tenlophc = tenlophc;
    }

    public String getMasv() {
        return masv;
    }

    public void setMasv(String masv) {
        this.masv = masv;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

    public int getMalophc() {
        return malophc;
    }

    public void setMalophc(int malophc) {
        this.malophc = malophc;
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public String getEmbFace() {
        return EmbFace;
    }

    public void setEmbFace(String embFace) {
        EmbFace = embFace;
    }
}