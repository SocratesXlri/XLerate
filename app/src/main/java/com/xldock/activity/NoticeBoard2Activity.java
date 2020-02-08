
package com.xldock.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.xldock.R;
import com.xldock.databinding.ActivityGoogleFormBinding;
import com.xldock.databinding.ActivityNoticeBoard2Binding;
import com.xldock.utils.Constants;

/**
 * Created by Honey Shah on 18-11-2017.
 */

public class NoticeBoard2Activity extends AppCompatActivity {

    private ActivityNoticeBoard2Binding mBinder;
    boolean loadingFinished = true;
    boolean redirect = false;
    private static final String TAG = "NoticeBoard2Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_notice_board2);
        getSupportActionBar().show();
        getSupportActionBar().setTitle(getString(R.string.lbl_notice_board2));
        Log.e(TAG, getSupportActionBar().getTitle().toString());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
    }

    private void initUI() {

        if (!isDeviceOnline()) {
            mBinder.tvError.setVisibility(View.VISIBLE);
            return;
        }
        mBinder.tvError.setVisibility(View.GONE);
        WebSettings webSettings = mBinder.webview.getSettings();
        mBinder.webview.getSettings().setSupportZoom(true);
        mBinder.webview.getSettings().setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        String url = getIntent().getStringExtra(Constants.DATA);
        mBinder.webview.loadUrl(url);
        mBinder.webview.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.stopLoading();

                view.setVisibility(View.INVISIBLE);
                Toast.makeText(NoticeBoard2Activity.this, "Unable to reach AIS Server at the moment", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(NoticeBoard2Activity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                mBinder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mBinder.progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mBinder.webview.canGoBack()) {
            mBinder.webview.goBack();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mBinder.webview.canGoBack()) {
                mBinder.webview.goBack();
            } else
                super.onBackPressed();
        }
        return true;
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}

