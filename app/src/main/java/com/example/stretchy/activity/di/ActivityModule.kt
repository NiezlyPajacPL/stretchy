package com.example.stretchy.activity.di

import androidx.activity.ComponentActivity
import com.example.stretchy.activity.di.scope.ActivityScope
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {

    //provided here so that the init function is called as soon as possible,
    // so that when sound needs to be played - there will be no delay
    @ActivityScope
    @Provides
    fun provideSpeaker(activity: ComponentActivity) =
        SoundPlayer(context = activity.baseContext)
}