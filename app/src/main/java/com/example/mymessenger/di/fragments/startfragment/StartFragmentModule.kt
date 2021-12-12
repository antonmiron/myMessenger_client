package com.example.mymessenger.di.fragments.startfragment

import androidx.lifecycle.ViewModelProvider
import com.example.mymessenger.di.fragments.FragmentScope
import com.example.mymessenger.network.ConnectionManager
import com.example.mymessenger.network.MessageManager
import com.example.mymessenger.view.fragments.startfragment.StartFragment
import com.example.mymessenger.view.fragments.startfragment.StartViewModel
import dagger.Module
import dagger.Provides

@Module
interface StartFragmentModule {
    companion object{
        @FragmentScope
        @Provides
        fun provideStartViewModel(fragment: StartFragment, factory: ViewModelProvider.Factory): StartViewModel{
            return ViewModelProvider(fragment, factory).get(StartViewModel::class.java)
        }

        @FragmentScope
        @Provides
        fun provideStartFragmentFactory(connectionManager: ConnectionManager, messageManager: MessageManager): ViewModelProvider.Factory{
            return StartViewModel.Factory(connectionManager, messageManager)
        }
    }
}