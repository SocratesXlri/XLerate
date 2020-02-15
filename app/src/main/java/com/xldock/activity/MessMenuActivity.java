package com.xldock.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
import com.xldock.databinding.ActivityMessMenuBinding;
import com.xldock.utils.PreferenceUtility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MessMenuActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks, View.OnClickListener {
    private static final String TAG = "MessMenuActivity";
    GoogleAccountCredential mCredential;
    private ActivityMessMenuBinding mBinder;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS_READONLY};

    private List<List<Object>> mOutput;
    private int countButtonPressed = 0;

    private String range;
    //TEMPORARY FIX: to avoid crash of stack over flow due never ending loop
    private final int MAX_TRIES_FOR_SETUP_ACCOUNT = 5;
    private int triesDone = 0;

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_mess_menu);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait ...");

        getSupportActionBar().show();
        getSupportActionBar().setTitle(getString(R.string.xlri_mess_menu));
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

        setAppropriateRange();

        mBinder.spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getSelectedItem();
                if (selectedItem.equals("Old Hostel")) {
                    range = "Data Old Hostel!A2:G";
                    PreferenceUtility.getInstance(MessMenuActivity.this).setPrefMessMenu("Old Hostel");
                    getResultsFromApi();
                } else if (selectedItem.equals("New Hostel")) {
                    range = "Data New Hostel!A2:G";
                    PreferenceUtility.getInstance(MessMenuActivity.this).setPrefMessMenu("New Hostel");
                    getResultsFromApi();
                } else {
                    range = "Data GMP Hostel!A2:G";
                    PreferenceUtility.getInstance(MessMenuActivity.this).setPrefMessMenu("GMP Hostel");
                    getResultsFromApi();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setAppropriateRange() {
        String selectedItem = (String) mBinder.spinnerMenu.getSelectedItem();
        String selectedMenu = PreferenceUtility.getInstance(this).getPrefMessMenu();
        if (selectedMenu == null) {
            if (selectedItem.equals("Old Hostel")) {
                range = "Data Old Hostel!A2:G";
                getResultsFromApi();
            } else if ((selectedItem.equals("New Hostel"))) {
                range = "Data New Hostel!A2:G";
                // Initialize credentials and service object.
                getResultsFromApi();
            } else {
                range = "Data GMP Hostel!A2:G";
                // Initialize credentials and service object.
                getResultsFromApi();
            }
        } else {
            if (selectedMenu.equals("Old Hostel")) {
                mBinder.spinnerMenu.setSelection(0);
                range = "Data Old Hostel!A2:G";
                getResultsFromApi();
            } else if ((selectedItem.equals("New Hostel"))) {
                mBinder.spinnerMenu.setSelection(1);
                range = "Data New Hostel!A2:G";
                // Initialize credentials and service object.
                getResultsFromApi();
            } else {
                mBinder.spinnerMenu.setSelection(2);
                range = "Data GMP Hostel!A2:G";
                // Initialize credentials and service object.
                getResultsFromApi();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mess, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action:
                countButtonPressed = 0;
                setData();
                break;

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinder.buttonPrevious.getId()) {
            countButtonPressed--;
            setData();

        } else if (v.getId() == mBinder.buttonNext.getId()) {
            countButtonPressed++;
            setData();
        }
    }


    private void setData() {
        mBinder.buttonNext.setVisibility(View.VISIBLE);
        mBinder.buttonPrevious.setVisibility(View.VISIBLE);
        boolean isDataAvailable = false;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, countButtonPressed);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        // formattedDate have current date/time
        String formattedDateTime = df.format(cal.getTime());
        for (List<Object> list : mOutput) {
            if (list.get(0).equals(formattedDateTime)) {
                isDataAvailable = true;
                try {
                    mBinder.rlMain.setVisibility(View.VISIBLE);
                    mBinder.llDate.setVisibility(View.VISIBLE);
                    mBinder.llBreakfast.setVisibility(View.VISIBLE);
                    mBinder.llLunch.setVisibility(View.VISIBLE);
                    mBinder.llSnacks.setVisibility(View.VISIBLE);
                    mBinder.llDinner.setVisibility(View.VISIBLE);
                    mBinder.tvNoData.setVisibility(View.GONE);

                    mBinder.tvDate.setText(list.get(0).toString() + " (" + list.get(1).toString() + ")");
                    mBinder.tvBreakfast.setText(list.get(3).toString());
                    mBinder.tvLunch.setText(list.get(4).toString());
                    mBinder.tvSnacks.setText(list.get(5).toString());
                    mBinder.tvDinner.setText(list.get(6).toString());
                    mBinder.tvError.setVisibility(View.GONE);
                } catch (Exception e) {
                    mBinder.llDate.setVisibility(View.GONE);
                    mBinder.llBreakfast.setVisibility(View.GONE);
                    mBinder.llLunch.setVisibility(View.GONE);
                    mBinder.llSnacks.setVisibility(View.GONE);
                    mBinder.llDinner.setVisibility(View.GONE);
                    mBinder.tvNoData.setVisibility(View.GONE);
                    mBinder.rlMain.setVisibility(View.GONE);
                    mBinder.buttonNext.setVisibility(View.GONE);

                    mBinder.tvError.setVisibility(View.VISIBLE);
                    mBinder.tvError.setText("Mess menu not uploaded\nPlease contact Infracom");
                }
                break;
            }
        }
        if (!isDataAvailable) {
            mBinder.llDate.setVisibility(View.GONE);
            mBinder.llBreakfast.setVisibility(View.GONE);
            mBinder.llLunch.setVisibility(View.GONE);
            mBinder.llSnacks.setVisibility(View.GONE);
            mBinder.llDinner.setVisibility(View.GONE);
            mBinder.tvNoData.setVisibility(View.VISIBLE);
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
            //To stop never ending loop (TEMPORARY FIX)
            if (triesDone < MAX_TRIES_FOR_SETUP_ACCOUNT) {
                triesDone++;
                chooseAccount();
            }else{
                mBinder.tvError.setVisibility(View.VISIBLE);
                mBinder.tvError.setText(getString(R.string.msg_reinstall_the_app));
            }
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
            Log.d(TAG, "chooseAccount:AccountName check:" + accountName);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                Log.d(TAG, "chooseAccount:" + mCredential.getSelectedAccountName());

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
                    Log.d(TAG, "onActivityResult: " + accountName);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        Log.d(TAG, "onActivityResult: " + mCredential.getSelectedAccountName());
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
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
                MessMenuActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
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
                    .setApplicationName("Google Sheets API Android Demo")
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
                e.printStackTrace();
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
        private List<List<Object>> getDataFromApi() throws IOException {
            String spreadsheetId = getString(R.string.spreadsheet_id);
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
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
                mOutput = output;
                mBinder.rlMain.setVisibility(View.VISIBLE);
                setData();
//                Calendar c = Calendar.getInstance();
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                // formattedDate have current date/time
//                String formattedDateTime = df.format(c.getTime());
//                for (List<Object> list : mOutput) {
//                    if (list.get(0).equals(formattedDateTime)) {
//                        mBinder.tvDate2.setText(list.get(0).toString());
//                        mBinder.tvBreakfast2.setText(list.get(1).toString());
//                        mBinder.tvLunch2.setText(list.get(2).toString());
//                        mBinder.tvSnacks2.setText(list.get(3).toString());
//                        mBinder.tvDinner2.setText(list.get(4).toString());
//                    }
//                }
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
                            MessMenuActivity.REQUEST_AUTHORIZATION);
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
