package com.example.myewaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
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

import com.example.myewaste.adapter.SatuanAdapter;
import com.example.myewaste.model.SatuanModel;
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


public class MasterSatuanActivity extends AppCompatActivity {

    FloatingActionButton tblData;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("satuan_barang");
    List<SatuanModel> list = new ArrayList<>();
    SatuanAdapter satuanAdapter;
    public static final String NO_SATUAN = "S-0001";
    private static final String TAG = "MasterSatuanActivity";
    SatuanModel satuan = new SatuanModel();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_satuan);

        getSupportActionBar().setTitle("Master Satuan Barang");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tblData = findViewById(R.id.tbl_data);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tblData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogTambahData();
            }
        });

        bacaData();
    }

    private void bacaData()
    {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SatuanModel value = snapshot.getValue(SatuanModel.class);
                    list.add(value);
                }
                satuanAdapter = new SatuanAdapter(MasterSatuanActivity.this,list);
                recyclerView.setAdapter(satuanAdapter);

                setClick();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });
    }

    private void setClick()
    {
        satuanAdapter.setOnCallBack(new SatuanAdapter.OnCallBack() {
            @Override
            public void onTblHapus(SatuanModel satuanModel) {
                //todo hapus to risky to use it
//                hapusData(satuanModel);
            }

            @Override
            public void onTblEdit(SatuanModel satuanModel) {
                showDialogEditData(satuanModel);

            }
        });
    }

    private void showDialogEditData(SatuanModel satuanModel)
    {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_tambah_data_satuan);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageButton tblKeluar = dialog.findViewById(R.id.tbl_keluar);
        tblKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //EditText txtNomor = dialog.findViewById(R.id.txt_nomor);
        EditText txtNama = dialog.findViewById(R.id.txt_nama);
        Button tblEdit = dialog.findViewById(R.id.tbl_tambah);
        TextView tvHeader = dialog.findViewById(R.id.text_header);

        txtNama.setText(satuanModel.getNamaSatuan());
        tblEdit.setText("Update");
        tvHeader.setText("Edit Data");

        tblEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(txtNama.getText()))
                {
                    tblEdit.setError("Data Kosong");
                }
                else {
                    editData(satuanModel, txtNama.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void editData(SatuanModel satuanModel, String baru)
    {
        satuan.setNamaSatuan(baru);
        generateIdSatuan(new onGenerateKodeListener() {
            @Override
            public void onSuccess(String noSatuan, boolean hasSimiliarName) {
                if(!hasSimiliarName)
                {
                    myRef.child(satuanModel.getNoSatuan()).child("namaSatuan").setValue(baru).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Berhasil",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Update Gagal Data Sudah Ada",Toast.LENGTH_SHORT).show();
                }
            }
        },baru);

//        myRef.child(satuanModel.getNoSatuan()).child("namaSatuan").setValue(baru).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getApplicationContext(),satuanModel.getNamaSatuan()+" Berhasil Diupdate",Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void hapusData(SatuanModel satuanModel)
    {
        myRef.child(satuanModel.getNoSatuan()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getApplicationContext(),satuanModel.getNamaSatuan()+" Berhasil Dihapus",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogTambahData()
    {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_tambah_data_satuan);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageButton tblKeluar = dialog.findViewById(R.id.tbl_keluar);
        tblKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //EditText txtNomor = dialog.findViewById(R.id.txt_nomor);
        EditText txtNama = dialog.findViewById(R.id.txt_nama);
        Button tblTambah = dialog.findViewById(R.id.tbl_tambah);

        tblTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(txtNama.getText()))
                {
                    tblTambah.setError("Data Kosong");
                }
                else {
                    simpanData(txtNama.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void simpanData(String nama) {
        //String no_satuan = myRef.push().getKey();
        satuan.setNamaSatuan(nama);
        generateIdSatuan(new onGenerateKodeListener() {
            @Override
            public void onSuccess(String noSatuan, boolean hasSimiliarName) {
                if(!hasSimiliarName)
                {
                    satuan.setNoSatuan(noSatuan);
                    myRef.child(noSatuan).setValue(satuan).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Berhasil",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Data Sudah Ada",Toast.LENGTH_SHORT).show();
                }
            }
        },nama);

    }

    private void  generateIdSatuan( final  onGenerateKodeListener listener,String nama){
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String noSatuan = NO_SATUAN;
                boolean hasSimiliarName = false;
                if(task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    Log.d(TAG, "onComplete: " + result);
                    if(result.getValue() != null) {
                        Log.d(TAG, "onComplete: running");
                        for (DataSnapshot dataSnapshot : result.getChildren()) {
                            String dataNomorSatuan = dataSnapshot.getKey();
                            if (dataNomorSatuan != null) {
                                noSatuan = increseNumber(dataNomorSatuan);
                                Log.d(TAG, "onComplete: "+ noSatuan);

                            }
                            String namaBarang = dataSnapshot.getValue(SatuanModel.class).getNamaSatuan();
                            if(namaBarang.equals(nama)){
                                hasSimiliarName = true;
                            }
                        }
                    }
                }
                listener.onSuccess(noSatuan, hasSimiliarName);
                Log.d(TAG, "onComplete: " + noSatuan);
                satuan.setNoSatuan(noSatuan);
            }
        });
    }

    public interface onGenerateKodeListener{
        void onSuccess(String noSatuan, boolean hasSimiliarName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
