package com.example.myewaste;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.myewaste.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.myewaste.Login.DEFAULT_EXTRAS_NAME;
import static com.example.myewaste.Util.getRegisterCode;

public class SplashScreen extends AppCompatActivity {
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //menghilangkan ActionBar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);

        getSupportActionBar().hide();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
                if(sessionManagement.getUserSession().equals("none")){
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }else{
                    Log.d("tes", "run: session found");
                    String noRegis = sessionManagement.getUserSession();
                    String initialCode = getRegisterCode(noRegis);
                    lookForUserData(noRegis, initialCode);
                }
            }
        }, 3000L); //3000 L = 3 detik
    }
    private void lookForUserData(String noRegis, String initialCode){
        Query userDataQuery = databaseReference.child("userdata").child(noRegis);
        userDataQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if(task.getResult() != null){
                        UserData userData = task.getResult().getValue(UserData.class);
                        switch (initialCode){
                            case "sa" : routeToSuperAdmin(userData);break;
                            case "t" : routeToTeller(userData);break;
                            case "n" : routeToNasabah(userData);break;
                        }
                    }
                }
            }
        });
    }

    private void routeToSuperAdmin(UserData userData) {
        Intent intent = new Intent(this, SuperAdminActivity.class);
        intent.putExtra(DEFAULT_EXTRAS_NAME,userData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        Log.d("route", "route to superadmin");
    }

    private void routeToTeller(UserData userData) {
        Intent intent = new Intent(this, TellerActivity.class);
        intent.putExtra(DEFAULT_EXTRAS_NAME,userData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        Log.d("route", "route to teller");
    }

    private void routeToNasabah(UserData userData) {
        Intent intent = new Intent(this, NasabahActivity.class);
        intent.putExtra(DEFAULT_EXTRAS_NAME,userData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

        Log.d("route", "route to nasabah");
    }
}