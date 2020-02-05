package com.xldock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.xldock.R;
import com.xldock.databinding.ActivityAdminBinding;
import com.xldock.databinding.ActivityMainBinding;
import com.xldock.utils.Constants;
import com.xldock.utils.PreferenceUtility;

/**
 * Created by honey on 10/11/17.
 */

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding mBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_admin);
        getSupportActionBar().show();
        getSupportActionBar().setTitle(getString(R.string.label_change_server));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
    }

    private void initUI() {
        String baseUrl = PreferenceUtility.getInstance(this).getBaseUrl();
        mBinder.etUrl.setText(baseUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    public void onSave(View v) {
        String baseUrl = mBinder.etUrl.getText().toString();
        boolean isValidUrl = Patterns.WEB_URL.matcher(baseUrl).matches();
        if (!isValidUrl || (!baseUrl.contains("http://") && !baseUrl.contains("https://"))) {
            Toast.makeText(this, R.string.valid_url, Toast.LENGTH_SHORT).show();
        } else {
            PreferenceUtility.getInstance(this).setBaseUrl(baseUrl);
            mBinder.etUrl.clearFocus();
            mBinder.etUrl.setFocusableInTouchMode(false);
            mBinder.etUrl.clearFocus();
            mBinder.buttonEdit.setVisibility(View.VISIBLE);
            mBinder.buttonSave.setVisibility(View.GONE);
            initUI();
        }
    }

    public void onEdit(View v) {
        mBinder.etUrl.setFocusableInTouchMode(true);
        mBinder.etUrl.requestFocus();
        mBinder.buttonEdit.setVisibility(View.GONE);
        mBinder.buttonSave.setVisibility(View.VISIBLE);
    }

}
