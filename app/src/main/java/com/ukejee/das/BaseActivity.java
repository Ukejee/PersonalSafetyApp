package com.ukejee.das;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author .: Ukeje Emeka
 * @email ..: ukejee3@gmail.com
 * @created : 6/13/20
 */
public class BaseActivity extends AppCompatActivity {

    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new Dialog(this);
    }

    public void showMessage(String title, String message){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", null);
            builder.create();
            builder.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showMessage(String title, String message, DialogInterface.OnClickListener listener) {
        cancelProgressDialog();
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", listener);
            builder.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void showProgressDialog(){
        if (mProgressDialog.isShowing()){
            cancelProgressDialog();
        }
        mProgressDialog.setContentView(R.layout.progress_bar);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
    }

    public void cancelProgressDialog(){
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
