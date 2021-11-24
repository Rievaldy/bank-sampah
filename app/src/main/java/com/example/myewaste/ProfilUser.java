package com.example.myewaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myewaste.model.UserData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilUser extends AppCompatActivity {
    private UserData userdata;
    private CircleImageView ivUserProfil, ivUpload;
    private LinearLayout layoutNik, layoutNama, layoutJenisKelamin, layoutTelepon, layoutAlamat;
    private TextView tvNik, tvNama, tvJenisKelamin, tvTelepon, tvAlamat;
    public static final String DEFAULT_EXTRAS_NAME = "USER_DATA_EXTRAS";
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final String TAG = "ProfilSuperAdmin";
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_data_super_admin);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        ivUserProfil = findViewById(R.id.ivDetailUserImage);
        ivUpload = findViewById(R.id.ivDetailUploadUserImage);
        layoutNik = findViewById(R.id.layoutDetailNikUser);
        layoutNama = findViewById(R.id.layoutDetailNamaUser);
        layoutJenisKelamin = findViewById(R.id.layoutDetailJKUser);
        layoutTelepon = findViewById(R.id.layoutDetailTeleponUser);
        layoutAlamat = findViewById(R.id.layoutDetailAlamatUser);
        tvNik = findViewById(R.id.tvNikUser);
        tvNama = findViewById(R.id.tvDetailNamaUser);
        tvJenisKelamin = findViewById(R.id.tvDetailJKUser);
        tvTelepon = findViewById(R.id.tvDetailTeleponUser);
        tvAlamat = findViewById(R.id.tvDetailAlamatUser);

        layoutNik.setOnClickListener(view -> showDialogEditText("Masukan Nik Anda", "nik"));
        layoutNama.setOnClickListener(view -> showDialogEditText("Masukan Nama Anda", "nama"));
        layoutJenisKelamin.setOnClickListener(view -> showDialogEditText("Masukan Jenis Kelamin", "kelamin"));
        layoutTelepon.setOnClickListener(view -> showDialogEditText("Masukan Telepon Anda", "notelp"));
        layoutAlamat.setOnClickListener(view -> showDialogEditText("Masukan Alamat Anda", "alamat"));
        ivUpload.setOnClickListener(view -> SelectImage());

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getParcelableExtra(DEFAULT_EXTRAS_NAME) != null){
            userdata = getIntent().getParcelableExtra(DEFAULT_EXTRAS_NAME);
            Log.d(TAG, "onCreate: " +userdata.getNama());
            loadData();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfilUser.this, SuperAdminActivity.class);
        intent.putExtra(DEFAULT_EXTRAS_NAME, userdata);
        setResult(Activity.RESULT_OK, intent);
        finish();
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
                uploadImage();
            }catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    void loadData(){
        Util.loadImage(userdata.getFotoProfil(), ivUserProfil, ProfilUser.this);
        tvNik.setText(userdata.getNik());
        tvNama.setText(userdata.getNama());
        tvJenisKelamin.setText(userdata.getKelamin());
        tvTelepon.setText(userdata.getNotelp());
        tvAlamat.setText(userdata.getAlamat()+", RT."+userdata.getRT()+"/RW."+userdata.getRW());
    }

    private void uploadImage() {
        if (filePath != null) {
            //menambahkan folder di storage firebase images/
            StorageReference referencesProfilImage = storageReference.child("images/profil/" + userdata.getNoregis().toLowerCase());
            referencesProfilImage.putFile(filePath)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return referencesProfilImage.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        userdata.setFotoProfil(downloadUri.toString());
                        Util.loadImage(userdata.getFotoProfil(), ivUserProfil, ProfilUser.this);
                        EditData("fotoProfil", downloadUri.toString(), "Berhasil Merubah Profil");
                    }else {
                        Log.d(TAG, "onComplete: " + task.getException());
                        showToast("Failed");
                    }
                }
            });
        }
    }

    private void EditData(String targetPath, String value, String successMessage){
        databaseReference.child("userdata").child(userdata.getNoregis()).child(targetPath).setValue(value);
        showToast(successMessage);
    }
    private void EditAlamat(String successMessage){
        databaseReference.child("userdata").child(userdata.getNoregis()).setValue(userdata);
        showToast(successMessage);
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

    public void showDialogEditText(String title, String targetPath){
        final BottomSheetDialog dialog = new BottomSheetDialog(this,R.style.BottomSheetDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_edit_user);
        TextView titleDialog = dialog.findViewById(R.id.tvTitleDialog);
        LinearLayout target = dialog.findViewById(R.id.frameEditData);
        titleDialog.setText(title);

        putframe(target, targetPath);
        Button btnSimpan = dialog.findViewById(R.id.btnDialogSimpan);
        btnSimpan.setOnClickListener(view -> {
            if(targetPath.equals("alamat")){
                View v = target.findViewWithTag("dialog_alamat");
                TextInputEditText alamat = v.findViewById(R.id.etDialogAlamat);
                TextInputEditText rt = v.findViewById(R.id.etDialogRT);
                TextInputEditText rw = v.findViewById(R.id.etDialogRW);
                userdata.setAlamat(alamat.getText().toString());
                userdata.setRT(rt.getText().toString());
                userdata.setRW(rw.getText().toString());
                tvAlamat.setText(userdata.getAlamat()+", RT."+userdata.getRT()+"/RW."+userdata.getRW());
                EditAlamat("Berhasil Merubah Alamat");
            }else if( targetPath.equals("kelamin")){
                View v = target.findViewWithTag("dialog_jenis_kelamin");
                RadioGroup radioGroup = v.findViewById(R.id.rgKelaminDialog);
                Integer idchecked = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = v.findViewById(idchecked);
                String value = rb.getText().toString();
                EditData(targetPath, value, "Berhasil Mengubah Jenis Kelamin");
                tvJenisKelamin.setText(value);
                userdata.setKelamin(value);
            }else{
                View v = target.findViewWithTag("dialog_edittext");
                TextInputEditText etData = v.findViewById(R.id.etDataEdited);
                String value = etData.getText().toString();
                switch (targetPath) {
                    case "nik":
                        EditData(targetPath, value, "Berhasil Mengubah NIK");
                        tvNik.setText(value);
                        userdata.setNik(value);
                        break;
                    case "nama":
                        EditData(targetPath, value, "Berhasil Mengubah Nama");
                        tvNama.setText(value);
                        userdata.setNama(value);
                        break;
                    case "notelp":
                        EditData(targetPath, value, "Berhasil Mengubah Telepon");
                        tvTelepon.setText(value);
                        userdata.setNotelp(value);
                        break;
                    default:
                        break;
                }
            }
            dialog.dismiss();
        });
        Button btnBatal = dialog.findViewById(R.id.btnDialogBatal);
        btnBatal.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    void putframe(LinearLayout target, String targetPath){
        View child;
        if(targetPath.equals("kelamin")){
            child = getLayoutInflater().inflate(R.layout.frame_custom_dialog_jenis_kelamin, target,false);
            child.setTag("dialog_jenis_kelamin");
            RadioGroup rgKelamin = child.findViewById(R.id.rgKelaminDialog);
            if(userdata.getKelamin().toLowerCase().equals("perempuan")){
                rgKelamin.check(R.id.rdDialogPerempuan);
            }else{
                rgKelamin.check(R.id.rdDialogLakilaki);
            }
        }else if(targetPath.equals("alamat")){
            child = getLayoutInflater().inflate(R.layout.frame_multi_edit_custom_dialog_, target,false);
            child.setTag("dialog_alamat");
            TextInputEditText etAlamat = child.findViewById(R.id.etDialogAlamat);
            TextInputEditText etRT = child.findViewById(R.id.etDialogRT);
            TextInputEditText etRW = child.findViewById(R.id.etDialogRW);

            etAlamat.setText(userdata.getAlamat());
            etRT.setText(userdata.getRT());
            etRW.setText(userdata.getRW());

        }else{
            child = getLayoutInflater().inflate(R.layout.frame_custom_dialog_edit, target,false);
            child.setTag("dialog_edittext");
            TextInputEditText editdata = child.findViewById(R.id.etDataEdited);
            switch (targetPath){
                case "nik": editdata.setText(userdata.getNik());break;
                case "nama" : editdata.setText(userdata.getNama());break;
                case "notelp" : editdata.setText(userdata.getNotelp());break;
                default : break;
            }
        }
        target.addView(child);
    }

    void showToast(String message){
        Toast.makeText(ProfilUser.this, message, Toast.LENGTH_SHORT).show();
    }
}