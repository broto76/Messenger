package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class PostTokenValidityRequest(
    @SerializedName("token") var token: String
)