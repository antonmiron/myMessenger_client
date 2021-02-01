package com.example.mymessenger.screens.startscreen

import com.example.mymessenger.App
import com.example.mymessenger.network.ConnectionManager
import com.example.mymessenger.network.MessageManager
import com.example.mymessenger.network.models.Message
import com.example.mymessenger.network.models.MessageType
import com.example.mymessenger.screens.BaseViewModel
import com.example.mymessenger.screens.startscreen.models.ConnectionCredentialData
import com.example.mymessenger.tools.customobserver.SimpleObserver
import javax.inject.Inject

class StartViewModel : BaseViewModel() {

    @Inject
    lateinit var connectionManager: ConnectionManager

    @Inject
    lateinit var messageManager: MessageManager

    /**
     * Use for catch the server login requests
     * @see createMessageManagerObserver
     * */
    private var messageManagerObserver: SimpleObserver<Message>? = null


    init {
        App.dependencyInjectorComponent.inject(this)
    }


    fun onClickConnect(connectionCredentialData: ConnectionCredentialData) {

        messageManagerObserver?.let{
            //remove old observer
            messageManager.newMessageObservable.deleteObserver(it)
        }

        //create new observer
        messageManagerObserver = createMessageManagerObserver(connectionCredentialData)
        messageManager.newMessageObservable.addObserver(messageManagerObserver)

        connectionManager.connect(connectionCredentialData)
    }

    /**
     * Send password to the server
     * */
    private fun sendPassword(connectionCredentialData: ConnectionCredentialData) {
        messageManager.sendPasswordMessage(connectionCredentialData.password)
    }

    /**
     * Send user name to the server
     * */
    private fun sendUserName(connectionCredentialData: ConnectionCredentialData) {
        messageManager.sendUsernameMessage(connectionCredentialData.userName)
    }

    /**
     * Function for create [messageManagerObserver].
     * Use for catch the server login requests
     * **/
    private fun createMessageManagerObserver(connectionCredentialData: ConnectionCredentialData): SimpleObserver<Message> {
        return object : SimpleObserver<Message>() {
            override fun onUpdate(value: Message?) {
                when (value?.type) {
                    MessageType.PASSWORD_REQUEST -> sendPassword(connectionCredentialData)
                    MessageType.NAME_REQUEST -> sendUserName(connectionCredentialData)
                    MessageType.NAME_NOT_ACCEPTED, MessageType.TEXT ->
                        messageManager.newMessageObservable.deleteObserver(this)
                    else -> { }
                }
            }
        }
    }

    override fun onCleared() {
        messageManagerObserver?.let{
            messageManager.newMessageObservable.deleteObserver(it)
        }
    }

}