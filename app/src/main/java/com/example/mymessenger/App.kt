package com.example.mymessenger

import android.app.Application
import com.example.mymessenger.tools.di.DaggerDependencyInjectorComponent
import com.example.mymessenger.tools.di.DependencyInjectorComponent
import io.reactivex.rxjava3.plugins.RxJavaPlugins

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        //stay empty
        RxJavaPlugins.setErrorHandler{}

        instance = this
        dependencyInjectorComponent = DaggerDependencyInjectorComponent.create()
    }

    companion object{
        lateinit var instance: App
            private set

        lateinit var dependencyInjectorComponent: DependencyInjectorComponent
            private set
    }
}