package com.example.myewaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myewaste.adapter.JenisBarangAdapter;
import com.example.myewaste.adapter.ParentJenisBarangAdapter;
import com.example.myewaste.model.MasterBarang;
import com.example.myewaste.model.MasterJenisBarang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.myewaste.Util.increseNumber;
import static com.example.myewaste.Util.showMessage;

public class MasterJenisBarangActivity extends AppCompatActivity {
    FloatingActionButton btnAdd;
    DatabaseReference jenisBarangReference  = FirebaseDatabase.getInstance().getReference("jenis_barang");
    DatabaseReference barangReference  = FirebaseDatabase.getInstance().getReference("barang");
    RecyclerView recyclerView;
    JenisBarangAdapter jenisBarangAdapter;
    ParentJenisBarangAdapter parentJenisBarangAdapter;
    JenisBarangListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_jenis_barang);

        getSupportActionBar().setTitle("Master Jenis Barang");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnAdd = findViewById(R.id.tbl_data);
        recyclerView = findViewById(R.id.recycler_view_jenis_barang);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        prepareAdapter();
        listener = new JenisBarangListener() {
            @Override
            public void requestAdapter(RecyclerView recyclerView, MasterBarang masterBarang) {
                settingRecycleViewJenisBarang(recyclerView, masterBarang);
            }

            @Override
            public void onClickDelete(MasterJenisBarang masterJenisBarang) {
                //todo delete to risky to use it
                //hapusData(masterJenisBarang);
            }

            @Override
            public void onClickUpdate(MasterJenisBarang masterJenisBarang) {
                editData(masterJenisBarang);
            }

            @Override
            public void onClickOpenDetailJenisBarang(MasterJenisBarang masterJenisBarang) {
                goToDetailActivity(masterJenisBarang);
            }
        };

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MasterJenisBarangActivity.this, TambahEditJenisBarang.class);
                startActivity(intent);
            }
        });
    }

    private void goToDetailActivity(MasterJenisBarang masterJenisBarang) {
        Intent intent = new Intent(MasterJenisBarangActivity.this, DetailActivityJenisBarang.class);
        intent.putExtra("EXTRA_JENIS_BARANG", masterJenisBarang);
        startActivity(intent);
    }

    private void settingRecycleViewJenisBarang(RecyclerView recyclerView, MasterBarang masterBarang) {
        Log.d("TAG", "settingRecycleViewJenisBarang: running");
        jenisBarangReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<MasterJenisBarang> list = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MasterJenisBarang value = snapshot.getValue(MasterJenisBarang.class);
                    Log.d("TAG", "settingRecycleViewJenisBarang: " + value.getNama_master_jenis_barang());
                    if(value.getNo_master_barang().toLowerCase().equals(masterBarang.getNo_master_barang().toLowerCase())){
                        Log.d("TAG", "onDataChange: " + value.getNama_master_jenis_barang());
                        list.add(value);
                    }
                }
                Log.d("TAG", "settingRecycleViewJenisBarang: " + list.size());
                if(list.size() > 0){
                    jenisBarangAdapter = new JenisBarangAdapter(MasterJenisBarangActivity.this,list, listener);
                    recyclerView.setAdapter(jenisBarangAdapter);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    showMessage(MasterJenisBarangActivity.this, "Data Tidak Tersedia Untuk " + masterBarang.getNama_master_barang());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });
    }


    private void prepareAdapter()
    {
        // Read from the database
        barangReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<MasterBarang> list = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MasterBarang value = snapshot.getValue(MasterBarang.class);
                    list.add(value);
                }
                parentJenisBarangAdapter = new ParentJenisBarangAdapter(MasterJenisBarangActivity.this,list, listener);
                recyclerView.setAdapter(parentJenisBarangAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });
    }

    private void hapusData(MasterJenisBarang jenisBarang)
    {
        jenisBarangReference.child(jenisBarang.getNo_master_jenis_barang()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getApplicationContext(), "Berhasil Dihapus", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editData(MasterJenisBarang jenisBarang)
    {
        Intent intent = new Intent(MasterJenisBarangActivity.this, TambahEditJenisBarang.class);
        intent.putExtra("EXTRAS_JENIS_BARANG", jenisBarang);
        startActivity(intent);
    }

    public interface JenisBarangListener{
        void requestAdapter(RecyclerView recyclerView, MasterBarang masterBarang);
        void onClickDelete(MasterJenisBarang masterJenisBarang);
        void onClickUpdate(MasterJenisBarang masterJenisBarang);
        void onClickOpenDetailJenisBarang(MasterJenisBarang masterJenisBarang);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}