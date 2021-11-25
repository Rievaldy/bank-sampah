package com.example.myewaste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myewaste.model.Saldo;
import com.example.myewaste.model.TransaksiSaldo;
import com.example.myewaste.model.UserData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.myewaste.Util.convertToRupiah;
import static com.example.myewaste.Util.getRegisterCode;
import static com.example.myewaste.Util.showMessage;

public class DetailTransaksiSaldoActivity extends AppCompatActivity {
    private LinearLayout layout;
    private TextView tvIdTransaksi, tvNamaNasabah, tvNamaTeller, tvJumlahPenarikan, tvPotongan,tvTotal, tvStatus, tvTanggal;
    private Button editTransaksi;
    private Button cancelTransaksi;
    private Button approveTransaksi;
    private Button rejectTransaksi;
    private SessionManagement sessionManagement;


    private TransaksiSaldo transaksiSaldo;
    private UserData dataTeller;
    private UserData dataUser;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi_saldo);

        getSupportActionBar().setTitle("Detail Transaksi Saldo");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layout = findViewById(R.id.layout_detail_transaksi_saldo);
        tvIdTransaksi = findViewById(R.id.tv_id_transaksi_dts);
        tvNamaNasabah = findViewById(R.id.tv_nama_nasabah_dts);
        tvNamaTeller = findViewById(R.id.tv_nama_teller_dts);
        tvJumlahPenarikan = findViewById(R.id.tv_jumlah_penarikan_dts);
        tvPotongan = findViewById(R.id.tv_jumlah_potongan_dts);
        tvTotal = findViewById(R.id.tv_total_dts);
        tvStatus = findViewById(R.id.tv_status_dts);
        tvTanggal = findViewById(R.id.tv_tanggal_transaksi_dts);
        editTransaksi = findViewById(R.id.btn_edit_dts);
        cancelTransaksi = findViewById(R.id.btn_cancel_dts);
        approveTransaksi = findViewById(R.id.btn_approve_dts);
        rejectTransaksi = findViewById(R.id.btn_reject_dts);

        transaksiSaldo = getIntent().getParcelableExtra("EXTRA_TRANSAKSI_SALDO");
        sessionManagement = new SessionManagement(getApplicationContext());

        if(!transaksiSaldo.getStatus().equals("PENDING") || getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("sa")){
            approveTransaksi.setVisibility(View.GONE);
            rejectTransaksi.setVisibility(View.GONE);
            editTransaksi.setVisibility(View.GONE);
            cancelTransaksi.setVisibility(View.GONE);
        }else{
            if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("n")){
                approveTransaksi.setVisibility(View.GONE);
                rejectTransaksi.setVisibility(View.GONE);
            }else if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("t")){
                editTransaksi.setVisibility(View.GONE);
                cancelTransaksi.setVisibility(View.GONE);
            }
        }

        editTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(DetailTransaksiSaldoActivity.this, TambahEditTransaksiSaldoActivity.class);
                intent.putExtra("EXTRA_TRANSAKSI_SALDO", transaksiSaldo);
                startActivity(intent);
            }
        });

        cancelTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTransaction();
            }
        });

        approveTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveTransaction();
            }
        });

        rejectTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectTransaction();
            }
        });

        prepareLayout();
    }

    private void prepareLayout(){
        tvIdTransaksi.setText(transaksiSaldo.getId_transaksi_saldo());
        tvJumlahPenarikan.setText(convertToRupiah(transaksiSaldo.getJumlah_transaksi()));
        tvPotongan.setText(convertToRupiah(transaksiSaldo.getPotongan()));
        tvTotal.setText(convertToRupiah(transaksiSaldo.getJumlah_transaksi()-transaksiSaldo.getPotongan()));
        tvStatus.setText(transaksiSaldo.getStatus());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = new Date(transaksiSaldo.getTanggal_transaksi());
        String tanggalTransaksi = sdf.format(date);
        tvTanggal.setText(tanggalTransaksi);
        loadUserData(0, transaksiSaldo.getId_nasabah());
        if(transaksiSaldo.getId_penerima().equals("none")){
            tvNamaTeller.setText(transaksiSaldo.getId_penerima());
        }else{
            loadUserData(1, transaksiSaldo.getId_penerima());
        }
    }

    private void loadUserData(int mode, String idUser){
        //todo mode 0 for user , 1 for teller
        Query userQuery = databaseReference.child("userdata").orderByChild("noregis").equalTo(idUser);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    if(mode == 0){
                        dataUser = data.getValue(UserData.class);
                    }else{
                        dataTeller = data.getValue(UserData.class);
                    }
                }
                if(mode == 0){
                    tvNamaNasabah.setText(dataUser.getNama());
                }else{
                    tvNamaTeller.setText(dataTeller.getNama());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cancelTransaction(){
        DatabaseReference saldoReference = databaseReference.child("saldonasabah").child(transaksiSaldo.getId_nasabah());
        DatabaseReference transaksiSaldoReference = databaseReference.child("transaksi_saldo").child(transaksiSaldo.getId_transaksi_saldo());
        saldoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Saldo saldoNasabah = snapshot.getValue(Saldo.class);
                int currentBalance = saldoNasabah.getSaldo();
                int retriveCanceledBalance = currentBalance + transaksiSaldo.getJumlah_transaksi();
                saldoNasabah.setSaldo(retriveCanceledBalance);
                saldoReference.setValue(saldoNasabah);
                transaksiSaldoReference.getRef().removeValue();
                showMessage(DetailTransaksiSaldoActivity.this, "Berhasil Membatalkan Penarikan Saldo");
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void approveTransaction(){
        DatabaseReference transaksiSaldoReference = databaseReference.child("transaksi_saldo").child(transaksiSaldo.getId_transaksi_saldo());
        transaksiSaldo.setStatus("APPROVED");
        transaksiSaldo.setId_penerima(sessionManagement.getUserSession());
        transaksiSaldoReference.setValue(transaksiSaldo);
        showMessage(DetailTransaksiSaldoActivity.this, "Berhasil Menyetujui Penarikan");
        finish();
    }

    private void rejectTransaction(){
        DatabaseReference transaksiSaldoReference = databaseReference.child("transaksi_saldo").child(transaksiSaldo.getId_transaksi_saldo());
        DatabaseReference saldoReference = databaseReference.child("saldonasabah").child(transaksiSaldo.getId_nasabah());
        transaksiSaldo.setStatus("REJECTED");
        transaksiSaldo.setId_penerima(sessionManagement.getUserSession());
        transaksiSaldoReference.setValue(transaksiSaldo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                saldoReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Saldo saldoNasabah = snapshot.getValue(Saldo.class);
                        int currentBalance = saldoNasabah.getSaldo();
                        saldoNasabah.setSaldo(currentBalance + transaksiSaldo.getJumlah_transaksi());
                        saldoReference.setValue(saldoNasabah);
                        showMessage(DetailTransaksiSaldoActivity.this, "Berhasil Menolak Penarikan");
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
