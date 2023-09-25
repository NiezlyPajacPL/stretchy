package com.example.stretchy.repository

import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.database.entity.ActivityEntity
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingEntity
import com.example.stretchy.database.entity.TrainingWithActivitiesEntity
import com.example.stretchy.database.entity.metatraining.MetaTrainingEntity
import com.example.stretchy.database.entity.metatraining.MetaTrainingWithTrainingsCrossRef
import com.example.stretchy.database.entity.metatraining.MetaTrainingWithTrainingsEntity
import com.example.stretchy.features.traininglist.ui.data.Training
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

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

    override suspend fun addMetaTraining(
        metaTraining: MetaTrainingEntity,
        trainingIds: List<Long>
    ) {
        withContext(Dispatchers.IO) {
            val metaTrainingId = generateMetaTrainingId()
            addMetaTrainingToDb(metaTrainingId, trainingIds)
            with(metaTraining) {
                db.metaTrainingDao().add(
                    MetaTrainingEntity(
                        metaTrainingId = metaTrainingId,
                        name = name,
                        lastExecuted = lastExecuted
                    )
                )
            }
        }
    }

    override suspend fun editMetaTraining(
        metaTraining: MetaTrainingEntity,
        newTrainingIds: List<Long>
    ) {
        withContext(Dispatchers.IO) {
            val currentTrainings = getTrainingIds(metaTraining.metaTrainingId)
            deleteTrainingsFromMetaTraining(
                metaTraining.metaTrainingId,
                currentTrainings
            )
            addMetaTrainingToDb(metaTraining.metaTrainingId, newTrainingIds)
            with(metaTraining) {
                db.metaTrainingDao().update(
                    MetaTrainingEntity(
                        metaTrainingId = metaTrainingId,
                        name = name,
                        lastExecuted = lastExecuted
                    )
                )
            }
        }
    }

    override suspend fun deleteMetaTraining(metaTrainingId: Long) {
        withContext(Dispatchers.IO) {
            val trainingIds = getTrainingIds(metaTrainingId)
            deleteTrainingsFromMetaTraining(metaTrainingId, trainingIds)
            db.metaTrainingDao().deleteById(metaTrainingId)
        }
    }

    override suspend fun getTrainingIds(metaTrainingId: Long): List<Long> =
        db.metaTrainingWithTrainingsDao().getTrainingIds(metaTrainingId).map { it.trainingId }


    override suspend fun getBreakByDuration(durationInSeconds: Long): Activity? {
        TODO("Not yet implemented")
    }

    override suspend fun addBreak(durationInSeconds: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getMetaTrainings(): List<MetaTraining> =
        withContext(Dispatchers.IO) {
            db.metaTrainingWithTrainingsDao().getMetaTrainings()
                .map { metaTrainingEntity ->
                    metaTrainingEntity.toMetaTraining()
                }
        }

    override suspend fun getMetaTrainingById(id: Long): MetaTraining =
        withContext(Dispatchers.IO) {
            db.metaTrainingWithTrainingsDao().getMetaTraining(id).toMetaTraining()
        }

    override suspend fun getTrainingsWithActivities(): List<TrainingWithActivity> =
        withContext(Dispatchers.IO) {
            db.trainingWithActivitiesDao().getTrainings().map { activity ->
                map(activity)
            }
        }

    override suspend fun getTrainingWithActivitiesById(id: Long): TrainingWithActivity =
        withContext(Dispatchers.IO) {
            val activity = db.trainingWithActivitiesDao().getTrainingsById(id)
            map(activity)
        }

    private suspend fun MetaTrainingWithTrainingsEntity.toMetaTraining(): MetaTraining {
        fun TrainingType.toType() =
            when (this) {
                TrainingType.BODYWEIGHT -> Training.Type.BODY_WEIGHT
                TrainingType.STRETCH -> Training.Type.STRETCHING
            }

        val trainingsMapped: List<Training> = this.trainings.map { trainingEntity ->
            with(trainingEntity) {
                var trainingDuration = 0
                val activities = getTrainingWithActivitiesById(this.trainingId)
                activities.activities.forEach { trainingDuration += it.duration }
                val exercisesCount =
                    activities.activities.filter { it.activityType != ActivityType.BREAK }.size
                Training(
                    trainingId.toString(),
                    name,
                    exercisesCount,
                    trainingDuration,
                    trainingEntity.trainingType.toType()
                )
            }
        }
        with(this.metaTrainingEntity) {
            return MetaTraining(
                name = name,
                trainings = trainingsMapped,
                lastExecuted = LocalDateTime.now() //todo // lastExecuted.toString()
            )
        }
    }

    private fun map(training: TrainingWithActivitiesEntity): TrainingWithActivity {
        val activitiesMapped: List<Activity> = training.activities
            .map {
                Activity(it.name, it.duration, it.activityType).apply {
                    this.activityId = it.activityId
                }
            }

        with(training.training) {
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

    private fun generateMetaTrainingId(): Long =
        db.metaTrainingDao().getAll().maxOfOrNull { it.metaTrainingId + 1 } ?: 0

    private fun deleteActivitiesFromTraining(activities: List<Activity>, trainingId: Long) {
        activities.forEach { activity ->
            with(activity) {
                db.activityDao().delete(ActivityEntity(activityId, name, duration, activityType))
                db.trainingWithActivitiesDao()
                    .delete(TrainingActivityEntity(trainingId, activityId))
            }
        }
    }

    private fun deleteTrainingsFromMetaTraining(
        metaTrainingId: Long,
        trainingIds: List<Long>
    ) {
        trainingIds.forEach { id ->
            db.metaTrainingWithTrainingsDao()
                .delete(MetaTrainingWithTrainingsCrossRef(metaTrainingId, id))
        }
    }

    private fun addTrainingWithActivitiesToDb(activities: List<Activity>, trainingId: Long) {
        activities.forEach { activity ->
            with(activity) {
                val aId = generateActivityId()
                db.activityDao().add(ActivityEntity(aId, name, duration, activityType))
                db.trainingWithActivitiesDao().insert(TrainingActivityEntity(trainingId, aId))
            }
        }
    }

    private fun addMetaTrainingToDb(
        metaTrainingId: Long,
        trainingIds: List<Long>
    ) {
        trainingIds.forEach { trainingId ->
            db.metaTrainingWithTrainingsDao()
                .insert(
                    MetaTrainingWithTrainingsCrossRef(
                        metaTrainingId,
                        trainingId
                    )
                )
        }
    }
}