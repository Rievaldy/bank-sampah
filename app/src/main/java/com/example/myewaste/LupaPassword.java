package com.example.myewaste;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myewaste.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

public class LupaPassword extends AppCompatActivity {

    private DatabaseReference database;
    private EditText etUsername;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnSimpan;
    private Button btnBatal ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lupa_password);
        setTitle("Lupa Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        database = FirebaseDatabase.getInstance().getReference();

        etUsername = findViewById(R.id.et_username);
        etNewPassword = findViewById(R.id.et_newpassword);
        etConfirmPassword = findViewById(R.id.et_konfirmpass);

        btnSimpan = findViewById(R.id.btn_simpan);
        btnSimpan.setOnClickListener(v -> {
            new AlertDialog.Builder(LupaPassword.this)
                .setTitle("Alert!")
                .setMessage("Anda yakin ingin mengganti password?")
                .setPositiveButton("OK", (dialog, which) -> {
                    performLupaPassword();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(LupaPassword.this, "Kamu memilih Cancel", Toast.LENGTH_SHORT).show();
                })
                .show();
        });

        btnBatal = findViewById(R.id.btn_batal);
        btnBatal.setOnClickListener(v -> {
            new AlertDialog.Builder(LupaPassword.this)
                .setTitle("Alert!")
                .setMessage("Pengantian Password bATAL")
                .setPositiveButton("OK", (dialog, which) -> {
                    Toast.makeText(LupaPassword.this, "Kamu memilih OK", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(LupaPassword.this, "Kamu memilih Cancel", Toast.LENGTH_SHORT).show();
                })
                .show();
        });
    }

    private void performLupaPassword() {
        try {
            database.child("user").child(etUsername.getText().toString()).get()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, task.getException().toString(), Toast.LENGTH_LONG).show();

                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot result = task.getResult();
                    if(result != null) {
                        User user = result.getValue(new GenericTypeIndicator<User>() {
                        });
                        if(user != null) {
                            if(etNewPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                                user.password = etNewPassword.getText().toString();
                                database.child("user").child(etUsername.getText().toString())
                                    .setValue(user).addOnCompleteListener((task1) -> {
                                        etNewPassword.setText("");
                                        etConfirmPassword.setText("");

                                        Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_LONG).show();
                                });
                            }
                            else {
                                Toast.makeText(this, "Password baru tidak sesuai dengan konfirmasi", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_LONG).show();
                    }
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            });
        }
        catch (Exception e) {
            Log.e("performLupaPassword", e.getMessage());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
