package com.halonguniversity.diemdanh.entities;

public class SinhVienLopTC {
    private String hoten;
    private Boolean gioitinh;
    private int sobuoidd;


    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getGioitinh() {
        if (gioitinh) return "Nam";
        return "Nữ";
    }

    public void setGioitinh(Boolean gioitinh) {
        this.gioitinh = gioitinh;
    }

    public int getSobuoidd() {
        return sobuoidd;
    }

    public void setSobuoidd(int sobuoidd) {
        this.sobuoidd = sobuoidd;
    }

    public SinhVienLopTC(String hoten, Boolean gioitinh, int sobuoidd) {
        this.hoten = hoten;
        this.gioitinh = gioitinh;
        this.sobuoidd = sobuoidd;
    }
}
