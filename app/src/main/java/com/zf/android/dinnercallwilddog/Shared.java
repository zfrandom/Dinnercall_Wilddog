package com.zf.android.dinnercallwilddog;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zifeifeng on 7/1/17.
 */

public class Shared {
    private static final String CONFIGS = "configs";
    public static final String DEFINE_EMAIL = "defind_username";
    public static final String DEFINE_PASSWORD = "defind_password";
    public static final String DEFINE_IS_REMEMBER = "defind_is_remember";
    private static SharedPreferences msp;
    private static SharedPreferences mPreferences=null;
    public static final String DEFINE_TIME = "define time";
    public static final String DEFINE_RINGTONE = "define ringtone";
    private static final String SHARED_PREFERNECE_NAME="name";
    private static final String TAG="SharedPreference";

    private static SharedPreferences getSharePreferenceCheck(Context context){
        if(mPreferences == null) {
            mPreferences = context.getSharedPreferences(SHARED_PREFERNECE_NAME, 0);
            Log.e(TAG, "it creates again");
        }
        return mPreferences;
    }


    public static Long getLong(Context context, String key, Long idk){
        SharedPreferences P = getSharePreferenceCheck(context);
        return P.getLong(key, idk);
    }


    public static void putLong(Context context, String key, Long value){
        SharedPreferences P = getSharePreferenceCheck(context);
        P.edit().putLong(key, value).commit();
    }

    private static SharedPreferences getSharedPreferencesCheck(Context context){
        if(msp==null){
            msp = context.getSharedPreferences(CONFIGS, Context.MODE_PRIVATE);
        }
        return msp;
    }


    public static String getString(Context context,String key){
        SharedPreferences sp = getSharedPreferencesCheck(context);
        return sp.getString(key, null);
    }

    public static void putString(Context context,String key,String value){
        SharedPreferences sp = getSharedPreferencesCheck(context);
        sp.edit().putString(key, value).commit();
    }

    public static boolean getBoolean(Context context,String key){
        SharedPreferences sp = getSharedPreferencesCheck(context);
        return sp.getBoolean(key, false);
    }

    public static void putBoolean(Context context,String key, Boolean value){
        SharedPreferences sp = getSharedPreferencesCheck(context);
        sp.edit().putBoolean(key, value).commit();
    }
    //store int
    public static void putInt(Context context, String key, int value){
        SharedPreferences sp=getSharedPreferencesCheck(context);
        sp.edit().putInt(key, value).commit();
    }

    //get int
    public static int getInt(Context context, String key){
        SharedPreferences sp = getSharedPreferencesCheck(context);
        return sp.getInt(key, -1);
    }

    public static void deleteSpDate(Context context){
        SharedPreferences sp = getSharedPreferencesCheck(context);
        sp.edit().clear().commit();
        Toast.makeText(context, "ɾ���ɹ�", 0).show();
    }
}
