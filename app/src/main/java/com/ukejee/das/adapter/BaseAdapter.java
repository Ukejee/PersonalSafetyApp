package com.ukejee.das.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.recyclerview.widget.RecyclerView;

import com.ukejee.das.R;

/**
 * @author .: Ukeje Emeka
 * @email ..: ukejee3@gmail.com
 * @created : 6/15/20
 */
abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends  RecyclerView.Adapter<VH>{

    private Context context;
    private Dialog  mProgressDialog;

    public BaseAdapter(Context context){
        this.context = context;
        mProgressDialog = new Dialog(context);
    }

    public void showProgressDialog() {
        if (mProgressDialog.isShowing()) {
            cancelProgressDialog();
        }
        mProgressDialog.setContentView(R.layout.progress_bar);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);


    }

    public void cancelProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showMessage(String title, String message) {
        cancelProgressDialog();
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", null);
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
