package com.example.myewaste.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.myewaste.R;
import com.example.myewaste.Util;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;


public class UserData implements Parcelable {
    private String noregis;
    private String fotoProfil;
    private String nik;
    private String nama;
    private String kelamin;
    private String notelp;
    private String alamat;
    private String RT;
    private String RW;


    public UserData() {
    }

    public UserData(String noregis, String fotoProfil, String nik, String nama, String kelamin, String notelp, String alamat, String RT, String RW) {
        this.noregis = noregis;
        this.fotoProfil = fotoProfil;
        this.nik = nik;
        this.nama = nama;
        this.kelamin = kelamin;
        this.notelp = notelp;
        this.alamat = alamat;
        this.RT = RT;
        this.RW = RW;
    }

    public UserData(Parcel parcel) {
        noregis = parcel.readString();
        fotoProfil = parcel.readString();
        nik = parcel.readString();
        nama = parcel.readString();
        kelamin = parcel.readString();
        notelp = parcel.readString();
        alamat = parcel.readString();
        RT = parcel.readString();
        RW = parcel.readString();
    }

    public String getNoregis() {
        return noregis;
    }

    public void setNoregis(String noregis) {
        this.noregis = noregis;
    }

    public String getFotoProfil(){ return fotoProfil;}

    public void setFotoProfil(String fotoProfil){ this.fotoProfil = fotoProfil;}

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelamin() {
        return kelamin;
    }

    public void setKelamin(String kelamin) {
        this.kelamin = kelamin;
    }

    public String getNotelp() {
        return notelp;
    }

    public void setNotelp(String notelp) {
        this.notelp = notelp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getRT() {
        return RT;
    }

    public void setRT(String RT) {
        this.RT = RT;
    }

    public String getRW() {
        return RW;
    }

    public void setRW(String RW) {
        this.RW = RW;
    }


    @NonNull
    @NotNull
    @Override
    public String toString() {
        return nama;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(noregis);
        parcel.writeString(fotoProfil);
        parcel.writeString(nik);
        parcel.writeString(nama);
        parcel.writeString(kelamin);
        parcel.writeString(notelp);
        parcel.writeString(alamat);
        parcel.writeString(RT);
        parcel.writeString(RW);
    }

    public static final Parcelable.Creator<UserData> CREATOR = new Parcelable.Creator<UserData>() {

        @Override
        public UserData createFromParcel(Parcel parcel) {
            return new UserData(parcel);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[0];
        }
    };
}
