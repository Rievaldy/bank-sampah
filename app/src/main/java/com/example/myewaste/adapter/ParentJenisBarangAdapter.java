package com.example.myewaste.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myewaste.MasterJenisBarangActivity;
import com.example.myewaste.R;
import com.example.myewaste.model.MasterBarang;
import com.example.myewaste.model.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.myewaste.Util.loadImage;

public class ParentJenisBarangAdapter extends RecyclerView.Adapter<ParentJenisBarangAdapter.MyViewHolder>  {

    private Context context;
    private ArrayList<MasterBarang> data;
    private MasterJenisBarangActivity.JenisBarangListener listener;

    public ParentJenisBarangAdapter(Context cont, ArrayList<MasterBarang> data, MasterJenisBarangActivity.JenisBarangListener listener){
        context= cont;
        this.data= data;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ParentJenisBarangAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_parent_barang ,parent, false);
        return new ParentJenisBarangAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentJenisBarangAdapter.MyViewHolder holder, int position) {
        //todo attaching layout with data and interaction
        holder.namaBarang.setText(data.get(position).getNama_master_barang());
        holder.btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnShow.setVisibility(View.GONE);
                holder.btnHide.setVisibility(View.VISIBLE);
                if(holder.rvListJenisBarang.getVisibility() == View.GONE){
                    holder.rvListJenisBarang.setVisibility(View.VISIBLE);
                    Log.d("TAG", "onClick: running");
                }else{
                    listener.requestAdapter(holder.rvListJenisBarang, data.get(position));
                    Log.d("TAG", "onClick: calling listener");
                }
            }
        });

        holder.btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnShow.setVisibility(View.VISIBLE);
                holder.btnHide.setVisibility(View.GONE);
                holder.rvListJenisBarang.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView namaBarang;
        ImageButton btnShow, btnHide;
        RecyclerView rvListJenisBarang;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            namaBarang = itemView.findViewById(R.id.tv_nama_parent_barang);
            btnShow = itemView.findViewById(R.id.btnShow);
            btnHide = itemView.findViewById(R.id.btnHide);
            rvListJenisBarang = itemView.findViewById(R.id.rv_nama_parent_barang);
            LinearLayoutManager mManager = new LinearLayoutManager(context);
            rvListJenisBarang.setLayoutManager(mManager);
        }
    }



}
