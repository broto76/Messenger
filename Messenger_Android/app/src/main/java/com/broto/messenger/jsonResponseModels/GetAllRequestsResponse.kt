package com.broto.messenger.jsonResponseModels

import com.google.gson.annotations.SerializedName

data class GetAllRequestsResponse(
    @SerializedName("returnList") val returnList: List<RequestedUserDetails>
) {
    data class RequestedUserDetails (
        @SerializedName("remoteUserId") val remoteUserId: String,
        @SerializedName("remoteUserName") val remoteUserName: String,
        @SerializedName("remoteUserPhoneNumber") val remoteUserPhoneNumber: String,
        @SerializedName("status") val status: Int
    )
}