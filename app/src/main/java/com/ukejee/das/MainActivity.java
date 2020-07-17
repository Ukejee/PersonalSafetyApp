package com.ukejee.das;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import com.ukejee.das.broadcastReceivers.PowerButtonReceiver;

public class MainActivity extends AppCompatActivity {

    private PowerButtonReceiver myReceiver = new PowerButtonReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUi(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(myReceiver, filter);
    }

    private void setUpUi(final Activity activity){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myReceiver);
    }

}
