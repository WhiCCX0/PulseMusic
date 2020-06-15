package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.Preferences;

public class AppSettings {

    private static final String TAG = "AppSettings";

    public static void savePortraitGridSpanCount(@Nullable Context context, int count) {
        if (null != context) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.SPAN_COUNT_PORTRAIT_KEY, Context.MODE_PRIVATE).edit();
            editor.putInt(Preferences.SPAN_COUNT_PORTRAIT_KEY, count);
            editor.apply();
        } else Log.e(TAG, "Context is null cannot save data to shared preference");
    }

    public static int getPortraitGridSpanCount(@Nullable Context c) {
        if (null == c)
            return 2;
        return c.getSharedPreferences(Preferences.SPAN_COUNT_PORTRAIT_KEY, Context.MODE_PRIVATE).getInt(Preferences.SPAN_COUNT_PORTRAIT_KEY, 2);
    }

    public static void saveLandscapeGridSpanCount(@Nullable Context context, int count) {
        if (null != context) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.SPAN_COUNT_LANDSCAPE_KEY, Context.MODE_PRIVATE).edit();
            editor.putInt(Preferences.SPAN_COUNT_LANDSCAPE_KEY, count);
            editor.apply();
        } else Log.e(TAG, "Context is null cannot save data to shared preference");
    }

    public static int getLandscapeGridSpanCount(@Nullable Context c) {
        if (null == c)
            return 4;
        return c.getSharedPreferences(Preferences.SPAN_COUNT_LANDSCAPE_KEY, Context.MODE_PRIVATE).getInt(Preferences.SPAN_COUNT_LANDSCAPE_KEY, 4);
    }

    public static void enableDarkMode(Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.UI_THEME_DARK_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.UI_THEME_DARK_KEY, state);
        editor.apply();
    }

    public static void enableAutoTheme(Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.UI_MODE_AUTO_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.UI_MODE_AUTO_KEY, state);
        editor.apply();
    }

    public static boolean isDarkModeEnabled(Context context) {
        return context.getSharedPreferences(Preferences.UI_THEME_DARK_KEY, Context.MODE_PRIVATE).getBoolean(Preferences.UI_THEME_DARK_KEY, false);
    }

    public static boolean isAutoThemeEnabled(Context context) {
        return context.getSharedPreferences(Preferences.UI_MODE_AUTO_KEY, Context.MODE_PRIVATE).getBoolean(Preferences.UI_MODE_AUTO_KEY, false);
    }

    public static void saveSelectedDarkTheme(Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.DARK_THEME_CATEGORY_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.DARK_THEME_CATEGORY_KEY, id);
        editor.apply();
    }

    public static int getSelectedDarkTheme(Context context) {
        return context.getSharedPreferences(Preferences.DARK_THEME_CATEGORY_KEY, Context.MODE_PRIVATE)
                .getInt(Preferences.DARK_THEME_CATEGORY_KEY, Preferences.DARK_THEME_GRAY);
    }


    public static void saveSelectedAccentId(Context context, int accentId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.ACCENTS_COLOR_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.ACCENTS_COLOR_KEY, accentId);
        editor.apply();
    }

    public static int getSelectedAccentId(Context context) {
        return context.getSharedPreferences(Preferences.ACCENTS_COLOR_KEY, Context.MODE_PRIVATE)
                .getInt(Preferences.ACCENTS_COLOR_KEY, Preferences.ACCENT_EXODUS_FRUIT);
    }

    public static void saveAccentDesaturatedColor(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.ACCENTS_COLOR_DESATURATED_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.ACCENTS_COLOR_DESATURATED_KEY, enabled);
        editor.apply();
    }

    public static boolean getAccentDesaturatedColor(Context context) {
        return context.getSharedPreferences(Preferences.ACCENTS_COLOR_DESATURATED_KEY, Context.MODE_PRIVATE)
                .getBoolean(Preferences.ACCENTS_COLOR_DESATURATED_KEY, false);
    }


    public static void saveLibraryFragmentSortOrder(Context context, int sortOrder) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.SORT_ORDER_PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.SORT_ORDER_LIBRARY_KEY, sortOrder);
        editor.apply();
    }

    public static void saveAlbumsFragmentSortOrder(Context context, int sortOrder) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.SORT_ORDER_PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.SORT_ORDER_ALBUMS_KEY, sortOrder);
        editor.apply();
    }

    public static void saveArtistsFragmentSortOrder(Context context, int sortOrder) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.SORT_ORDER_PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.SORT_ORDER_ARTIST_KEY, sortOrder);
        editor.apply();
    }

    public static int getLibraryFragmentSortOrder(Context context) {
        return context.getSharedPreferences(Preferences.SORT_ORDER_PREFS_KEY, Context.MODE_PRIVATE)
                .getInt(Preferences.SORT_ORDER_LIBRARY_KEY, Preferences.SORT_ORDER_ASC);
    }

    public static int getAlbumsFragmentSortOrder(Context context) {
        return context.getSharedPreferences(Preferences.SORT_ORDER_PREFS_KEY, Context.MODE_PRIVATE)
                .getInt(Preferences.SORT_ORDER_ALBUMS_KEY, Preferences.SORT_ORDER_ASC);
    }

    public static int getArtistFragmentSortOrder(Context context) {
        return context.getSharedPreferences(Preferences.SORT_ORDER_PREFS_KEY, Context.MODE_PRIVATE)
                .getInt(Preferences.SORT_ORDER_ARTIST_KEY, Preferences.SORT_ORDER_ASC);
    }

    public static void setAlbumCardOverlayEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.ALBUM_CARD_OVERLAY_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.ALBUM_CARD_OVERLAY_KEY, enabled);
        editor.apply();
    }

    public static boolean isAlbumCardOverlayEnabled(Context context) {
        return context.getSharedPreferences(Preferences.ALBUM_CARD_OVERLAY_KEY, Context.MODE_PRIVATE)
                .getBoolean(Preferences.ALBUM_CARD_OVERLAY_KEY, false);
    }

    public static void setNowPlayingAlbumCardStyle(Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_KEY, id);
        editor.apply();
    }

    public static void setNowPlayingAlbumCardOverlayEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.NOW_PLAYING_ALBUM_CARD_OVERLAY_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.NOW_PLAYING_ALBUM_CARD_OVERLAY_KEY, enabled);
        editor.apply();
    }

    public static int getNowPlayingAlbumCardStyle(Context context) {
        return context.getSharedPreferences(Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_KEY, Context.MODE_PRIVATE)
                .getInt(Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_KEY, Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_SQUARE);
    }

    public static boolean isNowPlayingAlbumCardOverlayEnabled(Context context) {
        return context.getSharedPreferences(Preferences.NOW_PLAYING_ALBUM_CARD_OVERLAY_KEY, Context.MODE_PRIVATE)
                .getBoolean(Preferences.NOW_PLAYING_ALBUM_CARD_OVERLAY_KEY, false);
    }
}
