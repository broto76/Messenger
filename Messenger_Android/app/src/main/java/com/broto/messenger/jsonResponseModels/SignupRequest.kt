package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class SignupRequest (
    @SerializedName("name") val name: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("password") val password: String
)