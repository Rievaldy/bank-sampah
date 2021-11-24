package com.example.myewaste.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MasterJenisBarang implements Parcelable {
    private String no_master_barang, no_master_jenis_barang, nama_master_jenis_barang, no_satuan_barang;
    private int harga;

    public MasterJenisBarang() {
    }

    public MasterJenisBarang(String no_master_barang, String no_master_jenis_barang, String nama_master_jenis_barang, String no_satuan_barang, int harga) {
        this.no_master_barang = no_master_barang;
        this.no_master_jenis_barang = no_master_jenis_barang;
        this.nama_master_jenis_barang = nama_master_jenis_barang;
        this.no_satuan_barang = no_satuan_barang;
        this.harga = harga;
    }

    protected MasterJenisBarang(Parcel in) {
        no_master_barang = in.readString();
        no_master_jenis_barang = in.readString();
        nama_master_jenis_barang = in.readString();
        no_satuan_barang = in.readString();
        harga = in.readInt();
    }

    public static final Creator<MasterJenisBarang> CREATOR = new Creator<MasterJenisBarang>() {
        @Override
        public MasterJenisBarang createFromParcel(Parcel in) {
            return new MasterJenisBarang(in);
        }

        @Override
        public MasterJenisBarang[] newArray(int size) {
            return new MasterJenisBarang[size];
        }
    };

    public String getNo_master_barang() {
        return no_master_barang;
    }

    public void setNo_master_barang(String no_master_barang) {
        this.no_master_barang = no_master_barang;
    }

    public String getNo_master_jenis_barang() {
        return no_master_jenis_barang;
    }

    public void setNo_master_jenis_barang(String no_master_jenis_barang) {
        this.no_master_jenis_barang = no_master_jenis_barang;
    }

    public String getNama_master_jenis_barang() {
        return nama_master_jenis_barang;
    }

    public void setNama_master_jenis_barang(String nama_master_jenis_barang) {
        this.nama_master_jenis_barang = nama_master_jenis_barang;
    }

    public String getNo_satuan_barang() {
        return no_satuan_barang;
    }

    public void setNo_satuan_barang(String no_satuan_barang) {
        this.no_satuan_barang = no_satuan_barang;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    @Override
    public String toString() {
        return nama_master_jenis_barang;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(no_master_barang);
        parcel.writeString(no_master_jenis_barang);
        parcel.writeString(nama_master_jenis_barang);
        parcel.writeString(no_satuan_barang);
        parcel.writeInt(harga);
    }
}

