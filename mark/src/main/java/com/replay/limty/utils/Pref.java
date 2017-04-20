package com.replay.limty.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class Pref {

    private static SharedPreferences sharedPreferences;
    private static Pref prefsInstance;
    private static final String preferencesName = "preferences";

    private Pref(Context context){
        if (sharedPreferences == null) {
            sharedPreferences = context.getApplicationContext().getSharedPreferences(preferencesName,Context.MODE_PRIVATE);
        }
    }

    public static Pref with(Context context) {
        if (prefsInstance == null) {
            prefsInstance = new Pref(context);
        }
        return prefsInstance;
    }

    public String read(String what, String defaultString) {
        return sharedPreferences.getString(what, defaultString);
    }

    public void write(String where, String what) {
        sharedPreferences.edit().putString(where, what).apply();
    }

    public int read(String what, int defaultString) {
        return sharedPreferences.getInt(what, defaultString);
    }

    public void write(String where, int what) {
        sharedPreferences.edit().putInt(where, what).apply();
    }
}
