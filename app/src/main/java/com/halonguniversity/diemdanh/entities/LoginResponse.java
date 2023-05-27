package com.halonguniversity.diemdanh.entities;

public class LoginResponse {
    private int maloptc;
    private String mahp, tenltc;
    private int magv;
    private boolean trangthai;
    private int sttlop;

    public LoginResponse() {
    }

    public LoginResponse(int maloptc, String mahp, String tenltc, int magv, boolean trangthai, int sttlop) {
        this.maloptc = maloptc;
        this.mahp = mahp;
        this.tenltc = tenltc;
        this.magv = magv;
        this.trangthai = trangthai;
        this.sttlop = sttlop;
    }

    public int getMaloptc() {
        return maloptc;
    }

    public void setMaloptc(int maloptc) {
        this.maloptc = maloptc;
    }

    public String getMahp() {
        return mahp;
    }

    public void setMahp(String mahp) {
        this.mahp = mahp;
    }

    public String getTenltc() {
        return tenltc;
    }

    public void setTenltc(String tenltc) {
        this.tenltc = tenltc;
    }

    public int getMagv() {
        return magv;
    }

    public void setMagv(int magv) {
        this.magv = magv;
    }

    public boolean isTrangthai() {
        return trangthai;
    }

    public void setTrangthai(boolean trangthai) {
        this.trangthai = trangthai;
    }

    public int getSttlop() {
        return sttlop;
    }

    public void setSttlop(int sttlop) {
        this.sttlop = sttlop;
    }
}
