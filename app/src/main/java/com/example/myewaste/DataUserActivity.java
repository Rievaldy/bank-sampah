package com.example.myewaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myewaste.adapter.DataUserAdapter;
import com.example.myewaste.model.UserData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class DataUserActivity extends AppCompatActivity {
    private DatabaseReference reference;
    ArrayList<UserData> listUserData;
    DataUserAdapter adapter;
    private Util.MODE mode;
    private static final String TAG = "DataNasabahActivity";
    private ImageView filter;

    FloatingActionButton btn_addnasabah;

    private RecyclerView mRecyler;
    private LinearLayoutManager mManager;
    /*private RecyclerView dataNasabahRecylcer;
    private LinearLayoutManager dataNasabahManager;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_user);

        if(getIntent().hasExtra("mode")){
            mode = (Util.MODE) getIntent().getSerializableExtra("mode");
            switch (mode){
                case MODE_SUPER_ADMIN :
                    setTitle("Data Super Admin");
                    break;
                case MODE_NASABAH :
                    setTitle("Data Nasabah");
                    break;
                case MODE_TELLER :
                    setTitle("Data Teller");
                    break;
                default: break;
            }
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRecyler = findViewById(R.id.list_data_nasabah);
        mRecyler.setHasFixedSize(true);

        filter = findViewById(R.id.filterUser);
        filter.setOnClickListener(view -> {

        });

        btn_addnasabah = findViewById(R.id.btn_addnasabah);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyler.setLayoutManager(mManager);

        reference = FirebaseDatabase.getInstance().getReference().child("userdata");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
            listUserData = new ArrayList<>();

            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                UserData userData = dataSnapshot1.getValue(UserData.class);
                String extractRegisterCode = Util.getRegisterCode(userData.getNoregis()).toLowerCase();
                boolean isValid = false;

                if(mode.equals(Util.MODE.MODE_NASABAH) && extractRegisterCode.equals("n")
                        || mode.equals(Util.MODE.MODE_TELLER) && extractRegisterCode.equals("t")
                        || mode.equals(Util.MODE.MODE_SUPER_ADMIN) && extractRegisterCode.equals("sa")){
                    isValid = true;
                }

                if(isValid) listUserData.add(userData);
            }
                Collections.reverse(listUserData);
                adapter = new DataUserAdapter(DataUserActivity.this, listUserData);
                mRecyler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan",Toast.LENGTH_LONG).show();
            }
        });

        //event klik untuk tambah mahasiswa
        btn_addnasabah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addnasabah = new Intent(getApplicationContext(), TambahDataUser.class);
                addnasabah.putExtra("mode", mode);
                startActivity(addnasabah);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}