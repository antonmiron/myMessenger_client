package com.example.mymessenger.view.app

import android.app.Application
import com.example.mymessenger.di.app.AppComponent
import com.example.mymessenger.di.app.DaggerAppComponent
import io.reactivex.rxjava3.plugins.RxJavaPlugins

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        //stay empty
        RxJavaPlugins.setErrorHandler{}

        instance = this
        appComponent = DaggerAppComponent.factory().create().apply { inject(this@App) }
    }

    companion object{
        lateinit var instance: App
            private set

        lateinit var appComponent: AppComponent
            private set
    }
}