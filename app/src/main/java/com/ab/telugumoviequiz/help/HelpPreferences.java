package com.ab.telugumoviequiz.help;

import android.content.Context;
import android.content.SharedPreferences;

public class HelpPreferences {

    private static HelpPreferences instance;
    private final String fileName = "HelpPreferences";

    public static final String TERMS_CONDITIONS = "termsconditions";
    public static final String REFERRAL_INFO = "referral_info";
    public static final String BEFORE_GAME_INFO = "before_game_info";
    public static final String WITHDRAW_INFO = "withdraw_info";


    private HelpPreferences() {
    }

    public static HelpPreferences getInstance() {
        if (instance == null) {
            instance = new HelpPreferences();
        }
        return instance;
    }

    public int readPreference(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        return sharedPref.getInt(key, 0);
    }

    public void writePreference(Context context, String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}