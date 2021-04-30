package com.broto.messenger.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.broto.messenger.Constants
import com.broto.messenger.HomeActivity
import com.broto.messenger.LoginActivity
import com.broto.messenger.Utility
import com.broto.messenger.jsonResponseModels.PostTokenValidityRequest
import com.broto.messenger.jsonResponseModels.PostTokenValidityResponse
import com.broto.messenger.retrofitServices.AuthenticationService
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoreService : Service() {

    private val TAG = "CoreService"

    var mIsHomeActivityRunning = false

    companion object {

        private var mServiceInstance: CoreService? = null

        fun getInstance(): CoreService? {
            return mServiceInstance
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        mServiceInstance = this
    }

//    fun startJob() {
//        GlobalScope.launch {
//            var counter = 0
//            while(true) {
//                Log.d(TAG, "Counter: $counter")
//                counter++
//                delay(1000)
//            }
//        }
//    }

    fun checkTokenValid(callback: JobCompleteCallback) {
        val loginToken = Utility.getPreference(Constants.SP_KEY_LOGIN_TOKEN, applicationContext)
        val userId = Utility.getPreference(Constants.SP_KEY_LOGIN_USERID, applicationContext)

        Log.d(TAG, "Token: $loginToken")
        if (loginToken.isEmpty() || userId.isEmpty()) {
            Log.d(TAG, "Token/UserID not found. Verification failed")
            callback.onjobcompleted(1)
        } else {
            // Verify if the token is valid
            val authService = com.broto.messenger.retrofitServices.Utility.getRetrofitService()
                .create(AuthenticationService::class.java)
            authService.postTokenValidity(PostTokenValidityRequest(loginToken))
                .enqueue(object: Callback<PostTokenValidityResponse> {
                override fun onFailure(call: Call<PostTokenValidityResponse>?, t: Throwable?) {
                    Log.e(TAG, "Token verification Failed. Message: ${t?.message}")
                    callback.onjobcompleted(JobCompleteCallback.RESPONSE_FAILED)
                }

                override fun onResponse(
                    call: Call<PostTokenValidityResponse>?,
                    response: Response<PostTokenValidityResponse>?
                ) {
                    Log.d(TAG, "PostTokenValidity ResponseCode: ${response?.code()}")
                    Log.d(TAG, "PostTokenValidity response: ${response?.body()}")
                    if (response?.body()?.tokenStatus ==
                        PostTokenValidityResponse.TOKEN_STATUS_VALID) {
                        Log.d(TAG, "Token is valid.")
                        if (response?.body()?.userId == userId) {
                            Log.d(TAG, "UserID match success")
                            callback.onjobcompleted(JobCompleteCallback.RESPONSE_SUCCESS)
                        } else {
                            Log.d(TAG, "UserID match failed")
                            callback.onjobcompleted(JobCompleteCallback.RESPONSE_FAILED)
                        }
                    } else {
                        Log.d(TAG, "Token is invalid.")
                        callback.onjobcompleted(JobCompleteCallback.RESPONSE_FAILED)
                    }
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    interface JobCompleteCallback {
        /**
         * 0 - Success
         * 1 - Failed
         *
         */
        fun onjobcompleted(status: Int)

        companion object {
            const val RESPONSE_SUCCESS = 0
            const val RESPONSE_FAILED  = 1
        }
    }
}
