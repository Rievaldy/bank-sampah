package com.example.myewaste;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myewaste.model.Saldo;
import com.example.myewaste.model.TransaksiSaldo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.myewaste.Util.convertToRupiah;
import static com.example.myewaste.Util.increseNumber;
import static com.example.myewaste.Util.showMessage;

public class TambahEditTransaksiSaldoActivity extends AppCompatActivity {

    private TextView tvSaldo, tvPotongan, tvtotal;
    private EditText jumlahPenarikan;
    private Button btnDoPenarikan;
    private TransaksiSaldo transaksiSaldo;
    public static final String DEFAULT_KODE_TRANSAKSI_SALDO = "TRS-0001";
    private DatabaseReference databaseReference;
    private SessionManagement sessionManagement;
    private int saldoNasabah = 0;
    private int mode = 0; //mode 0 = tambah, 1 = edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_edit_transaksi_saldo);
        tvSaldo = findViewById(R.id.tv_saldo);
        tvPotongan = findViewById(R.id.tv_potongan);
        tvtotal = findViewById(R.id.tv_total_diterima);
        jumlahPenarikan = findViewById(R.id.txt_jumlah_penarikan);
        btnDoPenarikan = findViewById(R.id.btnLakukanPenarikan);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sessionManagement = new SessionManagement(getApplicationContext());

        getSupportActionBar().setTitle("Tarik Saldo Saya");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("EXTRA_TRANSAKSI_SALDO")){
            getSupportActionBar().setTitle("Edit Penarikan Nasabah");
            mode = 1;
            transaksiSaldo = getIntent().getParcelableExtra("EXTRA_TRANSAKSI_SALDO");
            jumlahPenarikan.setText(String.valueOf(transaksiSaldo.getJumlah_transaksi()));
            setPotonganAndTotal(transaksiSaldo.getJumlah_transaksi());
        }else{
            transaksiSaldo = new TransaksiSaldo();
        }
        getSaldoNasabah();

        btnDoPenarikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jumlahPenarikan.getText().toString().isEmpty()){
                    showMessage(TambahEditTransaksiSaldoActivity.this, "Jumlah Tidak boleh Kosong");
                }else if(Integer.valueOf(jumlahPenarikan.getText().toString()) > saldoNasabah || Integer.valueOf(jumlahPenarikan.getText().toString()) <= 0){
                    showMessage(TambahEditTransaksiSaldoActivity.this, "Maaf Saldo Anda Tidak Cukup untuk melakukan penarikan tersebut");
                }else{
                    if(mode == 0){
                        fetchDataTransaksiSaldo(Integer.valueOf(jumlahPenarikan.getText().toString()));
                    }else{
                        createTransaksiSaldo(Integer.valueOf(jumlahPenarikan.getText().toString()), transaksiSaldo.getId_transaksi_saldo(), transaksiSaldo.getJumlah_transaksi());
                    }
                    finish();
                }
            }
        });

        jumlahPenarikan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.equals("")){
                    if(!jumlahPenarikan.getText().toString().isEmpty()){
                        setPotonganAndTotal(Integer.valueOf(jumlahPenarikan.getText().toString()));
                    }else{
                        tvPotongan.setText(convertToRupiah(0));
                        tvtotal.setText(convertToRupiah(0));
                    }
                }else{
                    tvPotongan.setText(convertToRupiah(0));
                    tvtotal.setText(convertToRupiah(0));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setPotonganAndTotal(int jumlahPenarikan){
        int potongan = (jumlahPenarikan*10)/100;
        int total = jumlahPenarikan - potongan;

        tvPotongan.setText(convertToRupiah(potongan));
        tvtotal.setText(convertToRupiah(total));
    }

    private void getSaldoNasabah(){
        DatabaseReference saldoReference = databaseReference.child("saldonasabah").child(sessionManagement.getUserSession()).child("saldo");
        saldoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saldoNasabah = snapshot.getValue(int.class);
                if(mode == 0){
                    tvSaldo.setText(convertToRupiah(snapshot.getValue(int.class)));
                }else{
                    tvSaldo.setText(convertToRupiah(snapshot.getValue(int.class)) + " + "+ convertToRupiah(transaksiSaldo.getJumlah_transaksi()));
                    saldoNasabah = saldoNasabah + transaksiSaldo.getJumlah_transaksi();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(error.getMessage(), "onCancelled: ");
            }
        });
    }

    private void fetchDataTransaksiSaldo(int jumlahPenarikan)
    {
        databaseReference.child("transaksi_saldo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String kodeTransaksiSaldo = DEFAULT_KODE_TRANSAKSI_SALDO;
                if(task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    if(result.getValue() != null) {
                        for (DataSnapshot dataSnapshot : result.getChildren()) {
                            String dataKodeTransaksi = dataSnapshot.getKey();
                            if (dataKodeTransaksi != null) {
                                kodeTransaksiSaldo = increseNumber(dataKodeTransaksi);
                            }
                        }
                    }
                    createTransaksiSaldo(jumlahPenarikan, kodeTransaksiSaldo, 0);
                }
            }
        });
    }

    private void createTransaksiSaldo(int jumlahPenarikan, String idTransaksiSaldo, int lastTransaction){
        transaksiSaldo.setStatus("PENDING");
        transaksiSaldo.setId_transaksi_saldo(idTransaksiSaldo);
        transaksiSaldo.setTanggal_transaksi(System.currentTimeMillis());
        transaksiSaldo.setJumlah_transaksi(jumlahPenarikan);
        transaksiSaldo.setJenis_transaksi("TARIK");
        transaksiSaldo.setPotongan((jumlahPenarikan/100)*10);
        transaksiSaldo.setId_nasabah(sessionManagement.getUserSession());
        transaksiSaldo.setId_penerima("none");

        DatabaseReference saldoReference = databaseReference.child("saldonasabah").child(sessionManagement.getUserSession());
        DatabaseReference dbReferencesTransaksiSaldo = databaseReference.child("transaksi_saldo").child(idTransaksiSaldo);
        dbReferencesTransaksiSaldo.setValue(transaksiSaldo).addOnSuccessListener(aVoid->
        {
            saldoReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Saldo saldoNasabah = snapshot.getValue(Saldo.class);
                    int currentBalance = saldoNasabah.getSaldo();

                    if(mode == 0){
                        int newBalance = currentBalance - jumlahPenarikan;
                        saldoNasabah.setSaldo(newBalance);
                        saldoReference.setValue(saldoNasabah);
                        showMessage(TambahEditTransaksiSaldoActivity.this, "Berhasil Meminta Penarikan Harap Tunggu Di Approve oleh admin");
                    }else{
                        currentBalance = currentBalance + lastTransaction;
                        int newBalance = currentBalance - jumlahPenarikan;
                        saldoNasabah.setSaldo(newBalance);
                        saldoReference.setValue(saldoNasabah);
                        showMessage(TambahEditTransaksiSaldoActivity.this, "Berhasil Meminta Penarikan Harap Tunggu Di Approve oleh admin");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
