package com.xldock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.xldock.R;
import com.xldock.databinding.ActivityAisBinding;
import com.xldock.databinding.ActivityMainBinding;
import com.xldock.utils.Constants;
import com.xldock.utils.PreferenceUtility;

/**
 * Created by honey on 10/11/17.
 */

public class AisActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityAisBinding mBinder;
    private String roll;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_ais);
        getSupportActionBar().show();
        getSupportActionBar().setTitle(getString(R.string.label_ais));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
        Intent intent = getIntent();
        roll=intent.getStringExtra("roll") ;
    }

    private void initUI() {
        mBinder.buttonGrades.setOnClickListener(this);
        mBinder.buttonMySchedule.setOnClickListener(this);
        mBinder.buttonCourseSchedule.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_grades:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.GRADES_URL+roll+"&PWD=PreferenceUtility.getInstance(this).getPwd())").
                        putExtra(Constants.FROM, getString(R.string.label_grades)));

                break;
            case R.id.button_my_schedule:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.MY_SCHEDULE_URL+roll).
                        putExtra(Constants.FROM, getString(R.string.label_my_schedule)));
                break;

            case R.id.button_course_schedule:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.COURSE_SCHEDULE_URL).
                        putExtra(Constants.FROM, getString(R.string.label_course_schedule)));
                break;

        }
    }
}
