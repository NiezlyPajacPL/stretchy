package com.example.stretchy.repository

import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.entity.ActivityEntity
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingEntity
import com.example.stretchy.database.entity.TrainingWithActivitiesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryImpl(private val db: AppDatabase) : Repository {
    override suspend fun addTrainingWithActivities(training: TrainingWithActivity) {
        withContext(Dispatchers.IO) {
            val tId = generateTrainingId()
            addTrainingWithActivitiesToDb(training.activities, tId)
            with(training) {
                db.trainingDao().add(TrainingEntity(tId, name, trainingType, finished))
            }
        }
    }

    override suspend fun editTrainingWithActivities(
        trainingId: Long,
        editedTraining: TrainingWithActivity
    ) {
        withContext(Dispatchers.IO) {
            deleteActivitiesFromTraining(
                getTrainingWithActivitiesById(trainingId).activities,
                trainingId
            )
            addTrainingWithActivitiesToDb(editedTraining.activities, trainingId)
            with(editedTraining) {
                db.trainingDao().update(TrainingEntity(trainingId, name, trainingType, finished))
            }
        }
    }

    override suspend fun deleteTrainingById(trainingId: Long) {
        withContext(Dispatchers.IO) {
            val training = getTrainingWithActivitiesById(trainingId)
            deleteActivitiesFromTraining(training.activities, trainingId)
            db.trainingDao().deleteById(trainingId = trainingId)
        }
    }

    override suspend fun deleteAllTrainings() {
        getTrainingsWithActivities().forEach { training ->
            with(training) {
                deleteActivitiesFromTraining(activities, id)
                db.trainingDao()
                    .delete(trainingEntity = TrainingEntity(id, name, trainingType, finished))
            }

        }
    }

    override suspend fun getTrainingsWithActivities(): List<TrainingWithActivity> =
        withContext(Dispatchers.IO) {
            db.trainingWithActivitiesDao().getTrainings().map { training ->
                training.mapToTrainingWithActivity()
            }
        }

    override suspend fun getTrainingWithActivitiesById(id: Long): TrainingWithActivity =
        withContext(Dispatchers.IO) {
            val training = db.trainingWithActivitiesDao().getTrainingsById(id)
            training.mapToTrainingWithActivity()
        }

    private fun TrainingWithActivitiesEntity.mapToTrainingWithActivity(): TrainingWithActivity {
        val trainingId = this.training.trainingId

        val activityOrderMap: MutableMap<Long, MutableList<Int>> =
            db.trainingWithActivitiesDao().getTrainingsActivitiesByTrainingId(trainingId)
                .groupBy { it.aId }
                .mapValues { (_, values) ->
                    mutableListOf<Int>().apply {
                        addAll(values.map { it.activityOrder })
                    }
                }.toMutableMap()
        val activitiesMapped: List<Activity> = this.activities
            .map {
                val activityOrder = activityOrderMap[it.activityId]!!.first()
                activityOrderMap[it.activityId]!!.removeAt(0)
                Activity(it.name, activityOrder, it.duration, it.activityType)
                    .apply {
                        this.activityId = it.activityId
                    }
            }
        with(this.training) {
            return TrainingWithActivity(
                name,
                trainingType,
                finished,
                activitiesMapped
            ).apply { id = trainingId }
        }

    }

    private fun generateActivityId(): Long =
        db.activityDao().getAll().maxOfOrNull { it.activityId + 1 } ?: 0

    private fun generateTrainingId(): Long =
        db.trainingDao().getAll().maxOfOrNull { it.trainingId + 1 } ?: 0

    private fun deleteActivitiesFromTraining(activities: List<Activity>, trainingId: Long) {
        activities.forEach { activity ->
            with(activity) {
                db.trainingWithActivitiesDao()
                    .delete(TrainingActivityEntity(trainingId, activityId, activityOrder!!))
            }
        }
    }

    private fun addTrainingWithActivitiesToDb(activities: List<Activity>, trainingId: Long) {
        activities.forEach { activity ->
            with(activity) {
                var aId = generateActivityId()
                val result = db.activityDao()
                    .add(ActivityEntity(aId, name, duration, activityType))
                if (result == -1L) {
                    aId = db.activityDao().getConflictActivity(name, duration).activityId
                }

                db.trainingWithActivitiesDao()
                    .insert(TrainingActivityEntity(trainingId, aId, activityOrder!!))
            }
        }
    }
}