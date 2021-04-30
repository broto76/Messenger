package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class AllFriendsResponse (
    @SerializedName("list") val friendList: List<FriendDetails>
) {
    data class FriendDetails (
        @SerializedName("name") val name: String,
        @SerializedName("phoneNumber") val phoneNumber: String,
        @SerializedName("_id") val _id: String
    )
}