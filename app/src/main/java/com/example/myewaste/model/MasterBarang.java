package com.example.myewaste.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class MasterBarang implements Parcelable {
    private String no_master_barang, nama_master_barang,foto_master_barang;

    public MasterBarang(){};

    public MasterBarang(String no_master_barang,String nama_master_barang, String foto_master_barang){
        this.no_master_barang = no_master_barang;
        this.nama_master_barang = nama_master_barang;
        this.foto_master_barang = foto_master_barang;
    }

    public MasterBarang (Parcel parcel){
        no_master_barang = parcel.readString();
        nama_master_barang = parcel.readString();
        foto_master_barang = parcel.readString();
    }

    public String getNo_master_barang() {
        return no_master_barang;
    }

    public void setNo_master_barang(String no_master_barang) {
        this.no_master_barang = no_master_barang;
    }

    public String getNama_master_barang() {
        return nama_master_barang;
    }

    public void setNama_master_barang(String nama_master_barang) {
        this.nama_master_barang = nama_master_barang;
    }

    public String getFoto_master_barang() {
        return foto_master_barang;
    }

    public void setFoto_master_barang(String foto_master_barang) {
        this.foto_master_barang = foto_master_barang;
    }

    //data yang akan disimpan kedalam firebase
    @NonNull
    @NotNull
    @Override
    public String toString () {
        return nama_master_barang;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(no_master_barang);
        parcel.writeString(nama_master_barang);
        parcel.writeString(foto_master_barang);
    }

    public static final Parcelable.Creator<MasterBarang> CREATOR = new Parcelable.Creator<MasterBarang>() {

        @Override
        public MasterBarang createFromParcel(Parcel parcel) {
            return new MasterBarang(parcel);
        }

        @Override
        public MasterBarang[] newArray(int size) {
            return new MasterBarang[0];
        }
    };
}
