package com.example.mymessenger.tools.di

import android.content.Context
import com.example.mymessenger.MainActivity
import com.example.mymessenger.screens.chatscreen.ChatViewModel
import com.example.mymessenger.screens.startscreen.StartViewModel
import com.example.mymessenger.services.NotificationReplyService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface DependencyInjectorComponent {
    //activity
    fun inject (activity: MainActivity)

    //services
    fun inject (service: NotificationReplyService)

    //view models
    fun inject(vm: StartViewModel)
    fun inject(vm: ChatViewModel)
}