package com.example.myewaste;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myewaste.adapter.TransaksiBarangAdapter;
import com.example.myewaste.adapter.TransaksiSaldoAdapter;
import com.example.myewaste.model.TransaksiBarang;
import com.example.myewaste.model.TransaksiSaldo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.myewaste.Util.getRegisterCode;
import static com.example.myewaste.Util.showMessage;

public class MasterTransaksiSaldoActivity extends AppCompatActivity {

    private RelativeLayout layoutFilterTanggal;
    private LinearLayout layoutTanggalMulai, layoutTanggalAkhir;
    private ImageView ivFilter;
    private EditText etSearch, etTanggalMulai, etTanggalAkhir;
    private ImageButton btnSearchFilter, closeFilter;

    DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("transaksi_saldo");
    RecyclerView recyclerView;
    TransaksiSaldoAdapter transaksiSaldoAdapter;
    SessionManagement sessionManagement;
    MasterTransaksiSaldoActivity.MasterSaldoListener listener;
    FloatingActionButton btnDownloadExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_saldo);
        //todo add filter and search

        getSupportActionBar().setTitle("Data Transaksi Penarikan");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutFilterTanggal = findViewById(R.id.filterTanggal);
        layoutTanggalMulai = findViewById(R.id.layout_tanggal_start);
        layoutTanggalAkhir = findViewById(R.id.layout_tanggal_end);
        ivFilter = findViewById(R.id.ivFilter);
        etSearch = findViewById(R.id.etSearch);
        etTanggalMulai = findViewById(R.id.et_tanggalStart);
        etTanggalAkhir = findViewById(R.id.et_tanggalEnd);
        btnSearchFilter = findViewById(R.id.searchFilter);
        closeFilter = findViewById(R.id.cancelFilter);

        recyclerView = findViewById(R.id.recycler_view_transaksi_saldo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnDownloadExcel = findViewById(R.id.btnDownloadExclLaporanSaldo);
        sessionManagement = new SessionManagement(getApplicationContext());

        if(!getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("sa")){
            btnDownloadExcel.setVisibility(View.GONE);
        }

        listener = new MasterTransaksiSaldoActivity.MasterSaldoListener() {
            @Override
            public void onClickListTransaksiSaldo(TransaksiSaldo transaksiSaldo) {
                Intent intent = new Intent(MasterTransaksiSaldoActivity.this, DetailTransaksiSaldoActivity.class);
                intent.putExtra("EXTRA_TRANSAKSI_SALDO", transaksiSaldo);
                startActivity(intent);
            }
        };

        etTanggalMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogCalendar(etTanggalMulai, "Tanggal Mulai");
            }
        });

        layoutTanggalMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogCalendar(etTanggalMulai, "Tanggal Mulai");
            }
        });

        etTanggalAkhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogCalendar(etTanggalAkhir, "Tanggal Akhir");
            }
        });

        layoutTanggalAkhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogCalendar(etTanggalAkhir, "Tanggal Akhir");
            }
        });

        btnSearchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etTanggalMulai.getText().toString().isEmpty() && !etTanggalAkhir.getText().toString().isEmpty()) {
                    loadDataFilterByDate(changeFormat(etTanggalMulai.getText().toString()), changeFormat(etTanggalAkhir.getText().toString()));
                    closeFilter.setVisibility(View.VISIBLE);
                    btnSearchFilter.setVisibility(View.GONE);
                }else{
                    showMessage(MasterTransaksiSaldoActivity.this, "Harap Lengkapi Memilih Tanggal");
                }
            }
        });

        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTanggalMulai.setText("");
                etTanggalAkhir.setText("");
                closeFilter.setVisibility(View.GONE);
                btnSearchFilter.setVisibility(View.VISIBLE);
                loadAllData();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.equals("")){
                    searchData(String.valueOf(charSequence));
                }else{
                    if (!etTanggalMulai.getText().toString().isEmpty() && !etTanggalAkhir.getText().toString().isEmpty()) {
                        Log.d("TAG", "onStart: work");
                        loadDataFilterByDate(changeFormat(etTanggalMulai.getText().toString()), changeFormat(etTanggalAkhir.getText().toString()));
                    } else{
                        loadAllData();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutFilterTanggal.getVisibility() == View.GONE) {
                    layoutFilterTanggal.setVisibility(View.VISIBLE);
                } else {
                    layoutFilterTanggal.setVisibility(View.GONE);
                }
            }
        });

        btnDownloadExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MasterTransaksiSaldoActivity.this, SettingDownloadExcel.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!etSearch.getText().toString().isEmpty()){
            searchData(etSearch.getText().toString().toLowerCase());
        }else if(!etTanggalMulai.getText().toString().isEmpty() && !etTanggalAkhir.getText().toString().isEmpty()){
            loadDataFilterByDate(changeFormat(etTanggalMulai.getText().toString()), changeFormat(etTanggalAkhir.getText().toString()));
        }else{
            loadAllData();
        }
    }

    private void loadAllData() {
        Query transaksiQuery = databaseReference.orderByChild("tanggal_transaksi");
        // Read from the database
        if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("n")){
            transaksiQuery = databaseReference.orderByChild("id_nasabah").equalTo(sessionManagement.getUserSession());
        }
        transaksiQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TransaksiSaldo> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    TransaksiSaldo value = data.getValue(TransaksiSaldo.class);
                    if(value.getJenis_transaksi().equals("TARIK")){
                        list.add(value);
                        if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("t")){
                            if((!value.getStatus().equals("PENDING") && !value.getId_penerima().toLowerCase().equals(sessionManagement.getUserSession()))){
                                int lastIndex = list.size()-1;
                                list.remove(lastIndex);
                            }
                        }
                    }
                }
                transaksiSaldoAdapter = new TransaksiSaldoAdapter(MasterTransaksiSaldoActivity.this,list, listener);
                recyclerView.setAdapter(transaksiSaldoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });
    }

    private void loadDataFilterByDate(long start, long end){
        Query transaksiQuery = databaseReference.orderByChild("tanggal_transaksi").startAt(start).endAt(end);
        // Read from the database
        if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("n")){
            transaksiQuery = databaseReference.orderByChild("id_nasabah").equalTo(sessionManagement.getUserSession());
        }
        transaksiQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TransaksiSaldo> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    TransaksiSaldo value = data.getValue(TransaksiSaldo.class);
                    if(value.getJenis_transaksi().equals("TARIK")){
                        list.add(value);
                        if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("t")){
                            if((!value.getStatus().equals("PENDING") && !value.getId_penerima().toLowerCase().equals(sessionManagement.getUserSession()))){
                                int lastIndex = list.size()-1;
                                list.remove(lastIndex);
                            }
                        }
                    }
                }
                transaksiSaldoAdapter = new TransaksiSaldoAdapter(MasterTransaksiSaldoActivity.this,list, listener);
                recyclerView.setAdapter(transaksiSaldoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });
    }

    private void searchData(String str){
        Query transaksiQuery = databaseReference.orderByChild("tanggal_transaksi");
        // Read from the database
        if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("n")){
            transaksiQuery = databaseReference.orderByChild("id_nasabah").equalTo(sessionManagement.getUserSession());
        }
        transaksiQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TransaksiSaldo> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    TransaksiSaldo value = data.getValue(TransaksiSaldo.class);
                    if(value.getJenis_transaksi().equals("TARIK") && value.getId_transaksi_saldo().toLowerCase().contains(str)){
                        list.add(value);
                        if(getRegisterCode(sessionManagement.getUserSession()).toLowerCase().equals("t")){
                            if((!value.getStatus().equals("PENDING") && !value.getId_penerima().toLowerCase().equals(sessionManagement.getUserSession()))){
                                int lastIndex = list.size()-1;
                                list.remove(lastIndex);
                            }
                        }
                    }
                }
                transaksiSaldoAdapter = new TransaksiSaldoAdapter(MasterTransaksiSaldoActivity.this,list, listener);
                recyclerView.setAdapter(transaksiSaldoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });
    }

    private void showDialogCalendar(View AttachTo, String title) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_edit_user);

        TextView titleDialog = dialog.findViewById(R.id.tvTitleDialog);
        Button btnBatal = dialog.findViewById(R.id.btnDialogBatal);
        Button btnSimpan = dialog.findViewById(R.id.btnDialogSimpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        LinearLayout target = dialog.findViewById(R.id.frameEditData);
        titleDialog.setText(title);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View child = inflater.inflate(R.layout.frame_calendar, null);
        CalendarView calendar = child.findViewById(R.id.calendarView);
        target.addView(child);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                ((TextView) AttachTo).setText(day + "-" + (month + 1) + "-" + year);
            }
        });

        dialog.show();
    }

    private long changeFormat(String oldDateString){
        final String OLD_FORMAT = "dd-MM-yyyy HH:mm:ss";
        long millisecond = 0;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(oldDateString+ " 12:0:0");
            millisecond = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return millisecond;
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

    public interface MasterSaldoListener{
        void onClickListTransaksiSaldo(TransaksiSaldo transaksiSaldo);
    }
}
