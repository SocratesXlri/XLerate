package com.xldock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.xldock.R;
import com.xldock.WebServiceCalls;
import com.xldock.databinding.ActivityAdminLoginBinding;
import com.xldock.model.Generic;
import com.xldock.utils.Constants;
import com.xldock.utils.PreferenceUtility;

/**
 * Created by Honey Shah on 11-01-2018.
 */

public class AdminLoginActivity extends AppCompatActivity{

    private ActivityAdminLoginBinding mBinder;
    WebServiceCalls mWebCalls;
    private String roll;
    private String pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_admin_login);

        mBinder.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
//                startActivity(new Intent(LoginActivity.this,MainActivity.class).addFlags
//                        (Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("roll",roll).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

            }
        });
    }


    private void doLogin(){
        String password=mBinder.etPassword.getText().toString();
        String userId=mBinder.etUserId.getText().toString();
        roll=userId;
        pwd= password;

        if(TextUtils.isEmpty(userId)){
            Toast.makeText(this,"Please enter user id",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
        }
        else{
            if(userId.equals(Constants.ADMIN_USERNAME ) && password.equals(Constants.ADMIN_PASSWORD)){
                PreferenceUtility.getInstance(this).setIsAdminLoggedIn(true);
                PreferenceUtility.getInstance(this).setPrefIsLoggedIn("true");
                PreferenceUtility.getInstance(this).setBaseUrl(Constants.LIVE_URL);
                startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
            else{
                Toast.makeText(this,R.string.provide_correct_credentials,Toast.LENGTH_SHORT).show();

            }
        }
    }
}
