package com.broto.messenger.retrofitServices

import com.broto.messenger.jsonResponseModels.LoginRequest
import com.broto.messenger.jsonResponseModels.LoginResponse
import com.broto.messenger.jsonResponseModels.SignupRequest
import com.broto.messenger.jsonResponseModels.SignupResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationService {

    @POST("auth/login")
    fun postLogin(
        @Body request: LoginRequest
    ) : Call<LoginResponse>

    @POST("auth/register")
    fun postSignup(
        @Body request: SignupRequest
    ) : Call<SignupResponse>
}