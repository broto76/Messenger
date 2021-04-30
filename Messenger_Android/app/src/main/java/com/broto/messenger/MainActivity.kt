package com.broto.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.broto.messenger.services.CoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startService()
        startPollingService()
    }

    private fun startService() {
        startService(Intent(this, CoreService::class.java))
    }

    private fun startPollingService() {
        CoroutineScope(Dispatchers.Unconfined).launch {
            while (CoreService.getInstance() == null) {
                delay(100)
            }
            CoreService.getInstance()?.checkTokenValid(object: CoreService.JobCompleteCallback {
                override fun onjobcompleted(status: Int) {
                    CoroutineScope(Dispatchers.Main).launch {
                        // Goto Main Thread
                        if (status == CoreService.JobCompleteCallback.RESPONSE_SUCCESS) {
                            // Verification Success
                            Log.d(TAG, "Token is valid. Goto HomeScreen")
                            startActivity(Intent(this@MainActivity,
                                HomeActivity::class.java))
                            finish()
                        } else {
                            // Verification failed
                            Log.d(TAG, "Token is invalid. Goto LoginScreen")
                            startActivity(Intent(this@MainActivity,
                                LoginActivity::class.java))
                            finish()
                        }
                    }
                }
            })
        }
    }
}
