package com.example.myewaste;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myewaste.model.UserData;
import com.google.firebase.auth.FirebaseAuth;

public class TellerActivity extends AppCompatActivity implements View.OnClickListener {
    CardView input_teller;
    CardView history_transaksi;
    CardView laporan_saldo;
    Button logout;
    private FirebaseAuth mAuth;

    private UserData userData;
    private ImageView ivFotoAdmin;
    private TextView tvNoregis, tvNamaUser;
    private static final String DEFAULT_EXTRAS_NAME = "USER_DATA_EXTRAS";
    private ActivityResultLauncher intentLaunch;
    private SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teller);

        getSupportActionBar().setTitle("Teller");


        if(getIntent().getParcelableExtra(DEFAULT_EXTRAS_NAME) != null) {
            userData = getIntent().getParcelableExtra(Login.DEFAULT_EXTRAS_NAME);
            Log.d("checking", "onCreate: " + userData.getFotoProfil());
        }

        input_teller = findViewById(R.id.cv_input_transaksi_nasabah);
        history_transaksi = findViewById(R.id.cv_history_transaksi);
        laporan_saldo = findViewById(R.id.cv_laporan_saldo_teller);

        logout = findViewById(R.id.btnlogoutteller);
        mAuth = FirebaseAuth.getInstance();
        sessionManagement = new SessionManagement(getApplicationContext());


        input_teller.setOnClickListener(this);
        history_transaksi.setOnClickListener(this);
        laporan_saldo.setOnClickListener(this);
        logout.setOnClickListener(this);

        ivFotoAdmin = findViewById(R.id.iv_fototeller);
        tvNoregis = findViewById(R.id.et_noregis);
        tvNamaUser = findViewById(R.id.et_nama);

        loadData();

        intentLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    userData = result.getData().getParcelableExtra(ProfilUser.DEFAULT_EXTRAS_NAME);
                    loadData();
                });

        ivFotoAdmin.setOnClickListener(view -> {
            Intent intent = new Intent(TellerActivity.this, ProfilUser.class);
            intent.putExtra(DEFAULT_EXTRAS_NAME, userData);
            intentLaunch.launch(intent);
        });

    }

    private void loadData() {
        Util.loadImage(userData.getFotoProfil(), ivFotoAdmin, TellerActivity.this);
        tvNoregis.setText(userData.getNoregis());
        tvNamaUser.setText(userData.getNama());
    }

    public void InputTeller(View view){
        startActivity(new Intent(this, MasterTransaksiBarangActivity.class));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_input_transaksi_nasabah:
                Intent cv_input_transaksi_nasabah = new Intent(this, TambahEditTransaksi.class);
                startActivity(cv_input_transaksi_nasabah);
                break;
            case R.id.cv_history_transaksi:
                Intent cv_history_transaksi = new Intent(this, MasterTransaksiBarangActivity.class);
                startActivity(cv_history_transaksi);
                break;
            case R.id.cv_laporan_saldo_teller :
                Intent cv_laporan_saldo_teller = new Intent(this, MasterTransaksiSaldoActivity.class);
                startActivity(cv_laporan_saldo_teller);
                break;
            case R.id.btnlogoutteller:
                sessionManagement.removeUserSession();
                Intent keluar = new Intent(this, Login.class);
                keluar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(keluar);
                break;

        }
    }
}