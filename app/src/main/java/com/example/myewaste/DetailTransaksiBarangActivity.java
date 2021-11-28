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

import com.example.myewaste.model.MasterBarang;
import com.example.myewaste.model.MasterJenisBarang;
import com.example.myewaste.model.SatuanModel;
import com.example.myewaste.model.TransaksiBarang;
import com.example.myewaste.model.UserData;
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

public class DetailTransaksiBarangActivity extends AppCompatActivity {

    private LinearLayout layout;
    private TextView tvIdTransaksi, tvNamaBarang, tvNamaJenisBarang, tvNamaUser, tvNamaTeller, tvHargaBarang, tvJumlah, tvSatuan, tvTotalHarga, tvTanggalTransaksi;
    private Button btnEditTransaksi;

    private TransaksiBarang transaksiBarang;
    private MasterBarang masterBarang;
    private SatuanModel satuanModel;
    private MasterJenisBarang jenisBarang;
    private UserData dataTeller;
    private UserData dataUser;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private SessionManagement sessionManagement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi_barang);

        getSupportActionBar().setTitle("Detail Transaksi Penimbangan");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layout = findViewById(R.id.layout_detail_transaksi_barang);
        tvIdTransaksi = findViewById(R.id.tv_id_transaksi_dtb);
        tvNamaBarang = findViewById(R.id.tv_nama_barang_dtb);
        tvNamaJenisBarang = findViewById(R.id.tv_nama_jenis_barang_dtb);
        tvNamaUser = findViewById(R.id.tv_nama_user_dtb);
        tvNamaTeller = findViewById(R.id.tv_nama_teller_dtb);
        tvHargaBarang = findViewById(R.id.tv_harga_dtb);
        tvJumlah = findViewById(R.id.tv_jumlah_dtb);
        tvSatuan = findViewById(R.id.tv_satuan_dtb);
        tvTotalHarga = findViewById(R.id.tv_total_harga_dtb);
        tvTanggalTransaksi = findViewById(R.id.tv_tanggal_transaksi_dtb);
        btnEditTransaksi = findViewById(R.id.btn_edit_dtb);
        transaksiBarang = getIntent().getParcelableExtra("EXTRA_TRANSAKSI_BARANG");
        sessionManagement = new SessionManagement(getApplicationContext());
        prepareLayout();

        if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("n")){
            btnEditTransaksi.setVisibility(View.GONE);
        }

        btnEditTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailTransaksiBarangActivity.this, TambahEditTransaksi.class);
                intent.putExtra("EXTRA_TRANSAKSI_BARANG", transaksiBarang);
                startActivity(intent);
                finish();
            }
        });
    }

    private void prepareLayout(){
        tvIdTransaksi.setText(transaksiBarang.getNo_transaksi_barang());
        tvJumlah.setText(String.valueOf(transaksiBarang.getJumlah()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = new Date(transaksiBarang.getTanggal_transaksi());
        String tanggalTransaksi = sdf.format(date);
        tvTanggalTransaksi.setText(tanggalTransaksi);
        tvTotalHarga.setText(convertToRupiah(transaksiBarang.getTotal_harga()));
        loadUserData(0, transaksiBarang.getNo_nasabah());//mode 0 for user
        loadUserData(1, transaksiBarang.getNo_teller());//mode 1 for teller
        loadJenisBarangById(transaksiBarang.getNomor_jenis_barang());
    }

    private void loadUserData(int mode, String idUser){
        //todo mode 0 for user , 1 for teller
        Query userQuery = databaseReference.child("userdata").orderByChild("noregis").equalTo(idUser);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    tvNamaUser.setText(dataUser.getNama());
                }else{
                    tvNamaTeller.setText(dataTeller.getNama());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadJenisBarangById(String idJenisBarang){
        Query jenisBarangQuery = databaseReference.child("jenis_barang").orderByChild("no_master_jenis_barang").equalTo(idJenisBarang);
        jenisBarangQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    jenisBarang = data.getValue(MasterJenisBarang.class);
                }
                tvNamaJenisBarang.setText(jenisBarang.getNama_master_jenis_barang());
                tvHargaBarang.setText(convertToRupiah(jenisBarang.getHarga()));
                loadSatuanById(jenisBarang.getNo_satuan_barang());
                loadBarangById(jenisBarang.getNo_master_barang());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadSatuanById(String idSatuan){
        Query satuanQuery = databaseReference.child("satuan_barang").orderByChild("noSatuan").equalTo(idSatuan);
        satuanQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    satuanModel = data.getValue(SatuanModel.class);
                }
                tvSatuan.setText(satuanModel.getNamaSatuan());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadBarangById(String idBarang){
        Query barangQuery = databaseReference.child("barang").orderByChild("no_master_barang").equalTo(idBarang);
        barangQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    masterBarang = data.getValue(MasterBarang.class);
                }
                tvNamaBarang.setText(masterBarang.getNama_master_barang());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
