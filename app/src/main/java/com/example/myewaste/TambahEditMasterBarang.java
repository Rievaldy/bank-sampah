package com.example.myewaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myewaste.model.MasterBarang;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import static com.example.myewaste.Util.getRegisterAs;
import static com.example.myewaste.Util.getRegisterCode;
import static com.example.myewaste.Util.increseNumber;

public class TambahEditMasterBarang extends AppCompatActivity {

    private Uri filePath;
    private Button btnPilihFoto;
    private TextInputEditText etNamaBarang;
    private String mode = "TAMBAH_DATA";
    private ImageView ivFotoBarang;
    private LinearLayout btnSimpan;
    private LinearLayout btnBatal;
    private MasterBarang masterBarang;
    private final int PICK_IMAGE_REQUEST = 22;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    public static final String DEFAULT_KODE_BARANG = "B-0001";
    private static final String PACKAGE_MESSAGE = "Data_Master_barang";
    private static final String TAG = "TambahMasterBarang";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_edit_master_barang);
        masterBarang = new MasterBarang();
        btnPilihFoto = (Button) findViewById(R.id.btnPilihFoto);
        btnBatal = (LinearLayout) findViewById(R.id.btnBatal);
        btnSimpan = (LinearLayout) findViewById(R.id.btnSimpan);
        etNamaBarang = (TextInputEditText) findViewById(R.id.etNamaBarang);
        ivFotoBarang = (ImageView) findViewById(R.id.ivFotoBarang);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent.getParcelableExtra(PACKAGE_MESSAGE) != null){
            masterBarang = intent.getParcelableExtra(PACKAGE_MESSAGE);
            loadDataFromPackage();
        }

        btnPilihFoto.setOnClickListener(view -> SelectImage());
        btnBatal.setOnClickListener(view -> onBackPressed());
        btnSimpan.setOnClickListener(view -> {
            fetchDataMasterBarang(new onFetchDataListener() {

                @Override
                public void onSuccessGenerateData(String kodeBarang, boolean hasSimiliarName) {
                    masterBarang.setNo_master_barang(kodeBarang);
                    masterBarang.setNama_master_barang(etNamaBarang.getText().toString());
                    uploadImage((uploaded, uri) -> {
                        if (uploaded && !hasSimiliarName) {
                            masterBarang.setFoto_master_barang(uri);
                            onSubmit(masterBarang);
                        } else {
                            if (!uploaded)
                                Toast.makeText(TambahEditMasterBarang.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                            if (hasSimiliarName)
                                Toast.makeText(TambahEditMasterBarang.this, "Nama Barang Sudah tersedia", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onSuccessCheckSimiliarName(boolean hasSimiliarName) {
                    uploadImage((uploaded, uri) -> {
                        if (uploaded && !hasSimiliarName) {
                            masterBarang.setNama_master_barang(etNamaBarang.getText().toString());
                            masterBarang.setFoto_master_barang(uri);
                            onSubmit(masterBarang);
                        } else {
                            if (!uploaded)
                                Toast.makeText(TambahEditMasterBarang.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                            if (hasSimiliarName)
                                Toast.makeText(TambahEditMasterBarang.this, "Nama Barang Sudah tersedia", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try{
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                ivFotoBarang.setBackground(bitmapDrawable);
            }catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    private void onSubmit(MasterBarang masterBarang){
        DatabaseReference dbReferencesBarang = databaseReference.child("barang");
        dbReferencesBarang.addListenerForSingleValueEvent(new ValueEventListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference dbReferencesBarang = databaseReference.child("barang").child(masterBarang.getNo_master_barang());
                dbReferencesBarang.setValue(masterBarang).addOnSuccessListener(aVoid -> {
                    Log.d("setvalue user", "success "+ masterBarang.getNama_master_barang());
                    Toast.makeText(TambahEditMasterBarang.this, "Berhasil Menambahkan Barang", Toast.LENGTH_SHORT).show();
                    goToMasterBarangActivity();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("error cancel user", error.getMessage());
            }
        });

    }


    private void  fetchDataMasterBarang( final  onFetchDataListener listener){
        databaseReference.child("barang").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        String kodeBarang = DEFAULT_KODE_BARANG;
                        boolean hasSimiliarName = false;
                        if(task.isSuccessful()) {
                            DataSnapshot result = task.getResult();
                            if(result.getValue() != null) {
                                for (DataSnapshot dataSnapshot : result.getChildren()) {
                                    String namaBarang = dataSnapshot.getValue(MasterBarang.class).getNama_master_barang();
                                    if(mode.equals("TAMBAH_DATA")){
                                        String dataKodeBarang = dataSnapshot.getKey();
                                        if (dataKodeBarang != null) {
                                            kodeBarang = increseNumber(dataKodeBarang);
                                        }

                                        if(namaBarang.equals(etNamaBarang.getText().toString())){
                                            hasSimiliarName = true;
                                        }
                                    }else{
                                        if(namaBarang.equals(etNamaBarang.getText().toString()) && !dataSnapshot.getKey().equals(masterBarang.getNo_master_barang())){
                                            hasSimiliarName = true;
                                        }
                                    }
                                }
                            }
                        }
                        if(mode.equals("TAMBAH_DATA")) {
                            listener.onSuccessGenerateData(kodeBarang, hasSimiliarName);
                        }else{
                            listener.onSuccessCheckSimiliarName(hasSimiliarName);
                        }
                    }
                });
    }

    private void uploadImage(final onUploadFileListener listener) {
        if (filePath != null) {
            //menambahkan folder di storage firebase images/
            StorageReference referencesBarangImage = storageReference.child("images/barang/" + masterBarang.getNo_master_barang().toLowerCase());
            referencesBarangImage.putFile(filePath)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return referencesBarangImage.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        listener.onSucces(true, downloadUri.toString());
                        Log.d(TAG, "onComplete: " + downloadUri.toString());
                    }else {
                        Log.d(TAG, "onComplete: " + task.getException());
                        Toast.makeText(TambahEditMasterBarang.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            if(mode.equals("EDIT_DATA")){
                listener.onSucces(true, masterBarang.getFoto_master_barang());
            }else{
                listener.onSucces(true, "none");
            }
        }
    }

    private void goToMasterBarangActivity(){
        Intent cv_master_barang = new Intent(this, MasterBarangActivity.class);
        startActivity(cv_master_barang);
        finish();
    }

    private void loadDataFromPackage(){
        mode = "EDIT_DATA";
        loadImage(masterBarang.getFoto_master_barang(), ivFotoBarang);
        etNamaBarang.setText(masterBarang.getNama_master_barang());
    }

    void loadImage(String imageSource, ImageView bindOn){
        if(!imageSource.equals("none")){
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Drawable drawableBitmap = new BitmapDrawable(TambahEditMasterBarang.this.getResources(), bitmap);
                    bindOn.setBackground(drawableBitmap);
                    Log.d(TAG, "onBitmapLoaded: loaded");
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    bindOn.setBackgroundResource(R.drawable.ic_launcher_foreground);
                    Log.d(TAG, "onBitmapLoaded: failed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    bindOn.setBackgroundResource(R.drawable.progress);
                    Log.d(TAG, "onBitmapLoaded: prepare");
                }
            };

            Picasso.get().load(imageSource).into(target);
            bindOn.setTag(target);
            Picasso.get().setLoggingEnabled(true);
        }
    }

    public interface onFetchDataListener{
        void onSuccessGenerateData(String kodeBarang, boolean hasSimiliarName);
        void onSuccessCheckSimiliarName(boolean hasSimiliarName);
    }

    public interface onUploadFileListener{
        void onSucces(boolean uploaded,String uri);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}