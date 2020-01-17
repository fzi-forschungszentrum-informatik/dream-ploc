package de.fzi.dream.ploc.utility;

import android.content.Context;
import android.content.SharedPreferences;

import static de.fzi.dream.ploc.utility.Constants.PREF_USER_FIRST_TIME;

public class SharedPreferencesAccess {

    public static void saveConstants(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREF_USER_FIRST_TIME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readConstants(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREF_USER_FIRST_TIME, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }
}
