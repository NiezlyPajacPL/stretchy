package com.example.stretchy.app.di

import android.app.Activity
import android.app.Application
import com.example.stretchy.app.StretchyApp
import com.example.stretchy.app.di.scope.ApplicationScope
import com.example.stretchy.features.createtraining.ui.data.AutomaticBreakPreferences
import com.example.stretchy.repository.Repository
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }

    fun inject(application: StretchyApp)

    fun repository(): Repository

    fun automaticBreakPreferences() : AutomaticBreakPreferences

    companion object {
        fun create(application: StretchyApp): AppComponent =
            DaggerAppComponent.factory().create(application).apply {
                inject(application)
            }

        val Activity.appComponent: AppComponent
            get() = (application as StretchyApp).getAppComponent()
    }
}