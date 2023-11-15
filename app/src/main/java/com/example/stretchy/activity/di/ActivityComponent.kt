package com.example.stretchy.activity.di

import androidx.activity.ComponentActivity
import com.example.stretchy.activity.MainActivity
import com.example.stretchy.activity.di.scope.ActivityScope
import com.example.stretchy.app.di.AppComponent
import com.example.stretchy.app.di.AppComponent.Companion.appComponent
import com.example.stretchy.features.createtraining.ui.data.AutomaticBreakPreferences
import com.example.stretchy.features.executetraining.sound.Speaker
import com.example.stretchy.repository.Repository
import dagger.BindsInstance
import dagger.Component


@ActivityScope
@Component(
    dependencies = [AppComponent::class],
    modules = [ActivityModule::class]
)
interface ActivityComponent {
    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent,
            @BindsInstance activity: ComponentActivity
        ): ActivityComponent
    }

    fun inject(activity: MainActivity)

    fun repository(): Repository
    fun activity(): ComponentActivity
    fun speaker() : Speaker
    fun automaticBreakPreferences() : AutomaticBreakPreferences

    companion object {
        fun create(activity: MainActivity): ActivityComponent =
            DaggerActivityComponent.factory().create(activity.appComponent, activity).apply {
                inject(activity)
            }
    }
}