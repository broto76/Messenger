package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class PostTokenValidityResponse(
    // This will either be valid/invalid
    @SerializedName("tokenStatus") var tokenStatus: String,
    @SerializedName("userId") var userId: String
) {
    companion object {
        const val TOKEN_STATUS_VALID = "valid"
        const val TOKEN_STATUS_INVALID = "invalid"
    }
}