package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class PostAcceptMessageRequest (
    @SerializedName("id") var id: String
)