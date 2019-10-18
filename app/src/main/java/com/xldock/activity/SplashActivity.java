package com.xldock.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.xldock.R;
import com.xldock.WebServiceCalls;
import com.xldock.databinding.ActivityLoginBinding;
import com.xldock.model.Generic;
import com.xldock.utils.PreferenceUtility;

/**
 * Created by Honey Shah on 11-01-2018.
 */

public class SplashActivity extends AppCompatActivity{

    private static int SPLASH_TIME_OUT= 600;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, Splash_screen.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }



}
