package com.example.myewaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myewaste.adapter.TransaksiBarangAdapter;
import com.example.myewaste.model.TransaksiBarang;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.myewaste.Util.getRegisterCode;

public class MasterTransaksiBarangActivity extends AppCompatActivity {
    DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("transaksi_barang");
    RecyclerView recyclerView;
    TransaksiBarangAdapter transaksiBarangAdapter;
    SessionManagement sessionManagement;
    MasterTransaksiListener listener;
    FloatingActionButton btnDownloadLaporanBarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_barang);

        getSupportActionBar().setTitle("Data Transaksi Penimbangan");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view_transaksi);
        btnDownloadLaporanBarang = findViewById(R.id.btnDownloadExclLaporanBarang);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionManagement = new SessionManagement(getApplicationContext());
        listener = new MasterTransaksiListener() {
            @Override
            public void onClickListTransaksi(TransaksiBarang transaksiBarang) {
                Intent intent = new Intent(MasterTransaksiBarangActivity.this, DetailTransaksiBarangActivity.class);
                intent.putExtra("EXTRA_TRANSAKSI_BARANG", transaksiBarang);
                startActivity(intent);
            }
        };

        btnDownloadLaporanBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MasterTransaksiBarangActivity.this, SettingDownloadExcel.class);
                intent.putExtra("mode", 0);
                startActivity(intent);
            }
        });

        bacaData();
    }

    private void bacaData()
    {
        Query transaksiQuery = databaseReference.orderByChild("tanggal_transaksi");
        // Read from the database
        if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("n")){
            btnDownloadLaporanBarang.setVisibility(View.GONE);
            transaksiQuery = databaseReference.orderByChild("no_nasabah").equalTo(sessionManagement.getUserSession());
        }else if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("t")){
            btnDownloadLaporanBarang.setVisibility(View.GONE);
            transaksiQuery = databaseReference.orderByChild("no_teller").equalTo(sessionManagement.getUserSession());
        }
        transaksiQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TransaksiBarang> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    TransaksiBarang value = data.getValue(TransaksiBarang.class);
                    list.add(value);
                }
                transaksiBarangAdapter = new TransaksiBarangAdapter(MasterTransaksiBarangActivity.this,list, listener);
                recyclerView.setAdapter(transaksiBarangAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public interface MasterTransaksiListener{
        void onClickListTransaksi(TransaksiBarang transaksiBarang);
    }

}
