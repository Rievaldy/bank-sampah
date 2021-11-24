package com.example.myewaste;

import static com.example.myewaste.Util.increseNumber;
import static com.example.myewaste.Util.showMessage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myewaste.adapter.JenisBarangAdapter;
import com.example.myewaste.model.MasterBarang;
import com.example.myewaste.model.MasterJenisBarang;
import com.example.myewaste.model.SatuanModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TambahEditJenisBarang extends AppCompatActivity {
    private Spinner spinnerbarang;
    private Spinner spinnersatuan;
    private EditText t1;
    private EditText t2;
    private Button button;
    private int mode; // 0 tambah  : 1 edit

    private MasterJenisBarang masterJenisBarang;
    private static final String TAG = "MasterJenisBarang";

    private DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference();
    private ArrayList<MasterBarang> dataBarang = new ArrayList<>();
    private ArrayList<SatuanModel> dataSatuan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data_jenis_barang);
        masterJenisBarang = new MasterJenisBarang();

        spinnerbarang = findViewById(R.id.spin_barang);
        spinnersatuan = findViewById(R.id.spin_satuan);
        button = findViewById(R.id.btnadd);
        t1 = findViewById(R.id.txt_nama);
        t2 = findViewById(R.id.txt_harga);

        if(getIntent().hasExtra("EXTRAS_JENIS_BARANG")){
            masterJenisBarang = getIntent().getParcelableExtra("EXTRAS_JENIS_BARANG");
            mode = 1;
            t1.setText(masterJenisBarang.getNama_master_jenis_barang());
            t2.setText(String.valueOf(masterJenisBarang.getHarga()));
            button.setText("Edit");
        }
        showDataSpinnerBarang();
        showDataSpinnerSatuan();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Tunjuk Data Spinner
        spinnerbarang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posisiBarang, long l) {
                if(posisiBarang > 0){
                    final String valueBarang = dataBarang.get(posisiBarang).getNo_master_barang();
                    masterJenisBarang.setNo_master_barang(valueBarang);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnersatuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posisiSatuan, long l) {
                if(posisiSatuan > 0){
                    final String valueSatuan = dataSatuan.get(posisiSatuan).getNoSatuan();
                    masterJenisBarang.setNo_satuan_barang(valueSatuan);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //tambahdata
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(masterJenisBarang.getNo_master_barang() == null){
                    showMessage(TambahEditJenisBarang.this, "Harap pilih kategori barang");
                }else if( masterJenisBarang.getNo_satuan_barang() ==  null){
                    showMessage(TambahEditJenisBarang.this, "Harap pilih satuan");
                }else if(t1.getText().toString().isEmpty()){
                    showMessage(TambahEditJenisBarang.this, "Nama Tidak Boleh Kosong");
                }else if(t2.getText().toString().isEmpty()){
                    showMessage(TambahEditJenisBarang.this, "Harga Tidak Boleh Kosong");
                }else{
                    masterJenisBarang.setNama_master_jenis_barang(t1.getText().toString());
                    masterJenisBarang.setHarga(Integer.valueOf(t2.getText().toString()));
                    checkIsSimiliar(masterJenisBarang);
                }
            }
        });

    }

    private void checkIsSimiliar(MasterJenisBarang masterJenisBarang)
    {
        databaseReference.child("jenis_barang").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String kodeJenisBarang = Util.DEFAULT_KODE_BARANG;
                if(task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    boolean hasSimiliarName = false;
                    if(result.getValue() != null) {
                        for (DataSnapshot dataSnapshot : result.getChildren()) {
                            String namaJenisBarang = dataSnapshot.getValue(MasterJenisBarang.class).getNama_master_jenis_barang();
                            String datakodeJenisBarang = dataSnapshot.getKey();
                            if (datakodeJenisBarang != null) {
                                kodeJenisBarang = increseNumber(datakodeJenisBarang);
                            }
                            if(namaJenisBarang.toLowerCase().equals(t1.getText().toString().toLowerCase())){
                                if(datakodeJenisBarang.toLowerCase().equals(masterJenisBarang.getNo_master_jenis_barang().toLowerCase()) && mode == 1){
                                   hasSimiliarName = false;
                                }else{
                                    hasSimiliarName = true;
                                    showMessage(TambahEditJenisBarang.this, "Nama Jenis Barang Sudah Tersedia");
                                }
                            }
                        }
                        if(hasSimiliarName){
                            showMessage(TambahEditJenisBarang.this, "Nama Jenis Barang Sudah Tersedia");
                        }else if(mode == 0) {
                            masterJenisBarang.setNo_master_jenis_barang(kodeJenisBarang);
                        }
                        onSubmit(masterJenisBarang);
                    }
                }
            }
        });
    }

    private void onSubmit(MasterJenisBarang masterJenisBarang)
    {
        DatabaseReference dbReferencesJenisBarang = databaseReference.child("jenis_barang");
        dbReferencesJenisBarang.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference dbReferencesJenisBarang = databaseReference.child("jenis_barang").child(masterJenisBarang.getNo_master_jenis_barang());
                dbReferencesJenisBarang.setValue(masterJenisBarang).addOnSuccessListener(aVoid->
                {
                    Toast.makeText(TambahEditJenisBarang.this, "Berhasil Menambahkan Jenis Barang", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("error cancel user", error.getMessage());
            }
        });
    }

    private void showDataSpinnerSatuan()
    {
        dataSatuan.add(0, new SatuanModel(null, "pilih satuan"));
        DatabaseReference refrenceSatuan = databaseReference.child("satuan_barang");
        refrenceSatuan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int indexSelected = 0;
                for(DataSnapshot item: snapshot.getChildren())
                {
                    //buat list master satuan
                    SatuanModel pilihSatuan = item.getValue(SatuanModel.class);
                    dataSatuan.add(pilihSatuan);
                    if(mode == 1 && pilihSatuan.getNoSatuan().toLowerCase().equals(masterJenisBarang.getNo_satuan_barang().toLowerCase())){
                        indexSelected = dataSatuan.size() -1;
                    }
                }
                //inisialisasi kode satuan
                ArrayAdapter<SatuanModel> arrayAdapter = new ArrayAdapter<>(TambahEditJenisBarang.this,R.layout.style_spinner, dataSatuan);
                spinnersatuan.setAdapter(arrayAdapter);
                spinnersatuan.setSelection(indexSelected);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataSpinnerBarang() {
        dataBarang.add(0, new MasterBarang(null, "Pilih Barang", null));
        DatabaseReference refrenceBarang = databaseReference.child("barang");
        refrenceBarang.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int indexSelected = 0;
                for(DataSnapshot item: snapshot.getChildren())
                {
                    MasterBarang barang = item.getValue(MasterBarang.class);
                    dataBarang.add(barang);
                    Log.d(TAG, "onDataChange: " + masterJenisBarang.getNo_master_barang());
                    Log.d(TAG, "onDataChange: " + barang.getNo_master_barang());
                    if(mode == 1 && barang.getNo_master_barang().toLowerCase().equals(masterJenisBarang.getNo_master_barang().toLowerCase())){
                        indexSelected = dataBarang.size() -1;
                    }
                }
                ArrayAdapter<MasterBarang> arrayAdapter = new ArrayAdapter<>(TambahEditJenisBarang.this,R.layout.style_spinner, dataBarang);
                spinnerbarang.setAdapter(arrayAdapter);
                spinnerbarang.setSelection(indexSelected);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
