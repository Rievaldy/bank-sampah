package com.example.myewaste.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TransaksiSaldo implements Parcelable {
    private String id_transaksi_saldo, id_nasabah, jenis_transaksi, id_penerima, status ;
    private int jumlah_transaksi, potongan;
    private long tanggal_transaksi;

    public TransaksiSaldo() {
    }

    public TransaksiSaldo(String id_transaksi_saldo, String id_nasabah, String jenis_transaksi, String id_penerima, String status, int jumlah_transaksi, int potongan, long tanggal_transaksi) {
        this.id_transaksi_saldo = id_transaksi_saldo;
        this.id_nasabah = id_nasabah;
        this.jenis_transaksi = jenis_transaksi;
        this.id_penerima = id_penerima;
        this.status = status;
        this.jumlah_transaksi = jumlah_transaksi;
        this.potongan = potongan;
        this.tanggal_transaksi = tanggal_transaksi;
    }

    protected TransaksiSaldo(Parcel in) {
        id_transaksi_saldo = in.readString();
        id_nasabah = in.readString();
        jenis_transaksi = in.readString();
        id_penerima = in.readString();
        status = in.readString();
        jumlah_transaksi = in.readInt();
        potongan = in.readInt();
        tanggal_transaksi = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id_transaksi_saldo);
        dest.writeString(id_nasabah);
        dest.writeString(jenis_transaksi);
        dest.writeString(id_penerima);
        dest.writeString(status);
        dest.writeInt(jumlah_transaksi);
        dest.writeInt(potongan);
        dest.writeLong(tanggal_transaksi);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransaksiSaldo> CREATOR = new Creator<TransaksiSaldo>() {
        @Override
        public TransaksiSaldo createFromParcel(Parcel in) {
            return new TransaksiSaldo(in);
        }

        @Override
        public TransaksiSaldo[] newArray(int size) {
            return new TransaksiSaldo[size];
        }
    };

    public String getId_transaksi_saldo() {
        return id_transaksi_saldo;
    }

    public void setId_transaksi_saldo(String id_transaksi_saldo) {
        this.id_transaksi_saldo = id_transaksi_saldo;
    }

    public String getId_nasabah() {
        return id_nasabah;
    }

    public void setId_nasabah(String id_nasabah) {
        this.id_nasabah = id_nasabah;
    }

    public String getJenis_transaksi() {
        return jenis_transaksi;
    }

    public void setJenis_transaksi(String jenis_transaksi) {
        this.jenis_transaksi = jenis_transaksi;
    }

    public String getId_penerima() {
        return id_penerima;
    }

    public void setId_penerima(String id_penerima) {
        this.id_penerima = id_penerima;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getJumlah_transaksi() {
        return jumlah_transaksi;
    }

    public void setJumlah_transaksi(int jumlah_transaksi) {
        this.jumlah_transaksi = jumlah_transaksi;
    }

    public int getPotongan() {
        return potongan;
    }

    public void setPotongan(int potongan) {
        this.potongan = potongan;
    }

    public long getTanggal_transaksi() {
        return tanggal_transaksi;
    }

    public void setTanggal_transaksi(long tanggal_transaksi) {
        this.tanggal_transaksi = tanggal_transaksi;
    }
}
