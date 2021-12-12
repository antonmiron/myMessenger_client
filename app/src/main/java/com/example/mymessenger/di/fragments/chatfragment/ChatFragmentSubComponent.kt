package com.example.mymessenger.di.fragments.chatfragment

import com.example.mymessenger.di.fragments.FragmentScope
import com.example.mymessenger.view.fragments.chatfragment.ChatFragment
import dagger.BindsInstance
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [ChatFragmentModule::class])
interface ChatFragmentSubComponent {
    fun inject(chatFragment: ChatFragment)

    @Subcomponent.Factory
    interface Factory{
        fun create(@BindsInstance chatFragment: ChatFragment): ChatFragmentSubComponent
    }
}