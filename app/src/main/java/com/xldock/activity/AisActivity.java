package com.xldock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
    private String baseUrl;
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
        baseUrl=PreferenceUtility.getInstance(this).getBaseUrl();

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
                        putExtra(Constants.DATA, Constants.getGradesUrl(baseUrl)+roll+"&PWD=PreferenceUtility.getInstance(this).getPwd())").
                        putExtra(Constants.FROM, getString(R.string.label_grades)));

                break;
            case R.id.button_my_schedule:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.getMyScheduleUrl(baseUrl)+roll).
                        putExtra(Constants.FROM, getString(R.string.label_my_schedule)));
                break;

            case R.id.button_course_schedule:
                startActivity(new Intent(this, GoogleFormActivity.class).
                        putExtra(Constants.DATA, Constants.getCourseScheduleUrl(baseUrl)).
                        putExtra(Constants.FROM, getString(R.string.label_course_schedule)));
                break;

        }
    }
}
