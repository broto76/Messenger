package com.broto.messenger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.broto.messenger.retrofitServices.AuthenticationService
import com.broto.messenger.retrofitServices.Utility
import com.broto.messenger.jsonResponseModels.LoginRequest
import com.broto.messenger.jsonResponseModels.LoginResponse
import com.broto.messenger.jsonResponseModels.SignupRequest
import com.broto.messenger.jsonResponseModels.SignupResponse
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    private val mAuth = FirebaseAuth.getInstance()
    private var mVerificationToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ll_login.visibility = View.VISIBLE
        ll_signup.visibility = View.GONE
        ll_progress.visibility = View.GONE
    }

    fun processSignIn(view: View) {
        val phoneNumber = et_login_number.text.toString()
        val password = et_login_password.text.toString()

        if (phoneNumber.isEmpty() || password.isEmpty()) {
            return
        }

        try {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ll_login.visibility = View.GONE
        ll_progress.visibility = View.VISIBLE

        val loginService = Utility.getRetrofitService().create(AuthenticationService::class.java)
        loginService.postLogin(LoginRequest(phoneNumber, password))
            .enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                Log.e(TAG, "Login Failed. Message: ${t?.message}")
                ll_login.visibility = View.VISIBLE
                ll_progress.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<LoginResponse>?,
                response: Response<LoginResponse>?
            ) {
                val body = response?.body()
                Log.d(TAG, "Login ResponseCode: ${response?.code()}")
                Log.d(TAG, "Login Message: ${body?.message}")

                /**
                 *
                 * Response: 200
                 * Response: LoginResponse(message=Login Success,
                 *
                 * token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
                 * eyJuYW1lIjoiU3dhcG5hIFNhaGEiLCJwaG9uZU51bWJlciI6Ijk0Nzc0NTg0MjciLCJpYXQiOjE2MTk1OTU2MzR9.
                 * 8qE8UJfURX85Ji1k_FkoRqEA3inS8LnMQmMLOuph93A,
                 *
                 * username=Swapna Saha,
                 *
                 * phoneNumber=9477458427,
                 *
                 * userId=608694c00d3cd54b047c901f)
                 *
                 */

                if ((response?.code()?:0) == 200) {
                    // Response Code OK
                    storeLoginToken(body?.token?:"", body?.userId?:"")
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                } else if ((response?.code()?:0) == 401) {
                    Log.d(TAG,"Authentication failed")
                    switchToLogin(view)
                } else {
                    Log.d(TAG,"Internal error")
                    switchToLogin(view)
                }
            }

        })

    }

    fun processSignUp(view: View) {
        val phoneNumber = et_signup_number.text.toString()
        val password = et_signup_password.text.toString()
        val name = et_signup_name.text.toString()

        if (phoneNumber.isEmpty()) {
            et_signup_number.error = getString(R.string.error_input_empty)
            return
        }
        if (name.isEmpty()) {
            et_signup_name.error = getString(R.string.error_input_empty)
            return
        }
        if (password.isEmpty()) {
            et_signup_password.error = getString(R.string.error_input_empty)
            return
        }

        try {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ll_signup.visibility = View.GONE
        ll_progress.visibility = View.VISIBLE
        et_signup_name.setText("")
        et_signup_number.setText("")
        et_signup_password.setText("")

        val authService = Utility.getRetrofitService().create(AuthenticationService::class.java)
        authService.postSignup(SignupRequest(name, phoneNumber, password))
            .enqueue(object: Callback<SignupResponse> {
            override fun onFailure(call: Call<SignupResponse>?, t: Throwable?) {
                Log.e(TAG, "Login Failed. Message: ${t?.message}")
                ll_signup.visibility = View.VISIBLE
                ll_progress.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<SignupResponse>?,
                response: Response<SignupResponse>?
            ) {

                Log.d(TAG, "Signup ResponseCode: ${response?.code()}")

                if ((response?.code()?:0) == 200) {
                    Log.d(TAG, "Signup Success")
                    Toast.makeText(this@LoginActivity, "Signup Success",
                        Toast.LENGTH_SHORT).show()
                    switchToLogin(view)
                } else if ((response?.code()?:0) == 422) {
                    Log.d(TAG, "User exists.")
                    CoroutineScope(Dispatchers.Main).launch {
                        ll_signup.visibility = View.VISIBLE
                        ll_progress.visibility = View.GONE

                        tv_signup_error.visibility = View.VISIBLE
                        tv_signup_error.text = getString(R.string.already_registered)

                        delay(Constants.DELAY_ERROR_MESSAGE)
                        tv_signup_error.text = ""
                        tv_signup_error.visibility = View.GONE
                    }
                }
            }

        })

    }

    fun switchToSignup(view: View) {

    if (currentFocus != null) {
        try {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

        ll_login.visibility = View.GONE
        ll_signup.visibility = View.VISIBLE
        ll_progress.visibility = View.GONE

        et_signup_number.setText("")
        et_signup_name.setText("")
        et_signup_password.setText("")

        et_login_number.setText("")
        et_login_password.setText("")
    }

    fun switchToLogin(view: View) {

        try {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ll_login.visibility = View.VISIBLE
        ll_signup.visibility = View.GONE
        ll_progress.visibility = View.GONE
        et_signup_name.setText("")
        et_signup_number.setText("")
        et_signup_password.setText("")
        et_login_number.setText("")
        et_login_password.setText("")
    }

    private fun storeLoginToken(token: String, userId: String) {
        Log.d(TAG,"Storing token: $token userid: $userId")

        com.broto.messenger.Utility.setPreference(Constants.SP_KEY_LOGIN_TOKEN,
            token, applicationContext)
        com.broto.messenger.Utility.setPreference(Constants.SP_KEY_LOGIN_USERID,
            userId, applicationContext)
    }

    fun processVerifyPhoneNumber(view: View) {
        val phoneNumber = "+91${et_signup_number.text}"
        if (phoneNumber.isEmpty()) {
            et_signup_number.error = getString(R.string.error_input_empty)
            return
        }

        if (currentFocus != null) {
            try {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        et_signup_number.isEnabled = false
        btn_signup_send_otp.visibility = View.GONE

        ll_signup.visibility = View.GONE
        ll_progress.visibility = View.VISIBLE

        // All Good. Send OTP
        Log.d(TAG, "Notify Firebase to send OTP to: $phoneNumber")
        val options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber)
            .setTimeout(20L, TimeUnit.SECONDS).setActivity(this).setCallbacks(
                object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        Log.d(TAG,"onVerificationCompleted Implicitly by Firebase")
                        showUserDetailFields()
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        Log.d(TAG,"onVerificationFailed: $p0")
                        verificationFailed()
                    }

                    override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        //Log.d(TAG,"onCodeSent: Code: $p0 Provider: $p1")

                        ll_signup.visibility = View.VISIBLE
                        ll_progress.visibility = View.GONE

                        et_otp.visibility = View.VISIBLE
                        btn_signup_verify_otp.visibility = View.VISIBLE

                        mVerificationToken = p0
                    }

                }
            ).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun processVerifyOTP(view: View) {

        if (currentFocus != null) {
            try {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val otp = et_otp.text.toString()
        if (otp.isEmpty()) {
            et_signup_number.error = getString(R.string.error_input_empty)
            return
        }

        btn_signup_verify_otp.visibility = View.GONE

        ll_signup.visibility = View.GONE
        ll_progress.visibility = View.VISIBLE

        val credential = PhoneAuthProvider.getCredential(mVerificationToken, otp)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this@LoginActivity) {
            if (it.isSuccessful) {
                Log.d(TAG, "VerifyOTP Success!")
                showUserDetailFields()
            } else {
                Log.d(TAG, "VerifyOTP Failed!")
                verificationFailed()
            }
        }
    }

    fun showUserDetailFields() {

        ll_signup.visibility = View.VISIBLE
        ll_progress.visibility = View.GONE

        // Hide OTP related Info
        et_otp.visibility = View.GONE
        btn_signup_verify_otp.visibility = View.GONE

        // Show other user fields
        et_signup_name.visibility = View.VISIBLE
        et_signup_password.visibility = View.VISIBLE
        btn_signup.visibility =View.VISIBLE
    }

    fun verificationFailed() {

        ll_signup.visibility = View.VISIBLE
        ll_progress.visibility = View.GONE

        et_otp.setText("")
        et_otp.visibility = View.GONE

        et_signup_number.setText("")
        et_signup_number.isEnabled = true

        btn_signup_send_otp.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            tv_signup_error.visibility = View.VISIBLE
            tv_signup_error.text = getString(R.string.number_verification_fail)

            delay(Constants.DELAY_ERROR_MESSAGE)
            tv_signup_error.text = ""
            tv_signup_error.visibility = View.GONE
        }
    }
}
