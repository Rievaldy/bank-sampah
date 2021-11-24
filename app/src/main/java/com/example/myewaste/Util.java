package com.example.myewaste;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class Util {
    public static final String DEFAULT_KODE_BARANG = "J-0001";
    public static final String DEFAULT_KODE_TELLER = "T-0001";

    public enum MODE {
        MODE_SUPER_ADMIN,
        MODE_TELLER,
        MODE_NASABAH,
        MODE_REGISTRASI
    }

    public static String getRegisterCode(String registerNumber) {
        String code = "";
        String[] array = registerNumber.split("-");
        if(array.length > 0)
            code = registerNumber.split("-")[0];
        else
            code = "-";
        return code;
    }
    public static String getRegisterAs(String registerCode) {
        String as = "";
        if(registerCode.equals("SA") || registerCode.equals("sa"))
            as = "SuperAdmin";
        else if(registerCode.equals("T") || registerCode.equals("t"))
            as = "Teller";
        else if(registerCode.equals("N") || registerCode.equals("n"))
            as = "Nasabah";
        else
            as = "-";
        return as;
    }

    public static String increseNumber(String dataOnCode){
        String[] split = dataOnCode.split("-");
        int newVal = Integer.parseInt(split[1])+1;
        StringBuilder digit = new StringBuilder();
        for(int i = 0; i < split[1].length()- String.valueOf(newVal).length(); i++) {
            digit.append("0");
        }
        digit.append(newVal);
        return split[0].toUpperCase()+"-"+digit;
    }

    public static void loadImage(String imageSource, ImageView bindOn, Context context){
        if(!imageSource.equalsIgnoreCase("none") ){
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Drawable drawableBitmap = new BitmapDrawable(context.getResources(), bitmap);
                    bindOn.setImageDrawable(drawableBitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    bindOn.setImageResource(R.drawable.ic_launcher_foreground);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    bindOn.setImageResource(R.drawable.progress);
                }
            };

            Picasso.get().load(imageSource).into(target);
            bindOn.setTag(target);
            Picasso.get().setLoggingEnabled(true);
            Log.d("test", "loadImage: true running");
        }
    }

    public static void showMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
