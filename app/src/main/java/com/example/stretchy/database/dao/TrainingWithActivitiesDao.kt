package com.example.stretchy.database.dao

import androidx.room.*
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingWithActivitiesEntity

@Dao
interface TrainingWithActivitiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trainingActivity: TrainingActivityEntity)

    @Transaction
    @Query("SELECT * FROM training")
    fun getTrainings(): List<TrainingWithActivitiesEntity>

    @Transaction
    @Query("SELECT * FROM training WHERE trainingId LIKE :id ")
    fun getTrainingsById(id: Long): TrainingWithActivitiesEntity

    @Transaction
    @Query("SELECT * FROM training_activities WHERE tId LIKE :trainingId ")
    fun getTrainingsActivitiesByTrainingId(trainingId: Long): List<TrainingActivityEntity>

    @Delete
    fun delete(trainingActivity: TrainingActivityEntity)

    @Update
    fun update(trainingActivityEntity: TrainingActivityEntity)
}