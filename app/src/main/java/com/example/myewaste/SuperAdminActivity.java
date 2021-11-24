package com.example.myewaste;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myewaste.model.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class SuperAdminActivity extends AppCompatActivity implements View.OnClickListener {
    CardView master_barang,
            master_jenis_barang,
            master_satuan,
            laporan_transaksi,
            laporan_saldo,
            data_nasabah,
            data_teller,
            data_super_admin;
    private TextView tvNamaUser, tvNoregis;
    Button logout;
    ImageView iv_profil;
    private FirebaseAuth mAuth;
    private static final String DEFAULT_EXTRAS_NAME = "USER_DATA_EXTRAS";
    private UserData userData;
    private static final String TAG = "SuperAdminActivity";
    ActivityResultLauncher<Intent> intentLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);

        //inisialisasi id dari card view
        iv_profil = findViewById(R.id.iv_profil);
        tvNamaUser = findViewById(R.id.et_nama);
        tvNoregis = findViewById(R.id.et_noregis);
        if(getIntent().getParcelableExtra(DEFAULT_EXTRAS_NAME) != null){
            userData = (UserData) getIntent().getParcelableExtra(DEFAULT_EXTRAS_NAME);
            refreshData();
        }
        //inisialisasi id dari card view
        master_barang = findViewById(R.id.cv_master_barang);
        master_jenis_barang = findViewById(R.id.cv_master_jenis_barang);
        master_satuan = findViewById(R.id.cv_master_satuan);
        laporan_transaksi = findViewById(R.id.cv_laporan_transaksi);
        laporan_saldo = findViewById(R.id.cv_laporan_saldo);
        data_nasabah = findViewById(R.id.cv_data_nasabah);
        data_teller = findViewById(R.id.cv_data_teller);
        data_super_admin = findViewById(R.id.cv_data_super_admin);
        logout = findViewById(R.id.btnlogoutsa);
        mAuth = FirebaseAuth.getInstance();
        intentLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    userData = result.getData().getParcelableExtra(ProfilUser.DEFAULT_EXTRAS_NAME);
                    refreshData();
        });

        iv_profil.setOnClickListener(this);
        master_barang.setOnClickListener(this);
        master_jenis_barang.setOnClickListener(this);
        master_satuan.setOnClickListener(this);
        laporan_transaksi.setOnClickListener(this);
        laporan_saldo.setOnClickListener(this);
        data_nasabah.setOnClickListener(this);
        data_teller.setOnClickListener(this);
        data_super_admin.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    private void refreshData() {
        tvNamaUser.setText(userData.getNama());
        tvNoregis.setText(userData.getNoregis());
        Util.loadImage(userData.getFotoProfil(), iv_profil, SuperAdminActivity.this);
    }


    //Fungsi pindah halaman
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_profil:
                Intent iv_profil = new Intent(this, ProfilUser.class);
                iv_profil.putExtra(DEFAULT_EXTRAS_NAME, userData);
                intentLaunch.launch(iv_profil);
                break;
            case R.id.cv_master_barang:
                Intent cv_master_barang = new Intent(this, MasterBarangActivity.class);
                startActivity(cv_master_barang);
                break;
            case R.id.cv_master_jenis_barang:
                Intent cv_master_jenis_barang = new Intent(this, MasterJenisBarangActivity.class);
                startActivity(cv_master_jenis_barang);
                break;
            case R.id.cv_master_satuan:
                Intent cv_master_satuan = new Intent(this, MasterSatuanActivity.class);
                startActivity(cv_master_satuan);
                break;
            case R.id.cv_laporan_transaksi:
                Intent cv_laporan_transaksi = new Intent(this, MasterTransaksiBarangActivity.class);
                startActivity(cv_laporan_transaksi);
                break;
            case R.id.cv_laporan_saldo:
                Intent cv_laporan_saldo = new Intent(this, MasterTransaksiSaldoActivity.class);
                startActivity(cv_laporan_saldo);
                break;
            case R.id.cv_data_nasabah:
                Intent cv_data_nasabah = new Intent(this, DataUserActivity.class);
                cv_data_nasabah.putExtra("mode", Util.MODE.MODE_NASABAH);
                startActivity(cv_data_nasabah);
                break;
            case R.id.cv_data_teller:
                Intent cv_data_teller = new Intent(this, DataUserActivity.class);
                cv_data_teller.putExtra("mode", Util.MODE.MODE_TELLER);
                startActivity(cv_data_teller);
                break;
            case R.id.cv_data_super_admin:
                Intent cv_data_super_admin = new Intent(this, DataUserActivity.class);
                cv_data_super_admin.putExtra("mode", Util.MODE.MODE_SUPER_ADMIN);
                startActivity(cv_data_super_admin);
                break;
            case R.id.btnlogoutsa:
                    mAuth.signOut();
                Intent keluar = new Intent(this, Login.class);
                keluar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(keluar);
                break;
        }
    }
}