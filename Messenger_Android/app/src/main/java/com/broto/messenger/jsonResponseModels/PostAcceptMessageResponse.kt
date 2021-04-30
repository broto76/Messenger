package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class PostAcceptMessageResponse (
    @SerializedName("message") var message: String,
    @SerializedName("remoteUser_id") var remoteUser_id: String,
    @SerializedName("user_newStatus") var user_newStatus: String,
    @SerializedName("remoteUser_newStatus") var remoteUser_newStatus: String
)