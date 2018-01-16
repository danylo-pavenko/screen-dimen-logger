package com.dp.screenparamlogger.storage

import android.content.Context

private const val PREFS_NAME = "screen_logger_prefs"
class DataStorage(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLoggedScreen(screenName: String) {
        prefs.edit().putBoolean(screenName, true).apply()
    }

    fun hasThisScreen(screenName: String) : Boolean = prefs.contains(screenName)
}