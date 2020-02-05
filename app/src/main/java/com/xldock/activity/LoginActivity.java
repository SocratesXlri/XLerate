package com.xldock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.xldock.R;
import com.xldock.WebServiceCalls;
import com.xldock.databinding.ActivityLoginBinding;
import com.xldock.model.Generic;
import com.xldock.utils.Constants;
import com.xldock.utils.PreferenceUtility;

/**
 * Created by Honey Shah on 11-01-2018.
 */

public class LoginActivity extends AppCompatActivity implements WebServiceCalls.WebServiceResponse {

    private ActivityLoginBinding mBinder;
    WebServiceCalls mWebCalls;
    private String roll;
    private String pwd;
    private Boolean exit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mWebCalls = new WebServiceCalls(this);

        mBinder.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
//                startActivity(new Intent(LoginActivity.this,MainActivity.class).addFlags
//                        (Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("roll",roll).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

            }
        });
//        mBinder.tvAdminLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, AdminLoginActivity.class));
//            }
//        });

    }


    private void doLogin() {
        String password = mBinder.etPassword.getText().toString();
        String userId = mBinder.etUserId.getText().toString().toUpperCase();
        roll = userId;
        pwd = password;

        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Please enter user id", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else {
            if (mBinder.etUserId.getText().toString().equals(Constants.ADMIN_USERNAME)) {
                if (password.equals(Constants.ADMIN_PASSWORD)) {
                    PreferenceUtility.getInstance(this).setIsAdminLoggedIn(true);
                    PreferenceUtility.getInstance(this).setPrefIsLoggedIn("true");
                    String baseUrl = PreferenceUtility.getInstance(this).getBaseUrl();
                    if (TextUtils.isEmpty(baseUrl))
                        PreferenceUtility.getInstance(this).setBaseUrl(Constants.LIVE_URL);
                    startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else {
                    Toast.makeText(this, R.string.provide_correct_credentials, Toast.LENGTH_SHORT).show();
                }
            } else {
                mWebCalls.callLoginWebService(userId, password, this);
                mBinder.progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void volleyError() {
        mBinder.progressBar.setVisibility(View.GONE);
    }

    @Override
    public <T> void volleySuccessResponse(Generic<T> response, String from) {
        mBinder.progressBar.setVisibility(View.GONE);
        String loginResponse = (String) response.get();
        if (loginResponse.equalsIgnoreCase("true")) {

            startActivity(new Intent(this, MainActivity.class).addFlags
                    (Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("roll", roll).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

            PreferenceUtility.getInstance(this).setPrefIsLoggedIn("true");
            PreferenceUtility.getInstance(this).setUserId(roll);
            PreferenceUtility.getInstance(this).setPwd(pwd);
            PreferenceUtility.getInstance(this).setIsAdminLoggedIn(false);
            PreferenceUtility.getInstance(this).setBaseUrl(Constants.LIVE_URL);
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.provide_correct_credentials, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }


    @Override
    public void volleyDisplayGeneralizedMessage() {
        mBinder.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void volleyDisplaySpecifiedMessage(String message) {
        mBinder.progressBar.setVisibility(View.GONE);
    }
}
