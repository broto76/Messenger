package com.broto.messenger.model

data class FirebaseMessage (
    var messageData: String? = null,
    var sender: String? = null,
    var timestamp: Long? = null,
    var isRead: Boolean? = null
) {
    override fun toString(): String {
        return "Message: $messageData ** Sender: $sender ** TimeStamp: $timestamp ** isRead: $isRead"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FirebaseMessage) {
            return false
        }
        var item: FirebaseMessage = other
        return (this.messageData == item.messageData &&
                this.sender == item.sender &&
                this.timestamp == item.timestamp)
    }
}