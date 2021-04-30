package com.broto.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.Main).launch {
            delay(Constants.DELAY_SPLASH_SCREEN)
            checkLoginCredentials()
        }
    }

    private fun checkLoginCredentials() {
        val loginToken = Utility.getPreference(Constants.SP_KEY_LOGIN_TOKEN, applicationContext)
        val userId = Utility.getPreference(Constants.SP_KEY_LOGIN_USERID, applicationContext)

        Log.d(TAG, "Token: $loginToken")
        if (loginToken.isEmpty() || userId.isEmpty()) {
            // Goto login page
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // Goto homepage
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
