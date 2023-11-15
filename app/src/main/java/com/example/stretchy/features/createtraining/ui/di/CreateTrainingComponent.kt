package com.example.stretchy.features.createtraining.ui.di

import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Provider

@CreateTrainingScope
@Component(
    dependencies = [ActivityComponent::class],
    modules = [CreateTrainingModule::class]
)
interface CreateTrainingComponent {
    @Component.Factory
    interface Factory {
        fun create(
            activityComponent: ActivityComponent,
            @BindsInstance trainingId: Long,
            @BindsInstance trainingType: TrainingType
        ): CreateTrainingComponent
    }

    fun viewModelProvider(): Provider<CreateOrEditTrainingViewModel>

    companion object {
        fun create(
            activityComponent: ActivityComponent,
            trainingId: Long,
            trainingType: TrainingType
        ): CreateTrainingComponent {
            return DaggerCreateTrainingComponent.factory()
                .create(activityComponent, trainingId, trainingType)
        }
    }
}