package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class SignupResponse (
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: String,
    @SerializedName("details") val details: String
)