package com.broto.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class TestActivity : AppCompatActivity() {

    private val TAG = "PhoneAuthentication"

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        sendOtp()
    }

    fun sendOtp() {

        val options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber("+16505553434")
            .setTimeout(20L, TimeUnit.SECONDS).setActivity(this).setCallbacks(
                object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        Log.d(TAG,"onVerificationCompleted: $p0")
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        Log.d(TAG,"onVerificationFailed: $p0")
                    }

                    override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        Log.d(TAG,"onCodeSent: Code: $p0 Provider: $p1")
                    }

                    override fun onCodeAutoRetrievalTimeOut(p0: String) {
                        val credential = PhoneAuthProvider.getCredential(p0, "121196")
                        mAuth.signInWithCredential(credential).addOnCompleteListener(this@TestActivity) {
                            if (it.isSuccessful) {
                                Log.d(TAG, "Signin success!")
                            } else {
                                Log.d(TAG, "Signin Failed!")
                            }
                        }
                    }

                }
            ).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}
