package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class UserFromNumberResponse (
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserDetails,
    @SerializedName("status") val status: Int,
    @SerializedName("statusOwner") val statusOwner: String
) {
    data class UserDetails (
        @SerializedName("_id") val _id: String,
        @SerializedName("name") val name: String,
        @SerializedName("phoneNumber") val phoneNumber: String,
        @SerializedName("isVerified") val isVerified: String
    )
}