package com.example.stretchy.features.traininglist.di

import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.datatransport.DataExporterImpl
import com.example.stretchy.features.datatransport.DataImporterImpl
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class TrainingListModule {
    @Provides
    fun provideTrainingListViewModel(
        repository: Repository,
        dataImporterImpl: DataImporterImpl,
        dataExporterImpl: DataExporterImpl,
        trainingType: TrainingType
    ) =
        TrainingListViewModel(repository, dataImporterImpl, dataExporterImpl,trainingType)
}