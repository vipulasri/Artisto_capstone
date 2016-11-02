package com.vipulasri.artisto.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by HP-HP on 01-10-2016.
 */
public class LocalStoreUtil {
    private static GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Gson gson = gsonBuilder.create();

    public static final String PREF_FILE_NAME = "com.vipulasri.artisto";
    public static final String PREF_FAVORITE_ARTS = "favorite_arts";

    public static void addToFavorites(final Context context, long artId) {
        SharedPreferences sp = null;
        try {
            sp = getSharedPreference(context);
            Set<String> set = sp.getStringSet(PREF_FAVORITE_ARTS, null);
            if (set == null) set = new HashSet<>();
            set.add(String.valueOf(artId));

            SharedPreferences.Editor editor = getSharedEditor(context);
            editor.clear();

            editor.putStringSet(PREF_FAVORITE_ARTS, set).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeFromFavorites(final Context context, long artId) {
        SharedPreferences sp = null;
        try {
            sp = getSharedPreference(context);
            Set<String> set = sp.getStringSet(PREF_FAVORITE_ARTS, null);
            if (set == null) set = new HashSet<>();
            set.remove(String.valueOf(artId));

            SharedPreferences.Editor editor = getSharedEditor(context);
            editor.clear();

            editor.putStringSet(PREF_FAVORITE_ARTS, set).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean hasInFavorites(final Context context, long artId) {
        SharedPreferences sp = null;
        boolean isfav = false;
        try {
            sp = getSharedPreference(context);
            Set<String> set = sp.getStringSet(PREF_FAVORITE_ARTS, null);
            if (set == null) set = new HashSet<>();
            isfav = set.contains(String.valueOf(artId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isfav;
    }

    public static List<String> getFavorites(final Context context) {

        List<String> favs = null;
        try {
            SharedPreferences pref = getSharedPreference(context);

            Set<String> set = pref.getStringSet(PREF_FAVORITE_ARTS, Collections.EMPTY_SET);
            favs = new ArrayList<>();

            if (set.isEmpty()) {
                favs = null;
            } else {
                for (String s : set) {
                    String user = gson.fromJson(s, String.class);
                    favs.add(user);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return favs;
    }

    public static void clearSession(Context context) {
        try {
            SharedPreferences.Editor editor = getSharedEditor(context);
            editor.clear();
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SharedPreferences.Editor getSharedEditor(Context context)
            throws Exception {
        if (context == null) {
            throw new Exception("Context null Exception");
        }
        return getSharedPreference(context).edit();
    }

    private static SharedPreferences getSharedPreference(Context context)
            throws Exception {
        if (context == null) {
            throw new Exception("Context null Exception");
        }
        return context.getSharedPreferences(PREF_FILE_NAME, 0);
    }

}
