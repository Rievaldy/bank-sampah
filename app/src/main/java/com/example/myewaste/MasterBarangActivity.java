package com.example.myewaste;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myewaste.adapter.DataBarangAdapter;
import com.example.myewaste.model.MasterBarang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MasterBarangActivity extends AppCompatActivity {
    private static final String TAG = "MasterBarangActivity";

    private RecyclerView rvMasterBarang;
    private DataBarangAdapter barangAdapter;
    private FloatingActionButton fabTambahMasterBarang;
    private ArrayList<MasterBarang> listMasterBarang;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_barang);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference("barang");
        loadRecycleView();
        fabTambahMasterBarang = (FloatingActionButton) findViewById(R.id.fabTambahMasterBarang);
        fabTambahMasterBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MasterBarangActivity.this, TambahEditMasterBarang.class);
                MasterBarangActivity.this.startActivity(intent);
            }
        });
    }

    private void loadRecycleView(){
        rvMasterBarang = (RecyclerView) findViewById(R.id.rvMasterBarang);
        rvMasterBarang.setNestedScrollingEnabled(false);
        listMasterBarang = new ArrayList<>();
        barangAdapter = new DataBarangAdapter(this, listMasterBarang);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MasterBarangActivity.this, 2);
        rvMasterBarang.setHasFixedSize(true);
        rvMasterBarang.setLayoutManager(layoutManager);
        rvMasterBarang.setAdapter(barangAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    MasterBarang masterBarang = data.getValue(MasterBarang.class);
                    listMasterBarang.add(masterBarang);
                }
                barangAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

}