package com.example.mymessenger.screens.chatscreen


import com.example.mymessenger.App
import com.example.mymessenger.network.ConnectionManager
import com.example.mymessenger.network.MessageManager
import com.example.mymessenger.screens.BaseViewModel
import javax.inject.Inject

class ChatViewModel: BaseViewModel() {
    @Inject
    lateinit var connectionManager: ConnectionManager
    @Inject
    lateinit var messageManager: MessageManager

    init {
        App.dependencyInjectorComponent.inject(this)
    }

    /**
     * @return current user name
     *
     * p.s.
     * this is almost impossible, but if userName is null and we are here,
     * it is better to quit NPE and find the cause of the problem
     * than to fix a lot of other bugs related to this problem
     */
    fun getCurrentUserName() = connectionManager.connectionCredentialData?.userName?:throw NullPointerException()


    fun sendMessage(text: String){
        messageManager.sendTextMessage(text)
    }

    fun logout(){
        connectionManager.disconnect()
    }
}