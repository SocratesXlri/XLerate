package com.xldock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

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
        mBinder.buttonLaptopIssue.setOnClickListener(this);
        mBinder.buttonInfracomIssues.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button_laptop_issue:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.LAPTOP_ISSUE_FORM_URL).
                        putExtra(Constants.FROM,"Socrates Helpdesk"));
                break;

            case R.id.button_infracom__issues:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.INFRACOM_ISSUES_FORM_URL).
                        putExtra(Constants.FROM,getString(R.string.label_infracom_issues)));
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
