package com.example.stretchy.features.createtraining.ui.di

import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.data.AutomaticBreakPreferences
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class CreateTrainingModule {
    @Provides
    fun provideCreateTrainingViewModel(
        repository: Repository,
        trainingId: Long,
        automaticBreakPreferences: AutomaticBreakPreferences,
        trainingType: TrainingType
    ) =
        CreateOrEditTrainingViewModel(
            repository,
            trainingId,
            automaticBreakPreferences,
            trainingType
        )
}