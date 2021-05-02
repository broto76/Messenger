package com.broto.messenger.retrofitServices

import com.broto.messenger.jsonResponseModels.*
import retrofit2.Call
import retrofit2.http.*

interface UserdataService {

    @GET("userDetails")
    fun getUserDetails(
        @Header("Authorization") token: String
    ): Call<UserDetailsResponse>

    @GET("allFriends")
    fun getAllFriends(
        @Header("Authorization") token: String
    ): Call<AllFriendsResponse>

    @GET("userDetailsFromNumber/{phoneNumber}")
    fun getUserFromNumber(
        @Header("Authorization") token: String,
        @Path("phoneNumber") phoneNumber: String
    ): Call<UserFromNumberResponse>

    @POST("sendMessageRequest")
    fun postSendMessageRequest(
        @Header("Authorization") token: String,
        @Body body: PostSendMessageRequest
    ): Call<PostSendMessageResponse>

    @POST("acceptMessageRequest")
    fun postAcceptMessageRequest(
        @Header("Authorization") token: String,
        @Body body: PostAcceptMessageRequest
    ): Call<PostAcceptMessageResponse>

    @GET("allRequests")
    fun getAllRequests(
        @Header("Authorization") token: String
    ): Call<GetAllRequestsResponse>
}