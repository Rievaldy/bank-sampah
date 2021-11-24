package com.example.myewaste.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class TransaksiBarang implements Parcelable {
    private String no_nasabah,nomor_jenis_barang,no_teller,no_transaksi_barang,no_transaksi_saldo, keterangan;
    private long tanggal_transaksi;
    private int jumlah, total_harga;

    public TransaksiBarang(){}

    public TransaksiBarang(String no_nasabah, String nomor_jenis_barang, String no_teller, String no_transaksi_barang, String no_transaksi_saldo, String keterangan, long tanggal_transaksi, int jumlah, int total_harga) {
        this.no_nasabah = no_nasabah;
        this.nomor_jenis_barang = nomor_jenis_barang;
        this.no_teller = no_teller;
        this.no_transaksi_barang = no_transaksi_barang;
        this.no_transaksi_saldo = no_transaksi_saldo;
        this.keterangan = keterangan;
        this.tanggal_transaksi = tanggal_transaksi;
        this.jumlah = jumlah;
        this.total_harga = total_harga;
    }

    protected TransaksiBarang(Parcel in) {
        no_nasabah = in.readString();
        nomor_jenis_barang = in.readString();
        no_teller = in.readString();
        no_transaksi_barang = in.readString();
        no_transaksi_saldo = in.readString();
        keterangan = in.readString();
        tanggal_transaksi = in.readLong();
        jumlah = in.readInt();
        total_harga = in.readInt();
    }

    public static final Creator<TransaksiBarang> CREATOR = new Creator<TransaksiBarang>() {
        @Override
        public TransaksiBarang createFromParcel(Parcel in) {
            return new TransaksiBarang(in);
        }

        @Override
        public TransaksiBarang[] newArray(int size) {
            return new TransaksiBarang[size];
        }
    };

    public String getNo_nasabah() {
        return no_nasabah;
    }

    public void setNo_nasabah(String no_nasabah) {
        this.no_nasabah = no_nasabah;
    }

    public String getNomor_jenis_barang() {
        return nomor_jenis_barang;
    }

    public void setNomor_jenis_barang(String nomor_jenis_barang) {
        this.nomor_jenis_barang = nomor_jenis_barang;
    }

    public String getNo_teller() {
        return no_teller;
    }

    public void setNo_teller(String no_teller) {
        this.no_teller = no_teller;
    }

    public String getNo_transaksi_barang() {
        return no_transaksi_barang;
    }

    public void setNo_transaksi_barang(String no_transaksi_barang) {
        this.no_transaksi_barang = no_transaksi_barang;
    }

    public String getNo_transaksi_saldo() {
        return no_transaksi_saldo;
    }

    public void setNo_transaksi_saldo(String no_transaksi_saldo) {
        this.no_transaksi_saldo = no_transaksi_saldo;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public long getTanggal_transaksi() {
        return tanggal_transaksi;
    }

    public void setTanggal_transaksi(long tanggal_transaksi) {
        this.tanggal_transaksi = tanggal_transaksi;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getTotal_harga() {
        return total_harga;
    }

    public void setTotal_harga(int total_harga) {
        this.total_harga = total_harga;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(no_nasabah);
        parcel.writeString(nomor_jenis_barang);
        parcel.writeString(no_teller);
        parcel.writeString(no_transaksi_barang);
        parcel.writeString(no_transaksi_saldo);
        parcel.writeString(keterangan);
        parcel.writeLong(tanggal_transaksi);
        parcel.writeInt(jumlah);
        parcel.writeInt(total_harga);
    }
}
