package com.example.user.realsmarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by 3 on 2016-08-15.
 */
public class LoadingActivity extends Activity
{
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Handler mHandler = new Handler(){
            public void handleMessage(Message msg){
                finish();
            }
        };

        mHandler.sendEmptyMessageDelayed(0,3000);
    }


}