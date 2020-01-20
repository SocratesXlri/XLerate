package com.xldock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.xldock.R;
import com.xldock.utils.PreferenceUtility;

public class Splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_splash);
        String isLoggedIn = PreferenceUtility.getInstance(this).getPrefIsLoggedIn();
        String userId = PreferenceUtility.getInstance(this).getUserId();
        if (!TextUtils.isEmpty(userId))
            Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();


        if ((!TextUtils.isEmpty(isLoggedIn)) && isLoggedIn.equalsIgnoreCase("true")) {
            startActivity(new Intent(this, MainActivity.class).addFlags
                    (Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("roll", PreferenceUtility.getInstance(this).getUserId()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

        } else {
            startActivity(new Intent(this, LoginActivity.class).addFlags
                    (Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        }

    }

}

