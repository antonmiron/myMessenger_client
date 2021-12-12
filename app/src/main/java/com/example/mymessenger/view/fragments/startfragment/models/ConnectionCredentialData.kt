package com.example.mymessenger.view.fragments.startfragment.models

data class ConnectionCredentialData(
    val serverAddress: String,
    val serverPort: Int,
    val userName: String,
    val password: String
)