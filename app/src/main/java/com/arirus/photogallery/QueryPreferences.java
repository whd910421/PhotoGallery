package com.arirus.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by whd910421 on 16/10/18.
 */

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_ID = "lastResultID";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static String getStoredQUery(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY, null);
    }

    public static void setStoredQuery(Context context, String query)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SEARCH_QUERY,query).apply();
    }

    public static String getLastResultID(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID, null);
    }

    public static void setLastResultID(Context context, String ID)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_LAST_RESULT_ID,ID).apply();
    }

    public static boolean isAlarmOn(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean ID)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_IS_ALARM_ON, ID).apply();
    }
}
