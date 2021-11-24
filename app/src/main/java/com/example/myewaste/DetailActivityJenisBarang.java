package com.example.myewaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myewaste.model.MasterBarang;
import com.example.myewaste.model.MasterJenisBarang;
import com.example.myewaste.model.SatuanModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.myewaste.Util.loadImage;

public class DetailActivityJenisBarang extends AppCompatActivity {
    private ImageView fotoBarang;
    private TextView tvNamaBarang,tvNamaJenisBarang, tvSatuan, tvHarga;
    private Button btnEdit, btnHapus;
    private MasterJenisBarang masterJenisBarang;
    private DatabaseReference referenceBarang;
    private DatabaseReference referenceSatuan;
    private DatabaseReference jenisBarangReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jenis_barang);

        masterJenisBarang = getIntent().getParcelableExtra("EXTRA_JENIS_BARANG");
        fotoBarang = findViewById(R.id.iv_foto_barang_djb);
        tvNamaBarang = findViewById(R.id.tv_nama_barang_djb);
        tvNamaJenisBarang = findViewById(R.id.tv_nama_jenis_barang_djb);
        tvSatuan = findViewById(R.id.tv_nama_satuan_djb);
        tvHarga = findViewById(R.id.tv_harga_djb);
        btnEdit = findViewById(R.id.btn_edit_djb);
        btnHapus = findViewById(R.id.btn_hapus_djb);

        referenceBarang = FirebaseDatabase.getInstance().getReference("barang");
        referenceSatuan = FirebaseDatabase.getInstance().getReference("satuan_barang");
        jenisBarangReference  = FirebaseDatabase.getInstance().getReference("jenis_barang");
        initializeData();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editData(masterJenisBarang);
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapusData(masterJenisBarang);
            }
        });

    }

    private void initializeData(){
        tvNamaJenisBarang.setText(masterJenisBarang.getNama_master_jenis_barang());
        tvHarga.setText(String.valueOf(masterJenisBarang.getHarga()));
        loadDataBarang();
        loadDataSatuan();
    }

    private void loadDataBarang(){
        referenceBarang.child(masterJenisBarang.getNo_master_barang()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MasterBarang masterBarang = snapshot.getValue(MasterBarang.class);
                tvNamaBarang.setText(masterBarang.getNama_master_barang());
                loadImage(masterBarang.getFoto_master_barang(), fotoBarang, DetailActivityJenisBarang.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadDataSatuan(){
        referenceSatuan.child(masterJenisBarang.getNo_satuan_barang()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SatuanModel satuanModel = snapshot.getValue(SatuanModel.class);
                tvSatuan.setText(satuanModel.getNamaSatuan());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hapusData(MasterJenisBarang jenisBarang)
    {
        jenisBarangReference.child(jenisBarang.getNo_master_jenis_barang()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getApplicationContext(), "Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void editData(MasterJenisBarang jenisBarang)
    {
        Intent intent = new Intent(DetailActivityJenisBarang.this, TambahEditJenisBarang.class);
        intent.putExtra("EXTRAS_JENIS_BARANG", jenisBarang);
        startActivity(intent);
        finish();
    }
}
