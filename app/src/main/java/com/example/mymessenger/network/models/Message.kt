package com.example.mymessenger.network.models

import java.io.Serializable
import java.util.Date

/**
 * Used for communicating with the server
 * */
data class Message(val type: MessageType,
                   val date: Date,
                   val userName: String?,
                   val text: String? = null): Serializable {
    companion object {
        private const val serialVersionUID = 6529685098267757690L
    }
}