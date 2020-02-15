package com.xldock.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xldock.R;
import com.xldock.adapter.ResourcesAdapter;
import com.xldock.databinding.ActivityMessMenuBinding;
import com.xldock.databinding.ActivityResourcesBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ResourcesActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks, View.OnClickListener, ResourcesAdapter.OnClickListener {
    GoogleAccountCredential mCredential;
    private ActivityResourcesBinding mBinder;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int REQUEST_PERMISSION_MAKE_CALL = 1004;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS_READONLY};

    private ResourcesAdapter mAdapter;
    private int totalRows;
    private int pageNumber = 1;
    private boolean isFirstTime;
    private int totalPages = 0;
    private String mNumber;


    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_resources);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait ...");

        getSupportActionBar().show();
        getSupportActionBar().setTitle(getString(R.string.resources));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        initUI();


    }

    private void initUI() {
        mBinder.buttonNext.setOnClickListener(this);
        mBinder.buttonPrevious.setOnClickListener(this);
        mAdapter = new ResourcesAdapter(this);
        mBinder.rvContactNumber.setLayoutManager(new LinearLayoutManager(this));
        mBinder.rvContactNumber.setAdapter(mAdapter);
//        if (pageNumber == 1) {
//            mBinder.buttonPrevious.setVisibility(View.GONE);
        isFirstTime = true;
//        } else {
//            isFirstTime = false;
//            mBinder.buttonPrevious.setVisibility(View.VISIBLE);
//
//        }
        getResultsFromApi();
    }

    private void setData() {
        if (pageNumber == 1) {
            mBinder.buttonPrevious.setVisibility(View.GONE);
            isFirstTime = true;
            if (pageNumber == totalPages)
                mBinder.buttonNext.setVisibility(View.GONE);
            else
                mBinder.buttonNext.setVisibility(View.VISIBLE);
        } else {
            isFirstTime = false;
            mBinder.buttonPrevious.setVisibility(View.VISIBLE);
            if (pageNumber == totalPages)
                mBinder.buttonNext.setVisibility(View.GONE);
            else
                mBinder.buttonNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinder.buttonPrevious.getId()) {
            pageNumber--;
            setData();
            getResultsFromApi();

        } else if (v.getId() == mBinder.buttonNext.getId()) {
            pageNumber++;
            setData();
            getResultsFromApi();
        }
    }


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            mBinder.tvError.setVisibility(View.VISIBLE);
            mBinder.tvError.setText(getString(R.string.msg_no_connection));
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.access_contacts),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mBinder.tvError.setVisibility(View.VISIBLE);
                    mBinder.tvError.setText(
                            getString(R.string.install_google_play));
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;

            case REQUEST_PERMISSION_MAKE_CALL:
                if (resultCode == RESULT_OK) {

                }
                break;

        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
        if(requestCode==REQUEST_PERMISSION_MAKE_CALL)
            onClick(mNumber);
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
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

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                ResourcesActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onClick(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number));
        mNumber=number;
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            EasyPermissions.requestPermissions(
//                    this,
//                    getString(R.string.make_call),
//                    REQUEST_PERMISSION_MAKE_CALL,
//                    Manifest.permission.CALL_PHONE);
//            return;
//        }
        startActivity(callIntent);
    }


    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<List<Object>>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getString(R.string.app_name))
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<List<Object>> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         *
         * @return List of names and majors
         * @throws IOException
         */
        private List<List<Object>> getDataFromApi() {
            String spreadsheetId = getString(R.string.contact_spreadsheet_id);
            if (isFirstTime) {
                String range = "Contact Details!A4:A";
                ValueRange response = null;
                try {
                    response = this.mService.spreadsheets().values()
                            .get(spreadsheetId, range)
                            .execute();
                } catch (IOException e) {
                    mBinder.tvError.setVisibility(View.VISIBLE);
                    mBinder.tvError.setText("Please contact administrator");
                    e.printStackTrace();
                }
                List<List<Object>> values = response.getValues();
                totalRows = values.size();
                if (totalRows % 8 == 0)
                    totalPages = totalRows / 8;
                else
                    totalPages = totalRows / 8 + 1;
            }
            String startingRange = "A" + String.valueOf ((pageNumber - 1) * 8 + 4);
            String endingRange;
            if(pageNumber!=totalPages)
                endingRange = "B" +String.valueOf ((pageNumber) * 8 + 3);
            else
                endingRange = "B" +String.valueOf (totalRows+4);
            String range = "Contact Details!" + startingRange + ":" + endingRange;
            ValueRange response = null;
            try {
                response = this.mService.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .execute();
            } catch (IOException e) {
                mBinder.tvError.setVisibility(View.VISIBLE);
                mBinder.tvError.setText("Please contact administrator");
                e.printStackTrace();
            }
            List<List<Object>> values = response.getValues();
            if (values != null) {
                return values;
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            mBinder.tvError.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<List<Object>> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mBinder.tvError.setVisibility(View.VISIBLE);
                mBinder.tvError.setText(getString(R.string.no_result));
            } else {
                mAdapter.clearList();
                mAdapter.addAll(output);
                setData();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ResourcesActivity.REQUEST_AUTHORIZATION);
                } else {
                    mBinder.tvError.setVisibility(View.VISIBLE);
                    String message = getString(R.string.error_occurred)
                            + mLastError.getMessage();
                    mBinder.tvError.setText(message);
                }
            } else {
                mBinder.tvError.setVisibility(View.VISIBLE);
                mBinder.tvError.setText(getString(R.string.request_cancelled));
            }
        }
    }
}
