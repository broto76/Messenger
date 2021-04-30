package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class UserDetailsResponse (
    @SerializedName("_id") val _id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("isVerified") val isVerified: String
)