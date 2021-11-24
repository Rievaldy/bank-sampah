package com.example.myewaste;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myewaste.model.Saldo;
import com.example.myewaste.model.User;
import com.example.myewaste.model.UserData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import static com.example.myewaste.Util.getRegisterAs;
import static com.example.myewaste.Util.getRegisterCode;

public class TambahDataUser extends AppCompatActivity {

    private Util.MODE mode;
    private static final String TAG = "registrasi";
    private static final String DEFAULT_NO_REGIST_SUPER_ADMIN = "SA-001";
    private static final String DEFAULT_NO_REGIST_TELLER = "T-001";
    private static final String DEFAULT_NO_REGIST_NASABAH = "N-001";
    private static final int PICK_IMAGE_REQUEST = 22;
    private ImageView imageView;
    private DatabaseReference database;
    private StorageReference storageReference;

    private Uri filePath;
    private Button btnUpload;
    private EditText etNoregis;
    private EditText etNik;
    private EditText etNama;
    private EditText etNotelp;
    private EditText etAlamat;
    private EditText etRT;
    private EditText etRW;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etFoto;
    private RadioGroup rgKelamin;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_user);


        if (getIntent().hasExtra("mode")){

            mode = (Util.MODE) getIntent().getSerializableExtra("mode");
            switch (mode){
                case MODE_SUPER_ADMIN :
                    setTitle("Tambah Data Super Admin");
                    break;
                case MODE_NASABAH :
                    setTitle("Tambah Data Nasabah");
                    break;
                case MODE_TELLER :
                    setTitle("Tambah Data Teller");
                    break;
                case MODE_REGISTRASI :
                    setTitle("Register");
                    break;
                default: break;
            }
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inisialiasi database
        database = FirebaseDatabase.getInstance().getReference();
        storageReference  = FirebaseStorage.getInstance().getReference();

        // Inisialiasi field
        generateNoRegis();
        etNoregis = findViewById(R.id.et_noregis);
        etNik = findViewById(R.id.et_nik);
        etNama = findViewById(R.id.et_nama);
        rgKelamin = findViewById(R.id.rgKelamin);
        etNotelp = findViewById(R.id.et_notelp);
        etAlamat = findViewById(R.id.et_alamat);
        etRT = findViewById(R.id.et_rt);
        etRW = findViewById(R.id.et_rw);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etFoto = findViewById(R.id.et_foto);
        btnUpload = findViewById(R.id.btn_upload);

        etFoto.setFocusable(false);
        etFoto.setOnClickListener( v -> selectImage() );

        btnUpload.setOnClickListener(v -> uploadImage() );

        //Fungsi Button
        findViewById(R.id.btn_simpan).setOnClickListener(v -> {

            Integer buttonId = rgKelamin.getCheckedRadioButtonId();
            RadioButton rbkelamin = findViewById(buttonId);
            Log.d("Kelamin", rbkelamin.getText().toString());

            String Snoregis = etNoregis.getText().toString().toUpperCase();
            String Snik = etNik.getText().toString().toLowerCase();
            String Snama = etNama.getText().toString().toLowerCase();
            String Skelamin = rbkelamin.getText().toString().toLowerCase();
            String Snotelp = etNotelp.getText().toString().toLowerCase();
            String Salamat = etAlamat.getText().toString().toLowerCase();
            String SRT = etRT.getText().toString().toLowerCase();
            String SRW = etRW.getText().toString().toLowerCase();
            String Susername = etUsername.getText().toString();
            String Spassword = etPassword.getText().toString();

            //validasi saat input
            if (Snoregis.equals("")) {
                etNoregis.setError("Silahkan Masukan No. Register");
                etNoregis.requestFocus();
            }else if (Snik.equals("")) {
                etNik.setError("Silahkan Masukan NIK");
                etNik.requestFocus();
            }else if (Snama.equals("")) {
                etNama.setError("Silahkan Masukan Nama");
                etNama.requestFocus();
            } else if (Snotelp.equals("")) {
                etNotelp.setError("Silahkan Masukan No. Telepon");
                etNotelp.requestFocus();
            } else if (Salamat.equals("")) {
                etAlamat.setError("Silahkan Masukan Alamat");
                etAlamat.requestFocus();
            } else if (SRT.equals("")) {
                etRT.setError("Silahkan Masukan RT");
                etRT.requestFocus();
            } else if (SRW.equals("")) {
                etRW.setError("Silahkan Masukan RW");
                etRW.requestFocus();
            } else if (Susername.equals("")) {
                etUsername.setError("Silahkan Masukan Username");
                etUsername.requestFocus();
            } else if (Spassword.equals("")) {
                etPassword.setError("Silahkan Masukan Password");
                etPassword.requestFocus();
            } else {
                loading = ProgressDialog.show(TambahDataUser.this,
                        null,
                        "please wait...",
                        true,
                        false);

                //fungsi menambahkan data ke firebase dengan memanfaatkan class request.java
                submitUser(
                        new User(
                                Susername,
                                Spassword,
                                Snoregis.toLowerCase(),
                                "aktif"
                        ),
                        new UserData(
                                Snoregis.toLowerCase(),
                                "none",
                                Snik,
                                Snama,
                                Skelamin,
                                Snotelp,
                                Salamat,
                                SRT,
                                SRW
                        )
                );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            filePath = data.getData();
            etFoto.setText(filePath.toString());
        }
    }
    //fungsi mengupload image setelah memilih gambar di device
    private void uploadImage() {
        if(filePath != null) {
            if(etNoregis.getText() != null && getRegisterAs(getRegisterCode(etNoregis.getText().toString())) != "-") {
                btnUpload.setEnabled(false);
                //menambahkan folder di storage firebase images/
                storageReference.child("images/" + etNoregis.getText().toString().toLowerCase()).putFile(filePath)
                        .addOnSuccessListener(l -> {
                            Toast.makeText(TambahDataUser.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                            btnUpload.setEnabled(true);
                        })
                        .addOnFailureListener(l -> {
                            Toast.makeText(TambahDataUser.this, "Fail upload!!", Toast.LENGTH_SHORT).show();
                            btnUpload.setEnabled(true);
                        })
                        .addOnCompleteListener(l -> {
                            if(l.getException() != null)
                                Log.d(TAG, (l.getException().getMessage()));
                        });
            }
            else {
                Toast.makeText(TambahDataUser.this,"Invalid register code!!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(TambahDataUser.this,"Enter the file first!!", Toast.LENGTH_SHORT).show();
        }
    }

    // Select Image method
    private void selectImage()
    {
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

    private void submitUser(User user, UserData userData) {
        String registerCode = getRegisterCode(etNoregis.getText().toString());
        String child = getRegisterAs(registerCode);

        if(!child.equals("-")) {
            // Register data
            DatabaseReference childNIK = database.child("userdata").child(etNoregis.getText().toString().toLowerCase());
            DatabaseReference fSaldoNasabah = database.child("saldonasabah").child(etNoregis.getText().toString().toLowerCase());
            childNIK.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()) { // nik sudah terdaftar
                        this.onCancelled(DatabaseError.fromStatus("401", "NIK telah terdaftar", "Ongee"));

                        loading.dismiss();
                        Toast.makeText(TambahDataUser.this, "NIK telah terdaftar", Toast.LENGTH_SHORT).show();
                    }
                    else { // belum terdaftar
                        // do ketika belum terdaftar
                        DatabaseReference childUsername = database.child("user").child(etUsername.getText().toString());
                        childUsername.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    this.onCancelled(DatabaseError.fromStatus("401", "Username telah terdaftar", "Ongee"));
                                    loading.dismiss();
                                    Toast.makeText(TambahDataUser.this, "Username telah terdaftar", Toast.LENGTH_SHORT).show();
                                }else{
                                    childUsername.setValue(user)
                                            .addOnSuccessListener(TambahDataUser.this, onSuccess -> {
                                                generateNoRegis();
                                                etNoregis.setText("");
                                                etNik.setText("");
                                                etNama.setText("");
                                                etNotelp.setText("");
                                                etAlamat.setText("");
                                                etRT.setText("");
                                                etRW.setText("");
                                                etUsername.setText("");
                                                etPassword.setText("");

                                                loading.dismiss();
                                                Log.d("setvalue user", "success "+userData.toString());
                                                switch (mode){
                                                    case MODE_SUPER_ADMIN :
                                                        Toast.makeText(TambahDataUser.this, "Super Admin Berhasil di Tambah", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case MODE_NASABAH :
                                                        Toast.makeText(TambahDataUser.this, "Nasabah Berhasil di Tambah", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case MODE_TELLER :
                                                        Toast.makeText(TambahDataUser.this, "Teller Berhasil di Tambah", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case MODE_REGISTRASI :
                                                        Toast.makeText(TambahDataUser.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    default: break;
                                                }

                                            })
                                            .addOnFailureListener(TambahDataUser.this, exception -> {
                                                loading.dismiss();
                                                Toast.makeText(TambahDataUser.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                    childNIK.setValue(userData);
                                    if(getRegisterCode(registerCode).toLowerCase().equals("n")){
                                        Log.d("saldo commited", "submitUser: ");
                                        Saldo saldo = new Saldo(userData.getNoregis(), 0);
                                        fSaldoNasabah.setValue(saldo);
                                    }
                                    onBackPressed();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                Log.d("error cancel user", error.getMessage());
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d("error gemin", error.getMessage());
                }
            });

            Log.d(TAG, "submitUser: "+ userData.getNoregis() + "" + getRegisterCode(registerCode).toLowerCase().equals("n"));

        }
        else {
            loading.dismiss();
            Toast.makeText(TambahDataUser.this, "Registrasi gagal, kode no. registrasi tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    public void generateNoRegis() {
        database.child("userdata").get()
                .addOnCompleteListener(task -> {
                    String noregis;
                    if(mode.equals(Util.MODE.MODE_SUPER_ADMIN)){
                        noregis = DEFAULT_NO_REGIST_SUPER_ADMIN;
                    }else if(mode.equals(Util.MODE.MODE_TELLER)){
                        noregis = DEFAULT_NO_REGIST_TELLER;
                    }else{
                        noregis = DEFAULT_NO_REGIST_NASABAH;
                    }

                    if(task.isSuccessful()) {
                        DataSnapshot result = task.getResult();
                        if(result != null) {

                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                boolean isValid = false;
                                Log.d("generate", dataSnapshot.getKey());
                                String dataNoregis = dataSnapshot.getKey();
                                String extractRegisterCode = Util.getRegisterCode(dataNoregis).toLowerCase();
                                if ((dataNoregis != null && extractRegisterCode.equals("n") && (mode.equals(Util.MODE.MODE_NASABAH)  || mode.equals(Util.MODE.MODE_REGISTRASI)))
                                        || (dataNoregis != null && extractRegisterCode.toLowerCase().equals("t") && mode.equals(Util.MODE.MODE_TELLER))
                                        || (dataNoregis != null && extractRegisterCode.toLowerCase().equals("sa") && mode.equals(Util.MODE.MODE_SUPER_ADMIN))) {
                                    isValid = true;
                                    Log.d(TAG, "generateNoRegis: " + isValid);
                                }
                                if(isValid){
                                    noregis = Util.increseNumber(dataNoregis);
                                }
                                Log.d(TAG, "generateNoRegis: " + noregis);
                            }
                        }
                    }
                    etNoregis.setText(noregis);
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}