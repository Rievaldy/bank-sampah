package com.example.myewaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myewaste.model.User;
import com.example.myewaste.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;

import static com.example.myewaste.Util.getRegisterCode;


public class Login extends AppCompatActivity {

    private static final String TAG = "login";
    private TextView tvLupaPassword, tvDaftar, etUsername, etPassword;
    private Button btnLogin;
    private DatabaseReference database;
    public static final String DEFAULT_EXTRAS_NAME = "USER_DATA_EXTRAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        getSupportActionBar().hide();

        //Toolbar toolbar=findViewById(R.id.toolbar);
        //setActionBar(toolbar);
        String newBalance = Util.convertToRupiah(1230000);
        Log.d(TAG, "onCreate: "+ newBalance);

        database = FirebaseDatabase.getInstance().getReference();

        etUsername = findViewById(R.id.loginUsername);
        etPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.loginBtn);
        btnLogin.setOnClickListener(v -> {
            performLogin();
        });

        tvLupaPassword = findViewById(R.id.lupapass);
        tvLupaPassword.setOnClickListener(v ->  {

            Intent intent = new Intent(Login.   this, LupaPassword.class);
            startActivity(intent);
        });

        tvDaftar = findViewById(R.id.daftar);
        tvDaftar.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, TambahDataUser.class);
            intent.putExtra("mode", Util.MODE.MODE_REGISTRASI);
            startActivity(intent);
        });

    }

   /* private void performLogin() {
        if(etUsername.getText().toString().isEmpty())
            Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_LONG).show();
        else if(etPassword.getText().toString().isEmpty())
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_LONG).show();
        else {
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
                                if (user.password.equals(etPassword.getText().toString())) {
                                    String initialCode = getRegisterCode(user.noregis);
                                    Log.d("apasih?", initialCode);
                                    if(initialCode.equals("sa")) {
                                        // login superadmin
                                        routeToSuperAdmin();
                                    }
                                    else if(initialCode.equals("t")) {
                                        // login teller
                                        routeToTeller();
                                    }
                                    else if(initialCode.equals("n")) {
                                        // login nasabah
                                        routeToNasabah();
                                    }
                                    else {
                                        Log.d("route", "Dih?");
                                    }

                                } else {
                                    Toast.makeText(this, "Password salah", Toast.LENGTH_LONG).show();
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
                Log.e(TAG, e.getMessage());
            }
        }
    }
    */


    private void performLogin() {
        if(etUsername.getText().toString().isEmpty())
            Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_LONG).show();
        else if(etPassword.getText().toString().isEmpty())
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_LONG).show();
        else {
            try {
                Query userQuery = database.child("user").child(etUsername.getText().toString());
                userQuery.get()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(this, task.getException().toString(), Toast.LENGTH_LONG).show();

                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else {
                                DataSnapshot result = task.getResult();

                                Log.d(TAG, "performLogin: " + result);
                                if(result != null) {
                                    User user = result.getValue(new GenericTypeIndicator<User>() {
                                    });


                                    if(user != null) {
                                        if (user.password.equals(etPassword.getText().toString()) && user.getStatus().equals("aktif")) {
                                            String initialCode = getRegisterCode(user.noregis);
                                            lookForUserData(user.noregis, initialCode);
                                        }else {
                                            if(!user.password.equals(etPassword.getText().toString())) {
                                                Toast.makeText(this, "Password salah", Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(this, "Akun Anda Tidak Aktif Hubungi Admin Untuk Informasi Lebih Lanjut", Toast.LENGTH_LONG).show();
                                            }
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
                Log.e(TAG, e.getMessage());
            }
        }
    }
    private void lookForUserData(String noRegis, String initialCode){
        Query userDataQuery = database.child("userdata").child(noRegis);
        userDataQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if(task.getResult() != null){
                        UserData userData = task.getResult().getValue(UserData.class);
                        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
                        sessionManagement.saveUserSession(userData.getNoregis());
                        switch (initialCode){
                            case "sa" : routeToSuperAdmin(userData);break;
                            case "t" : routeToTeller(userData);break;
                            case "n" : routeToNasabah(userData);break;
                            default: Log.d(TAG, "has problem on look for user data "); break;
                        }
                    }
                }
            }
        });
    }

    private void routeToSuperAdmin(UserData userData) {
        Intent intent = new Intent(this, SuperAdminActivity.class);
        intent.putExtra(DEFAULT_EXTRAS_NAME, userData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        Log.d("route", "route to superadmin");
    }

    private void routeToTeller(UserData userData) {
        Intent intent = new Intent(this, TellerActivity.class);
        intent.putExtra(DEFAULT_EXTRAS_NAME, userData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        Log.d(TAG, "routeToTeller: " + userData.getNoregis());
    }

    private void routeToNasabah(UserData userData) {
        Intent intent = new Intent(this, NasabahActivity.class);
        intent.putExtra(DEFAULT_EXTRAS_NAME, userData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        Log.d("route", "route to nasabah");
    }

}