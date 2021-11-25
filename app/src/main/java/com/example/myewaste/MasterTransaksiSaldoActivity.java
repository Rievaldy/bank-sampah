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
import com.example.myewaste.adapter.TransaksiSaldoAdapter;
import com.example.myewaste.model.TransaksiBarang;
import com.example.myewaste.model.TransaksiSaldo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.myewaste.Util.getRegisterCode;

public class MasterTransaksiSaldoActivity extends AppCompatActivity {

    DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("transaksi_saldo");
    RecyclerView recyclerView;
    TransaksiSaldoAdapter transaksiSaldoAdapter;
    SessionManagement sessionManagement;
    MasterTransaksiSaldoActivity.MasterSaldoListener listener;
    FloatingActionButton btnDownloadExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_saldo);

        getSupportActionBar().setTitle("Master Transaksi Saldo");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view_transaksi_saldo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnDownloadExcel = findViewById(R.id.btnDownloadExclLaporanSaldo);
        sessionManagement = new SessionManagement(getApplicationContext());

        if(!getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("sa")){
            btnDownloadExcel.setVisibility(View.GONE);
        }

        listener = new MasterTransaksiSaldoActivity.MasterSaldoListener() {
            @Override
            public void onClickListTransaksiSaldo(TransaksiSaldo transaksiSaldo) {
                Intent intent = new Intent(MasterTransaksiSaldoActivity.this, DetailTransaksiSaldoActivity.class);
                intent.putExtra("EXTRA_TRANSAKSI_SALDO", transaksiSaldo);
                startActivity(intent);
            }
        };

        btnDownloadExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MasterTransaksiSaldoActivity.this, SettingDownloadExcel.class);
                intent.putExtra("mode", 1);
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
            transaksiQuery = databaseReference.orderByChild("id_nasabah").equalTo(sessionManagement.getUserSession());
        }
        transaksiQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TransaksiSaldo> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    TransaksiSaldo value = data.getValue(TransaksiSaldo.class);
                    if(value.getJenis_transaksi().equals("TARIK")){
                        list.add(value);
                        if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("t")){
                            if((!value.getStatus().equals("PENDING") && !value.getId_penerima().toLowerCase().equals(sessionManagement.getUserSession()))){
                                int lastIndex = list.size()-1;
                                list.remove(lastIndex);
                            }
                        }
                    }
                }
                transaksiSaldoAdapter = new TransaksiSaldoAdapter(MasterTransaksiSaldoActivity.this,list, listener);
                recyclerView.setAdapter(transaksiSaldoAdapter);
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

    public interface MasterSaldoListener{
        void onClickListTransaksiSaldo(TransaksiSaldo transaksiSaldo);
    }
}
