package com.broto.messenger.model

data class HomeFriendList (
    val name: String,
    val phoneNumber: String,
    val _id: String,
    var unreadMessages: Int
) {
    override fun toString(): String {
        return "Name: $name ** PhoneNumber: $phoneNumber ** Id: $_id ** Unread: $unreadMessages"
    }
}