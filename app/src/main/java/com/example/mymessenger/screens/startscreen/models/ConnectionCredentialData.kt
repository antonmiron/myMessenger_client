package com.example.mymessenger.screens.startscreen.models

data class ConnectionCredentialData(
    val serverAddress: String,
    val serverPort: Int,
    val userName: String,
    val password: String
)