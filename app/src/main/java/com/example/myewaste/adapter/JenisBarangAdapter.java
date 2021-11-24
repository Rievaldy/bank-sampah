package com.example.myewaste.adapter;

import android.content.Context;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myewaste.MasterJenisBarangActivity;
import com.example.myewaste.R;
import com.example.myewaste.model.MasterJenisBarang;

import java.util.List;

public class JenisBarangAdapter extends RecyclerView.Adapter<JenisBarangAdapter.ViewHolder>
{
    Context context;
    List<MasterJenisBarang> list;
    MasterJenisBarangActivity.JenisBarangListener listener;

    public JenisBarangAdapter(Context context, List<MasterJenisBarang> list, MasterJenisBarangActivity.JenisBarangListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_jenis_barang,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_jenisBarang.setText(list.get(position).getNama_master_jenis_barang());
        holder.tbl_Hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickDelete(list.get(position));
            }
        });
        holder.tbl_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickUpdate(list.get(position));
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickOpenDetailJenisBarang(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_jenisBarang;
        ImageButton tbl_Edit,tbl_Hapus;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_jenisBarang = itemView.findViewById(R.id.txt_jenisbarang);
            tbl_Edit = itemView.findViewById(R.id.tbl_edit);
            tbl_Hapus = itemView.findViewById(R.id.tbl_hapus);
            layout = itemView.findViewById(R.id.layout_list_jenis_barang);

        }
    }
}
