package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String,
    @SerializedName("username") val username: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("userId") val userId: String
)