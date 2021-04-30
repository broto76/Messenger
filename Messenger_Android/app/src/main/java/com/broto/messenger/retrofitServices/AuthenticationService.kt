package com.broto.messenger.retrofitServices

import com.broto.messenger.jsonResponseModels.*
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

    @POST("auth/tokenValidity")
    fun postTokenValidity(
        @Body request: PostTokenValidityRequest
    ) : Call<PostTokenValidityResponse>
}