package com.example.mymessenger.di.activity

import com.example.mymessenger.di.fragments.chatfragment.ChatFragmentSubComponent
import com.example.mymessenger.di.fragments.startfragment.StartFragmentSubComponent
import com.example.mymessenger.view.activity.MainActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface ActivitySubComponent {
    fun inject(mainActivity: MainActivity)

    fun startFragmentSubComponentFactory(): StartFragmentSubComponent.Factory
    fun chatFragmentSubComponentFactory(): ChatFragmentSubComponent.Factory

    @Subcomponent.Factory
    interface Factory{
        fun create():ActivitySubComponent
    }
}