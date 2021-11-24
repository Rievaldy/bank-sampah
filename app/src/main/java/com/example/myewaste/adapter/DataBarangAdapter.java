package com.example.myewaste.adapter;

import android.app.Dialog;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myewaste.DetailMasterBarang;
import com.example.myewaste.R;
import com.example.myewaste.model.MasterBarang;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DataBarangAdapter extends RecyclerView.Adapter<DataBarangAdapter.BarangViewHolder> {
    private static final String TAG = "DataBarangAdapter";

    private ArrayList<MasterBarang> datalist;
    private Context context;

    public DataBarangAdapter(Context context, ArrayList<MasterBarang> datalist){
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public BarangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_master_barang, parent, false);
        return new DataBarangAdapter.BarangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataBarangAdapter.BarangViewHolder holder, int position) {
        holder.tvNamaBarang.setText(datalist.get(position).getNama_master_barang());
        if (!datalist.get(position).getFoto_master_barang().equals("none")) {
            Target target = new Target(){
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Drawable drawableBitmap = new BitmapDrawable(context.getResources(), bitmap);
                    holder.ivFotoBarang.setBackground(drawableBitmap);
                    Log.d(TAG, "onBitmapLoaded: loaded" );
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    holder.ivFotoBarang.setBackgroundResource(R.drawable.ic_launcher_foreground);
                    Log.d(TAG, "onBitmapLoaded: failed" );
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    holder.ivFotoBarang.setBackgroundResource(R.drawable.progress);
                    Log.d(TAG, "onBitmapLoaded: prepare" );
                }
            };

            Picasso.get().load(datalist.get(position).getFoto_master_barang()).into(target);
            holder.ivFotoBarang.setTag(target);
        }
        holder.cvMasterBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailMasterBarang.class);
                intent.putExtra("barang",datalist.get(position));
                context.startActivity(intent);
            }
        });
        holder.cvMasterBarang.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialogUpdateDelete();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + datalist.size());
        return (datalist != null) ? datalist.size() : 0;
    }

    public class BarangViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNamaBarang;
        private ImageView ivFotoBarang;
        private CardView cvMasterBarang;
        public BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaBarang = (TextView) itemView.findViewById(R.id.tvNamaBarang);
            ivFotoBarang = (ImageView) itemView.findViewById(R.id.ivFotoBarang);
            cvMasterBarang = (CardView) itemView.findViewById(R.id.cvMasterBarang);
        }
    }


    public void showDialogUpdateDelete(){
        final BottomSheetDialog dialog = new BottomSheetDialog(context,R.style.BottomSheetDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_alert_dialog_barang);

        LinearLayout layoutBtnEditBarang = dialog.findViewById(R.id.btnEditBarang);
        LinearLayout layoutBtnHapusBarang = dialog.findViewById(R.id.btnHapusBarang);
        
        layoutBtnEditBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Edit Barang"  );
            }
        });
        
        layoutBtnHapusBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Hapus Barang");
            }
        });

        dialog.show();
    }
}
