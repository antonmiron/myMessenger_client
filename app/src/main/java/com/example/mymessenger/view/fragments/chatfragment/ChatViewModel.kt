package com.example.mymessenger.view.fragments.chatfragment


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mymessenger.network.ConnectionManager
import com.example.mymessenger.network.MessageManager
import com.example.mymessenger.viewmodel.BaseViewModel
import javax.inject.Inject

class ChatViewModel(
    val connectionManager: ConnectionManager,
    val messageManager: MessageManager
): BaseViewModel() {

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

    class Factory(
        private val connectionManager: ConnectionManager,
        private val messageManager: MessageManager
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChatViewModel(connectionManager, messageManager) as T
        }
    }
}