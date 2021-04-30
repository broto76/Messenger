package com.broto.messenger.model

data class FirebaseMessage (
    var MessageData: String? = null,
    var Sender: String? = null,
    var Timestamp: Long? = null
) {
    override fun toString(): String {
        return "Message: $MessageData ** Sender: $Sender ** TimeStamp: $Timestamp"
    }
}