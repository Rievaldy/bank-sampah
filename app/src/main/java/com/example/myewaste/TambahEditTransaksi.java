package com.example.myewaste;

import static com.example.myewaste.Util.getRegisterCode;
import static com.example.myewaste.Util.increseNumber;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myewaste.model.MasterBarang;
import com.example.myewaste.model.MasterJenisBarang;
import com.example.myewaste.model.Saldo;
import com.example.myewaste.model.SatuanModel;
import com.example.myewaste.model.TransaksiBarang;
import com.example.myewaste.model.TransaksiSaldo;
import com.example.myewaste.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TambahEditTransaksi extends AppCompatActivity {
    private Spinner spinnerNasabah;
    private Spinner spinnerJenisBarang;
    private TextView namaBarang;
    private TextView namaSatuan;
    private EditText harga;
    private EditText jumlahBarang;
    private EditText totalHarga;
    private EditText keterangan;
    private Button button;

    private TransaksiBarang masterTransaksi;
    private TransaksiSaldo transaksiSaldo;
    private static final String TAG = "MasterTransaksi";
    private int mode = 0; //0 for tambah, 1 for edit
    private SessionManagement sessionManagement;
    public static final String DEFAULT_KODE_TRANSAKSI_BARANG = "TRB-0001";
    public static final String DEFAULT_KODE_TRANSAKSI_SALDO = "TRS-0001";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<UserData> dataNasabah = new ArrayList<>();
    ArrayList<MasterJenisBarang> dataJenisBarang = new ArrayList<>();
    onFetchDataListener listener;
    SatuanModel satuanModel;
    MasterBarang masterBarang;
    int lastTotalHarga = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_input_teller);

        getSupportActionBar().setTitle("Tambah Transaksi Barang");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerNasabah = findViewById(R.id.spin_nasabah);
        namaBarang = findViewById(R.id.tv_nama_barang);
        spinnerJenisBarang = findViewById(R.id.spin_jenisBarang);
        namaSatuan = findViewById(R.id.tv_satuan);
        harga = findViewById(R.id.txt_harga);
        jumlahBarang = findViewById(R.id.txt_jumlah);
        totalHarga = findViewById(R.id.txt_total);
        keterangan = findViewById(R.id.txt_keterangan);
        button = findViewById(R.id.btnadd);
        sessionManagement = new SessionManagement(getApplicationContext());
        transaksiSaldo = new TransaksiSaldo();
        if(getIntent().hasExtra("EXTRA_TRANSAKSI_BARANG")){
            getSupportActionBar().setTitle("Edit Transaksi Barang");
            masterTransaksi = getIntent().getParcelableExtra("EXTRA_TRANSAKSI_BARANG");
            mode = 1;
            jumlahBarang.setText(String.valueOf(masterTransaksi.getJumlah()));
            lastTotalHarga = masterTransaksi.getTotal_harga();
            totalHarga.setText(String.valueOf(masterTransaksi.getTotal_harga()));
            keterangan.setText(masterTransaksi.getKeterangan());
        }else{
            masterTransaksi = new TransaksiBarang();
            masterTransaksi.setTanggal_transaksi(System.currentTimeMillis());
        }

        listener = new onFetchDataListener() {
            @Override
            public void onSuccessGenerateKodeTrBarang(String noTransaksi) {
                if(masterTransaksi.getNo_nasabah() == null) {
                    Toast.makeText(TambahEditTransaksi.this, "Harap Pilih Nama Nasabah", Toast.LENGTH_SHORT).show();
                }else if(masterTransaksi.getNomor_jenis_barang() == null) {
                    Toast.makeText(TambahEditTransaksi.this, "Harap Pilih Jenis Barang", Toast.LENGTH_SHORT).show();
                }else if(jumlahBarang.getText().toString().equals("")) {
                    Toast.makeText(TambahEditTransaksi.this, "Masukan Jumlah Barang", Toast.LENGTH_SHORT).show();
                }else{
                    masterTransaksi.setNo_transaksi_barang(noTransaksi);
                    masterTransaksi.setNo_teller(sessionManagement.getUserSession());
                    masterTransaksi.setJumlah(Integer.valueOf(jumlahBarang.getText().toString()));
                    masterTransaksi.setTotal_harga(Integer.valueOf(totalHarga.getText().toString()));
                    masterTransaksi.setKeterangan(keterangan.getText().toString());
                    fetchDataTransaksiSaldo();
                }
            }

            @Override
            public void onSuccessGenerateKodeTrSaldo(String noTransaksiSaldo) {
                masterTransaksi.setNo_transaksi_saldo(noTransaksiSaldo);
                transaksiSaldo.setId_transaksi_saldo(noTransaksiSaldo);
                onSubmit(masterTransaksi);
            }
        };

        //todo clue for date range
