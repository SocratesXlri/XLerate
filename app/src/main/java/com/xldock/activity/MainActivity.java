package com.xldock.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xldock.R;
import com.xldock.databinding.ActivityMainBinding;
import com.xldock.utils.Constants;
import com.xldock.utils.PreferenceUtility;

/**
 * Created by honey on 10/11/17.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainBinding mBinder;
    private String roll;
    private Boolean exit = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getSupportActionBar().show();
        getSupportActionBar().setTitle(getString(R.string.app_name));
        initUI();
        Intent intent = getIntent();
        roll= intent.getStringExtra("roll");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            startActivity(new Intent(this,LoginActivity.class).addFlags
                    (Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            PreferenceUtility.getInstance(this).setPrefIsLoggedIn("false");
            PreferenceUtility.getInstance(this).setUserId("");}
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        mBinder.buttonMessMenu.setOnClickListener(this);
        mBinder.buttonHelpdesk.setOnClickListener(this);
        mBinder.buttonAis.setOnClickListener(this);
        mBinder.buttonNotifications.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_mess_menu:
                startActivity(new Intent(this, MessMenuActivity.class));
                break;

            case R.id.button_helpdesk:
                startActivity(new Intent(this, HelpdeskActivity.class));
                break;

            case R.id.button_ais:
                Intent intent= new Intent(MainActivity.this, AisActivity.class);
                intent.putExtra("roll",roll);
                Toast.makeText(this,roll,Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;

            case R.id.button_notifications:
                startActivity(new Intent(this, NotificationsActivity.class));
                break;
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

    public void search (View v) {
        startActivity(new Intent(this, GoogleFormActivity.class).
                putExtra(Constants.DATA, Constants.SEARCH_URL).
                putExtra(Constants.FROM, "Student Search"));
    }
}
