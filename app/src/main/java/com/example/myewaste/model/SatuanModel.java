package com.example.myewaste.model;


public class SatuanModel {
    String noSatuan;
    String namaSatuan;

    public SatuanModel(){

    }

    public SatuanModel(String noSatuan, String namaSatuan) {
        this.noSatuan = noSatuan;
        this.namaSatuan = namaSatuan;
    }

    public String getNoSatuan() {
        return noSatuan;
    }

    public void setNoSatuan(String noSatuan) {
        this.noSatuan = noSatuan;
    }

    public String getNamaSatuan() {
        return namaSatuan;
    }

    public void setNamaSatuan(String namaSatuan) {
        this.namaSatuan = namaSatuan;
    }

    @Override
    public String toString() {
        return namaSatuan;
    }
}


