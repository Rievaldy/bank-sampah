package com.example.myewaste.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Saldo implements Parcelable {
    private String noregis;
    private int saldo;


    public Saldo() {
    }

    public Saldo(String noregis, int saldo) {
        this.noregis = noregis;
        this.saldo = saldo;
    }

    public Saldo (Parcel parcel){
        noregis = parcel.readString();
        saldo = parcel.readInt();

    }



    public String getNoregis() {
        return noregis;
    }

    public void setNoregis(String noregis) {
        this.noregis = noregis;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(noregis);
        parcel.writeInt(saldo);
    }

    public static final Creator<Saldo> CREATOR = new Creator<Saldo>() {
        @Override
        public Saldo createFromParcel(Parcel in) {
            return new Saldo(in);
        }

        @Override
        public Saldo[] newArray(int size) {
            return new Saldo[size];
        }
    };
}
