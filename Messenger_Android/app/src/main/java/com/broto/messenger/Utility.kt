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

        fun getFirebaseMessageKeyName(user1: String, user2: String): String {
            return if (user1.compareTo(user2) > 0) {
                user2 + "_" + user1
            } else {
                user1 + "_" + user2
            }
        }
    }
}