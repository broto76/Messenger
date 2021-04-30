package com.broto.messenger

class Constants {
    companion object {

        const val DELAY_SPLASH_SCREEN: Long = 1500
        const val DELAY_ERROR_MESSAGE: Long = 3000

        const val FIREBASE_DATABASE = "https://messenger-e83e4-default-rtdb.europe-west1.firebasedatabase.app/"

        const val PREFS_LOGIN_PREF = "com.broto.messenger.loginpreference"
        const val SP_KEY_LOGIN_TOKEN = "com.broto.messenger.loginJWTToken"
        const val SP_KEY_LOGIN_USERID = "com.broto.messenger.loginuserid"

        const val KEY_REMOTE_USERID = "com.broto.messenger.remoteuserid"
    }
}