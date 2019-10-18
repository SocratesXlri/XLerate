package com.xldock.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by pankaj on 5/19/17.
 */

public class PreferenceUtility {
    private static final String TAG = "PreferenceUtility";
    private final SharedPreferences mSharedPreferences;
    private static PreferenceUtility mPreferenceUtility;

    private static final String PREF_MESS_MENU = "com.xldock.mess_menu";
    private static final String PREF_IS_LOGGED_IN = "com.xldock.isLoggedIn";
    private static final String PREF_UserId = "com.xldock.UserId";
    private static final String PREF_Pwd = "com.xldock.Pwd";
    private PreferenceUtility(Context mContext) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static PreferenceUtility getInstance(Context mContext) {
        if (mPreferenceUtility == null)
            mPreferenceUtility = new PreferenceUtility(mContext);
        return mPreferenceUtility;
    }

    public void setPrefMessMenu(String value) {
        mSharedPreferences.edit().putString(PREF_MESS_MENU, value).apply();
    }
    public String setUserId(String value) {
        mSharedPreferences.edit().putString(PREF_UserId, value).apply();
        return value;
    }
    public String setPwd(String value) {
        mSharedPreferences.edit().putString(PREF_Pwd, value).apply();
        return value;
    }
    public String getUserId() {
        if (mSharedPreferences.contains(PREF_UserId)) {
            return mSharedPreferences.getString(PREF_UserId, null);
        }
        return null;
    }
    public String getPwd() {
        if (mSharedPreferences.contains(PREF_Pwd)) {
            return mSharedPreferences.getString(PREF_Pwd, null);
        }
        return null;
    }
    public String getPrefMessMenu() {
        if (mSharedPreferences.contains(PREF_MESS_MENU)) {
            return mSharedPreferences.getString(PREF_MESS_MENU, null);
        }
        return null;
    }

    public void setPrefIsLoggedIn(String value) {
        mSharedPreferences.edit().putString(PREF_IS_LOGGED_IN, value).apply();
    }

    public String getPrefIsLoggedIn() {
        if (mSharedPreferences.contains(PREF_IS_LOGGED_IN)) {
            return mSharedPreferences.getString(PREF_IS_LOGGED_IN,null);
        }
        return null;
    }

}
