package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class PostSendMessageResponse(
    @SerializedName("message") var message: String
)