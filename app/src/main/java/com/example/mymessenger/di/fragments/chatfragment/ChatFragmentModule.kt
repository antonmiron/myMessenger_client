package com.example.mymessenger.di.fragments.chatfragment

import androidx.lifecycle.ViewModelProvider
import com.example.mymessenger.di.fragments.FragmentScope
import com.example.mymessenger.network.ConnectionManager
import com.example.mymessenger.network.MessageManager
import com.example.mymessenger.view.fragments.chatfragment.ChatFragment
import com.example.mymessenger.view.fragments.chatfragment.ChatViewModel
import dagger.Module
import dagger.Provides

@Module
interface ChatFragmentModule {
    companion object{
        @FragmentScope
        @Provides
        fun provideChatViewModel(fragment: ChatFragment, factory: ViewModelProvider.Factory): ChatViewModel{
            return ViewModelProvider(fragment, factory).get(ChatViewModel::class.java)
        }

        @FragmentScope
        @Provides
        fun provideChatFragmentFactory(connectionManager: ConnectionManager, messageManager: MessageManager): ViewModelProvider.Factory{
            return ChatViewModel.Factory(connectionManager, messageManager)
        }
    }
}