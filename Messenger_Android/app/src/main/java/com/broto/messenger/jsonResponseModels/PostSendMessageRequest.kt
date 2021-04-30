package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class PostSendMessageRequest(
    @SerializedName("id") var id: String
)