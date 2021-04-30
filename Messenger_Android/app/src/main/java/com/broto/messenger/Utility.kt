package com.broto.messenger

import android.content.Context

class Utility {
    companion object {
        fun setPreference(key: String, value: String, context: Context) {
            val editor = context.getSharedPreferences(Constants.PREFS_LOGIN_PREF, Context.MODE_PRIVATE).edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getPreference(key: String, context: Context): String {
            val pref = context.getSharedPreferences(Constants.PREFS_LOGIN_PREF, Context.MODE_PRIVATE)
            return pref.getString(key, "") ?: ""
        }
    }
}