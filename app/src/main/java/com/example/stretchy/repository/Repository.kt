package com.example.stretchy.repository

import com.example.stretchy.database.entity.metatraining.MetaTrainingEntity

interface Repository {
    //meta trainings
    suspend fun getMetaTrainings(): List<MetaTraining>
    suspend fun getMetaTrainingById(id: Long): MetaTraining
    suspend fun addMetaTraining(metaTraining: MetaTrainingEntity, trainingIds: List<Long>)
    suspend fun editMetaTraining(metaTraining: MetaTrainingEntity, newTrainingIds: List<Long>)
    suspend fun deleteMetaTraining(metaTrainingId: Long)
    suspend fun getTrainingIds(metaTrainingId: Long) : List<Long>
    //trainings
    suspend fun getTrainingsWithActivities(): List<TrainingWithActivity>
    suspend fun getTrainingWithActivitiesById(id: Long): TrainingWithActivity
    suspend fun addTrainingWithActivities(training: TrainingWithActivity)
    suspend fun editTrainingWithActivities(trainingId: Long, editedTraining: TrainingWithActivity)
    suspend fun deleteTrainingById(trainingId: Long)
    //break
    suspend fun getBreakByDuration(durationInSeconds: Long): Activity?
    suspend fun addBreak(durationInSeconds: Long)
}
