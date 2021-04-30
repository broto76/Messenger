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
import kotlinx.android.synthetic.main.activity_login.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

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

        if (name.isEmpty()) {
            et_signup_name.error = getString(R.string.error_input_empty)
            return
        }
        if (phoneNumber.isEmpty()) {
            et_signup_number.error = getString(R.string.error_input_empty)
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
                    Toast.makeText(this@LoginActivity, "User already exists",
                        Toast.LENGTH_SHORT).show()

                    ll_signup.visibility = View.VISIBLE
                    ll_progress.visibility = View.GONE
                }
            }

        })

    }

    fun switchToSignup(view: View) {

        try {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ll_login.visibility = View.GONE
        ll_signup.visibility = View.VISIBLE
        ll_progress.visibility = View.GONE
        et_signup_name.setText("")
        et_signup_number.setText("")
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
}
