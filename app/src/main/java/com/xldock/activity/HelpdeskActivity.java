package com.xldock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.xldock.R;
import com.xldock.databinding.ActivityHelpDeskBinding;
import com.xldock.utils.Constants;

/**
 * Created by Honey Shah on 18-11-2017.
 */

public class HelpdeskActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHelpDeskBinding mBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_help_desk);
        getSupportActionBar().show();
        getSupportActionBar().setTitle(getString(R.string.helpdesk));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUI();
    }

    private void initUI() {
        mBinder.buttonSpareLaptop.setOnClickListener(this);
        mBinder.buttonMacRegister.setOnClickListener(this);
        mBinder.buttonXlerateIssues.setOnClickListener(this);
        mBinder.buttonInfracomIssues.setOnClickListener(this);
        mBinder.buttonCharms.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button_spare_laptop:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.LAPTOP_ISSUE_FORM_URL).
                        putExtra(Constants.FROM,"Spare Laptop Request"));
                break;

            case R.id.button_mac_register:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.MAC_ID_REGISTERATION_URL).
                        putExtra(Constants.FROM,"Mac Id Registration"));
                break;

            case R.id.button_xlerate_issues:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.XLERATE_ISSUES_URL).
                        putExtra(Constants.FROM,"XLerate Issues"));
                break;

            case R.id.button_infracom__issues:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.INFRACOM_ISSUES_FORM_URL).
                        putExtra(Constants.FROM,getString(R.string.label_infracom_issues)));
                break;

            case R.id.button_charms:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.CHARMS_URL).
                        putExtra(Constants.FROM,getString(R.string.label_charms)));
                break;

        }
    }



    public void contact (View v)
    {
        startActivity(new Intent(this, ResourcesActivity.class));

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
