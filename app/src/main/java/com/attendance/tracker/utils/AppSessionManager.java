package com.attendance.tracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class AppSessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "PREFOFBRIDGE";
    private static final String USER_IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USERID = "userid";
    public static final String KEY_USERNAME = "UserName";
    public static final String KEY_PASSWORD = "UserPass";
    public static final String KEY_CATEGORY = "UserCategory";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_USERADDRESS = "userAddress";
    public static final String KEY_PROFILEIMAGEURL = "profileImageURL";
    public static final String KEY_SERVICE_OFF = "service";

    public static final String KEY_ENABLE_DISABLE_AUTO_SMS = "IsSMSEnable";

    public AppSessionManager(Context context) {
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    // Shared preference data store for login session
    public void createLoginSession(String userID, String userName, String userPass, String userCategory,String mobile,String address,String image) {
        editor.putBoolean(USER_IS_LOGIN, true);
        editor.putString(KEY_USERID, userID);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_PASSWORD, userPass);
        editor.putString(KEY_CATEGORY, userCategory);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_USERADDRESS,address);
        editor.putString(KEY_PROFILEIMAGEURL,image);

        editor.commit();
    }

    //Get Stored Session Data from SharedPreference
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_USERID, sharedPreferences.getString(KEY_USERID, null));
        userData.put(KEY_USERNAME, sharedPreferences.getString(KEY_USERNAME, null));
        userData.put(KEY_PASSWORD, sharedPreferences.getString(KEY_PASSWORD, null));
        userData.put(KEY_CATEGORY, sharedPreferences.getString(KEY_CATEGORY, null));
        userData.put(KEY_MOBILE, sharedPreferences.getString(KEY_MOBILE, null));
        userData.put(KEY_USERADDRESS, sharedPreferences.getString(KEY_USERADDRESS, null));
        userData.put(KEY_PROFILEIMAGEURL, sharedPreferences.getString(KEY_PROFILEIMAGEURL, null));

        return userData;
    }


    public void storeProfileImageUrl(String url) {
        editor.putString(KEY_PROFILEIMAGEURL, url);
        editor.commit();
    }

    //Remove data from sharedPreferences when user is logout
    public void logoutUser() {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(USER_IS_LOGIN, false).apply();
        sharedPreferences.edit().remove(KEY_USERID).apply();
        sharedPreferences.edit().remove(KEY_USERNAME).apply();
        sharedPreferences.edit().remove(KEY_PASSWORD).apply();
        sharedPreferences.edit().remove(KEY_CATEGORY).apply();
        sharedPreferences.edit().remove(KEY_MOBILE).apply();
        sharedPreferences.edit().remove(KEY_USERADDRESS).apply();
        sharedPreferences.edit().remove(KEY_PROFILEIMAGEURL).apply();
    }
    public boolean isBackgroundSMSSystemEnable() {
        return sharedPreferences.getBoolean(KEY_ENABLE_DISABLE_AUTO_SMS, false);
    }

    //Check is user Login or not
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(USER_IS_LOGIN, false);
    }
    public boolean isServiceOFF() {
        return sharedPreferences.getBoolean(KEY_SERVICE_OFF, false);
    }

    public void isServiceOn(Boolean  on) {
        editor.putBoolean(KEY_SERVICE_OFF, on);
        editor.commit();
    }

}
