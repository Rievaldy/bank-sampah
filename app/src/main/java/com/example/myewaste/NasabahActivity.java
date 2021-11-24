package com.example.myewaste;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myewaste.model.Saldo;
import com.example.myewaste.model.TransaksiSaldo;
import com.example.myewaste.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.myewaste.Util.increseNumber;
import static com.example.myewaste.Util.showMessage;

public class NasabahActivity extends AppCompatActivity {


    private UserData userData;
    private CardView cv_history_transaksi, cv_laporan_saldo_nasabah;
    private DatabaseReference databaseReference;
    private ImageView ivFotoProfil;
    private Button logout;
    private TextView tvNoregis, tvNamaUser, tvTotalSaldo;
    private Button btnPenarikan;
    private static final String DEFAULT_EXTRAS_NAME = "USER_DATA_EXTRAS";
    private ActivityResultLauncher intentLaunch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nasabah);

        if(getIntent().getParcelableExtra(DEFAULT_EXTRAS_NAME) != null) userData = getIntent().getParcelableExtra(DEFAULT_EXTRAS_NAME);

        ivFotoProfil = findViewById(R.id.iv_fotonasabah);
        tvNoregis = findViewById(R.id.et_noregis);
        tvNamaUser = findViewById(R.id.et_nama);
        tvTotalSaldo = findViewById(R.id.totalsaldo);
        btnPenarikan = findViewById(R.id.btnPenarikan);
        cv_history_transaksi = findViewById(R.id.cv_history_transaksi);
        cv_laporan_saldo_nasabah = findViewById(R.id.cv_laporan_saldo_nasabah);
        logout = findViewById(R.id.btnlogoutn);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadData();

        intentLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    userData = result.getData().getParcelableExtra(ProfilUser.DEFAULT_EXTRAS_NAME);
                    loadData();
                });

        ivFotoProfil.setOnClickListener(view -> {
            Intent intent = new Intent(NasabahActivity.this, ProfilUser.class);
            intent.putExtra(DEFAULT_EXTRAS_NAME, userData);
            intentLaunch.launch(intent);
        });

        cv_history_transaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NasabahActivity.this, MasterTransaksiBarangActivity.class));
            }
        });

        cv_laporan_saldo_nasabah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NasabahActivity.this, MasterTransaksiSaldoActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keluar = new Intent(NasabahActivity.this, Login.class);
                keluar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(keluar);
                finish();
            }
        });

        btnPenarikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NasabahActivity.this, TambahEditTransaksiSaldoActivity.class));
            }
        });
    }

    private void loadData() {
        Util.loadImage(userData.getFotoProfil(), ivFotoProfil, NasabahActivity.this);
        tvNoregis.setText(userData.getNoregis());
        tvNamaUser.setText(userData.getNama());
        getSaldoNasabah();
    }

    private void getSaldoNasabah(){
        DatabaseReference saldoReference = databaseReference.child("saldonasabah").child(userData.getNoregis()).child("saldo");
        saldoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvTotalSaldo.setText(String.valueOf(snapshot.getValue(int.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(error.getMessage(), "onCancelled: ");
            }
        });
    }





}