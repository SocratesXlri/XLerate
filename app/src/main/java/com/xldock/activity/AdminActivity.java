package com.xldock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.xldock.R;
import com.xldock.databinding.ActivityAdminBinding;
import com.xldock.databinding.ActivityMainBinding;
import com.xldock.utils.Constants;
import com.xldock.utils.PreferenceUtility;

/**
 * Created by honey on 10/11/17.
 */

public class AdminActivity extends AppCompatActivity{

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
        String baseUrl=PreferenceUtility.getInstance(this).getBaseUrl();
        if(baseUrl.equals(Constants.DEBUG_URL))
            mBinder.radioDebug.setChecked(true);
        else
         mBinder.radioLive.setChecked(true);
        mBinder.radioDebug.setText(Constants.DEBUG_URL);
        mBinder.radioLive.setText(Constants.LIVE_URL);
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    public void onClickButtonMethod(View v){
        int selectedId = mBinder.radioGroup.getCheckedRadioButtonId();
        RadioButton serverRadioButton = (RadioButton) findViewById(selectedId);

        if(selectedId==-1){
            Toast.makeText(this,"Nothing selected", Toast.LENGTH_SHORT).show();
        }
        else{
            String selectedUrl=serverRadioButton.getText().toString();
            PreferenceUtility.getInstance(this).setBaseUrl(selectedUrl);
            //Toast.makeText(this,selectedUrl, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }

}
