package com.example.clipsync

import android.content.Context
import android.preference.PreferenceManager

object Prefs {
    private const val KEY_BASE = "base"
    private const val KEY_TOKEN = "token"

    fun setServerBaseUrl(ctx: Context, v: String) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(KEY_BASE, v).apply()
    }
    fun serverBaseUrl(ctx: Context): String =
        PreferenceManager.getDefaultSharedPreferences(ctx).getString(KEY_BASE, "http://192.168.1.2:8765") ?: ""

    fun setToken(ctx: Context, v: String) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(KEY_TOKEN, v).apply()
    }
    fun token(ctx: Context): String =
        PreferenceManager.getDefaultSharedPreferences(ctx).getString(KEY_TOKEN, "") ?: ""
}
