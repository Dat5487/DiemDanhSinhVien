package com.halonguniversity.diemdanh.entities;

public class SinhVien {
    private int masv;
    private String hoten;
    private boolean gioitinh;
    private int malophc;
    private String khoa;
    private String EmbFace;

    public SinhVien(int masv, String hoten, boolean gioitinh, int malophc, String khoa, String embFace) {
        this.masv = masv;
        this.hoten = hoten;
        this.gioitinh = gioitinh;
        this.malophc = malophc;
        this.khoa = khoa;
        EmbFace = embFace;
    }

    public int getMasv() {
        return masv;
    }

    public void setMasv(int masv) {
        this.masv = masv;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public boolean isGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(boolean gioitinh) {
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
