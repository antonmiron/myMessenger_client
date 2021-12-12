package com.example.mymessenger.di.app

import com.example.mymessenger.view.app.App
import com.example.mymessenger.di.activity.ActivitySubComponent
import dagger.Component

@AppScope
@Component
interface AppComponent {
    fun inject(app: App)

    fun activitySubComponentFactory(): ActivitySubComponent.Factory

    @Component.Factory
    interface Factory{
        fun create(): AppComponent
    }
}