//        Date test = new Date();
//        Timestamp ha = new Timestamp(test.getTime());

        jumlahBarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!(String.valueOf(charSequence).equals("")) && !harga.getText().toString().equals("")){
                    setTotalHarga();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        jumlahBarang.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if(!onFocus && jumlahBarang.getText().toString().equals("")){
                    jumlahBarang.setText("0");
                }
            }
        });

        showDataSpinnerNasabah();
        showDataSpinnerJenisBarang();


        //Tambah Data
        button.setOnClickListener(view ->
        {
            fetchDataMasterTeller();
        });


        //Input Value Spinner
        spinnerNasabah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posisiNasabah, long l) {
                if(posisiNasabah > 0){
                    final String valueNasabah = dataNasabah.get(posisiNasabah).getNoregis();
                    masterTransaksi.setNo_nasabah(valueNasabah);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerJenisBarang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posisiJenisBarang, long l) {
                if(posisiJenisBarang > 0){
                    masterTransaksi.setNomor_jenis_barang(dataJenisBarang.get(posisiJenisBarang).getNo_master_jenis_barang());
                    String getHarga = String.valueOf(dataJenisBarang.get(posisiJenisBarang).getHarga());
                    harga.setText(getHarga);
                    loadSatuanById(dataJenisBarang.get(posisiJenisBarang).getNo_satuan_barang());
                    loadBarangById(dataJenisBarang.get(posisiJenisBarang).getNo_master_barang());
                    if(!jumlahBarang.getText().toString().isEmpty()){
                        setTotalHarga();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadSatuanById(String idSatuan){
        Query satuanQuery = databaseReference.child("satuan_barang").orderByChild("noSatuan").equalTo(idSatuan);
        satuanQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    satuanModel = data.getValue(SatuanModel.class);
                }
                namaSatuan.setText(satuanModel.getNamaSatuan());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadBarangById(String idBarang){
        Query barangQuery = databaseReference.child("barang").orderByChild("no_master_barang").equalTo(idBarang);
        barangQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    masterBarang = data.getValue(MasterBarang.class);
                }
                namaBarang.setText(masterBarang.getNama_master_barang());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onSubmit(TransaksiBarang masterTransaksi)
    {
        DatabaseReference saldoReference = databaseReference.child("saldonasabah").child(masterTransaksi.getNo_nasabah());
        DatabaseReference dbReferencesTeller = databaseReference.child("transaksi_barang").child(masterTransaksi.getNo_transaksi_barang());
        dbReferencesTeller.setValue(masterTransaksi).addOnSuccessListener(aVoid->
        {
            saldoReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Saldo saldoNasabah = snapshot.getValue(Saldo.class);
                    int currentBalance = saldoNasabah.getSaldo();
                    int newBalance;
                    if(mode == 0){
                        newBalance = currentBalance + masterTransaksi.getTotal_harga();
                        saldoNasabah.setSaldo(newBalance);
                        saldoReference.setValue(saldoNasabah);
                    }else{
                        int removeLastBalance = currentBalance - lastTotalHarga;
                        newBalance = removeLastBalance + masterTransaksi.getTotal_harga();
                        saldoNasabah.setSaldo(newBalance);
                        saldoReference.setValue(saldoNasabah);
                    }

                    createTransaksiSaldo();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void createTransaksiSaldo(){
        transaksiSaldo.setId_nasabah(masterTransaksi.getNo_nasabah());
        transaksiSaldo.setId_penerima(masterTransaksi.getNo_teller());
        transaksiSaldo.setJenis_transaksi("SETOR");
        transaksiSaldo.setJumlah_transaksi(masterTransaksi.getTotal_harga());
        transaksiSaldo.setPotongan(0);
        transaksiSaldo.setStatus("ACCEPTED");
        transaksiSaldo.setTanggal_transaksi(masterTransaksi.getTanggal_transaksi());
        DatabaseReference transaksiSaldoReference = databaseReference.child("transaksi_saldo").child(masterTransaksi.getNo_transaksi_saldo());
        transaksiSaldoReference.setValue(transaksiSaldo).addOnSuccessListener(aVoid->
        {
            Toast.makeText(TambahEditTransaksi.this, "Berhasil Menambahkan Jenis Barang", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    //ID Otomatis
    private void fetchDataMasterTeller()
    {
        databaseReference.child("transaksi_barang").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String kodeTransaksi = DEFAULT_KODE_TRANSAKSI_BARANG;
                if(task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    if(result.getValue() != null) {
                        for (DataSnapshot dataSnapshot : result.getChildren()) {
                            if(mode == 0)
                            {
                                String dataKodeTransaksi = dataSnapshot.getKey();
                                if (dataKodeTransaksi != null) {
                                    kodeTransaksi = increseNumber(dataKodeTransaksi);
                                }
                            }else{
                                kodeTransaksi = masterTransaksi.getNo_transaksi_barang();
                            }
                        }
                    }
                }
                listener.onSuccessGenerateKodeTrBarang(kodeTransaksi);
            }
        });
    }

    //ID Otomatis
    private void fetchDataTransaksiSaldo()
    {
        databaseReference.child("transaksi_saldo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String kodeTransaksiSaldo = DEFAULT_KODE_TRANSAKSI_SALDO;
                if(task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    if(result.getValue() != null) {
                        for (DataSnapshot dataSnapshot : result.getChildren()) {
                            if(mode == 0)
                            {
                                String dataKodeTransaksi = dataSnapshot.getKey();
                                if (dataKodeTransaksi != null) {
                                    kodeTransaksiSaldo = increseNumber(dataKodeTransaksi);
                                }
                            }else{
                                kodeTransaksiSaldo = masterTransaksi.getNo_transaksi_saldo();
                            }
                        }
                    }
                }
                listener.onSuccessGenerateKodeTrSaldo(kodeTransaksiSaldo);
            }
        });
    }

    //View Spinner
    private void showDataSpinnerNasabah()
    {

        UserData dumy = new UserData();
        dumy.setNama("Pilih Nasabah");
        dataNasabah.add(0, dumy);
        DatabaseReference refrenceNasabah = databaseReference.child("userdata");
        refrenceNasabah.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 0;
                for(DataSnapshot item: snapshot.getChildren())
                {
                    UserData userData = item.getValue(UserData.class);
                    if(getRegisterCode(userData.getNoregis()).toLowerCase().equals("n"))
                    {
                        dataNasabah.add(userData);
                        if(mode == 1 && userData.getNoregis().toLowerCase().equals(masterTransaksi.getNo_nasabah().toLowerCase())){
                            index = dataNasabah.size() -1;
                        }
                    }

                }
                ArrayAdapter<UserData> arrayAdapter = new ArrayAdapter<>(TambahEditTransaksi.this, R.layout.style_spinner, dataNasabah);
                spinnerNasabah.setAdapter(arrayAdapter);
                spinnerNasabah.setSelection(index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataSpinnerJenisBarang()
    {
        MasterJenisBarang dumy = new MasterJenisBarang();
        dumy.setNama_master_jenis_barang("Pilih Jenis Barang");
        dataJenisBarang.add(0, dumy);
        DatabaseReference referenceJenisBarang = databaseReference.child("jenis_barang");
        referenceJenisBarang.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 0;
                for(DataSnapshot item: snapshot.getChildren())
                {
                    MasterJenisBarang pilihBarang = item.getValue(MasterJenisBarang.class);
                    dataJenisBarang.add(pilihBarang);
                    if(mode == 1 && pilihBarang.getNo_master_jenis_barang().toLowerCase().equals(masterTransaksi.getNomor_jenis_barang().toLowerCase())){
                        index = dataJenisBarang.size() -1;
                    }
                }
                ArrayAdapter<MasterJenisBarang> arrayAdapter = new ArrayAdapter<>(TambahEditTransaksi.this, R.layout.style_spinner, dataJenisBarang);
                spinnerJenisBarang.setAdapter(arrayAdapter);
                spinnerJenisBarang.setSelection(index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void setTotalHarga(){
        Integer hargaToInt = Integer.parseInt(harga.getText().toString()); //kegunaannya untuk merubah harga yang tadinya berbentuk string menjadi integer
        Integer jumlahToInt = Integer.parseInt(jumlahBarang.getText().toString()); //kegunaannya untuk merubah jumlah yang tadinya berbentuk string menjadi integer
        Integer hasilTotal =  hargaToInt * jumlahToInt; // totalkan value
        totalHarga.setText(String.valueOf(hasilTotal));
    }
    public interface onFetchDataListener {
        void onSuccessGenerateKodeTrBarang(String noTransaksi);
        void onSuccessGenerateKodeTrSaldo(String noTransaksi);
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
}
