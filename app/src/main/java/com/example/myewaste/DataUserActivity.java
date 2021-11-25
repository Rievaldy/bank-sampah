package com.example.myewaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myewaste.adapter.DataUserAdapter;
import com.example.myewaste.model.MasterBarang;
import com.example.myewaste.model.MasterJenisBarang;
import com.example.myewaste.model.SatuanModel;
import com.example.myewaste.model.UserData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import static com.example.myewaste.Util.showMessage;

public class DataUserActivity extends AppCompatActivity {
    private DatabaseReference reference;
    ArrayList<UserData> listUserData;
    DataUserAdapter adapter;
    private Util.MODE mode;
    private static final String TAG = "DataNasabahActivity";
    private ImageView filter;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int STORAGE_PERMISSION_CODE = 1;

    FloatingActionButton btn_addnasabah, btnDownload;

    private RecyclerView mRecyler;
    private LinearLayoutManager mManager;
    /*private RecyclerView dataNasabahRecylcer;
    private LinearLayoutManager dataNasabahManager;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_user);

        mRecyler = findViewById(R.id.list_data_nasabah);
        mRecyler.setHasFixedSize(true);

        filter = findViewById(R.id.filterUser);
        filter.setOnClickListener(view -> {

        });

        btn_addnasabah = findViewById(R.id.btn_addnasabah);
        btnDownload = findViewById(R.id.btn_downloadDataNasabah);

        if(getIntent().hasExtra("mode")){
            mode = (Util.MODE) getIntent().getSerializableExtra("mode");
            switch (mode){
                case MODE_SUPER_ADMIN :
                    setTitle("Data Super Admin");
                    break;
                case MODE_NASABAH :
                    setTitle("Data Nasabah");
                    btnDownload.setVisibility(View.VISIBLE);
                    break;
                case MODE_TELLER :
                    setTitle("Data Teller");
                    break;
                default: break;
            }
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



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

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    createExcelFileReportBarang();
                }else{
                    requestStoragePermission();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createExcelFileReportBarang(){
        File filePath = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "MyEwaste");

        if(!filePath.exists()){
            if(filePath.mkdir()){
                filePath = new File(filePath.getAbsolutePath() + File.separator +"data_nasabah_"+System.currentTimeMillis()+".xls");
            }else{
                showMessage(DataUserActivity.this, "Failed To make Directory");
            }
        }else{
            filePath = new File(filePath.getAbsolutePath() + File.separator +"data_nasabah_"+System.currentTimeMillis()+".xls");
        }
        //todo create new workbook
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        //todo create new worksheet
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Data Nasabah");

        //cell tanggal
        HSSFRow rowTanggal =hssfSheet.createRow(0);
        HSSFCell cellTanggal = rowTanggal.createCell(0);
        cellTanggal.setCellValue("Data Nasabah My Ewaste");
        hssfSheet.addMergedRegion(new CellRangeAddress(0,0,0,5));

        //todo cell style
        CellStyle cellStyle = hssfWorkbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setWrapText(true);

        HSSFRow rowTitle = hssfSheet.createRow(1);
        HSSFCell cellTitleNoRegis = rowTitle.createCell(0);
        cellTitleNoRegis.setCellStyle(cellStyle);
        cellTitleNoRegis.setCellValue("No Regis");

        HSSFCell cellTitleNIK = rowTitle.createCell(1);
        cellTitleNIK.setCellStyle(cellStyle);
        cellTitleNIK.setCellValue("NIK");

        HSSFCell cellTitleNama = rowTitle.createCell(2);
        cellTitleNama.setCellStyle(cellStyle);
        cellTitleNama.setCellValue("Nama Nasabah");

        HSSFCell cellTitleJK = rowTitle.createCell(3);
        cellTitleJK.setCellStyle(cellStyle);
        cellTitleJK.setCellValue("Jenis Kelamin");

        HSSFCell cellTitleAlamat = rowTitle.createCell(4);
        cellTitleAlamat.setCellStyle(cellStyle);
        cellTitleAlamat.setCellValue("Alamat");

        HSSFCell cellTitleRT = rowTitle.createCell(5);
        cellTitleRT.setCellStyle(cellStyle);
        cellTitleRT.setCellValue("RT");

        HSSFCell cellTitleRW = rowTitle.createCell(6);
        cellTitleRW.setCellStyle(cellStyle);
        cellTitleRW.setCellValue("RW");

        HSSFCell cellTitleNoTelp = rowTitle.createCell(7);
        cellTitleNoTelp.setCellStyle(cellStyle);
        cellTitleNoTelp.setCellValue("No Telp");

        HSSFCell cellTitleProfile = rowTitle.createCell(8);
        cellTitleProfile.setCellStyle(cellStyle);
        cellTitleProfile.setCellValue("Link Foto Profile");

        for(int i = 2; i <= listUserData.size(); i++){

            HSSFRow rowData = hssfSheet.createRow(i);

            HSSFCell cellDataNoregis = rowData.createCell(0);
            cellDataNoregis.setCellStyle(cellStyle);
            cellDataNoregis.setCellValue(listUserData.get(i-2).getNoregis());

            HSSFCell cellDataNIK = rowData.createCell(1);
            cellDataNIK.setCellStyle(cellStyle);
            cellDataNIK.setCellValue(listUserData.get(i-2).getNik());

            HSSFCell cellDataNama = rowData.createCell(2);
            cellDataNama.setCellStyle(cellStyle);
            cellDataNama.setCellValue(listUserData.get(i-2).getNama());

            HSSFCell cellDataJenisKelamin = rowData.createCell(3);
            cellDataJenisKelamin.setCellStyle(cellStyle);
            cellDataJenisKelamin.setCellValue(listUserData.get(i-2).getKelamin());

            HSSFCell cellDataAlamat = rowData.createCell(4);
            cellDataAlamat.setCellStyle(cellStyle);
            cellDataAlamat.setCellValue(listUserData.get(i-2).getAlamat());

            HSSFCell cellDataRT = rowData.createCell(5);
            cellDataRT.setCellStyle(cellStyle);
            cellDataRT.setCellValue(listUserData.get(i-2).getRT());

            HSSFCell cellDataRW = rowData.createCell(6);
            cellDataRW.setCellStyle(cellStyle);
            cellDataRW.setCellValue(listUserData.get(i-2).getRW());

            HSSFCell cellDataNoTelp = rowData.createCell(7);
            cellDataNoTelp.setCellStyle(cellStyle);
            cellDataNoTelp.setCellValue(listUserData.get(i-2).getNotelp());

            HSSFCell cellDataFotoProfile = rowData.createCell(8);
            cellDataFotoProfile.setCellStyle(cellStyle);
            cellDataFotoProfile.setCellValue(listUserData.get(i-2).getFotoProfil());
        }


        hssfSheet.setColumnWidth(8, 12000);

        try{
            if(!filePath.exists()){
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream= new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            if (fileOutputStream!=null){
                fileOutputStream.flush();
                fileOutputStream.close();
                showMessage(DataUserActivity.this, "Location : " +filePath.getAbsolutePath());
                finish();
            }
        }catch (Exception e){
            Log.d("TAG", "exportToExcel: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createExcelFileReportBarang();
            } else {
                Toast.makeText(this, "Tidak Mendapatkan Hak Akses Storage",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("We Need Permission to Access Your Galery to upload your File into Our Database")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(DataUserActivity.this, PERMISSIONS_STORAGE,STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
}