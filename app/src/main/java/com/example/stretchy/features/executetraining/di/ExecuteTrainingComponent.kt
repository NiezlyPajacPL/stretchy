package com.example.stretchy.features.executetraining.di

import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Provider

@ExecuteTrainingScope
@Component(
    dependencies = [ActivityComponent::class],
    modules = [ExecuteTrainingModule::class]
)
interface ExecuteTrainingComponent {
    @Component.Factory
    interface Factory {
        fun create(
            activityComponent: ActivityComponent,
            @BindsInstance trainingId: Long
        ): ExecuteTrainingComponent
    }

    fun viewModelProvider(): Provider<ExecuteTrainingViewModel>

    companion object {
        fun create(
            activityComponent: ActivityComponent,
            trainingId: Long
        ): ExecuteTrainingComponent {
            return DaggerExecuteTrainingComponent.factory().create(activityComponent, trainingId)
        }
    }
}