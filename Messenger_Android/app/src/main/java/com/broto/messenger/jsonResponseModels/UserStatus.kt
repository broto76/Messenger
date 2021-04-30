package com.broto.messenger.jsonResponseModels

class UserStatus {
    companion object {
        val UNKNOWN = -1
        val REQUESTED = 1
        val ACCEPTED = 2
        val BLOCKED = 3
        val DENIED = 4
    }
}