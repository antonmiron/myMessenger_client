package com.example.mymessenger.di.fragments.startfragment

import com.example.mymessenger.di.fragments.FragmentScope
import com.example.mymessenger.view.fragments.startfragment.StartFragment
import dagger.BindsInstance
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [StartFragmentModule::class])
interface StartFragmentSubComponent {
    fun inject(startFragment: StartFragment)

    @Subcomponent.Factory
    interface Factory{
        fun create(@BindsInstance fragment: StartFragment): StartFragmentSubComponent
    }
}