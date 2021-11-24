package com.example.myewaste.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class User implements Parcelable {
    public String username;
    public String password;
    public String noregis;
    public String status;

    public User(String username, String password, String noregis, String status) {
        this.username = username;
        this.password = password;
        this.noregis = noregis;
        this.status = status;
    }

    public User() {

    }

    public User(Parcel parcel) {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNoregis() {
        return noregis;
    }

    public void setNoregis(String noregis) {
        this.noregis = noregis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(noregis);
        parcel.writeString(status);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };

    //data yang akan disimpan kedalam firebase
    @NonNull
    @NotNull
    @Override
    public String toString () {
        return " "+username+"\n "+
                " "+password+"\n "+
                " "+noregis+"\n "+
                " "+status;
    }
}
