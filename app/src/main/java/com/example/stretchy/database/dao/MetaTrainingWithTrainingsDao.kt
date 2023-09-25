package com.example.stretchy.database.dao

import androidx.room.*
import com.example.stretchy.database.entity.metatraining.MetaTrainingWithTrainingsCrossRef
import com.example.stretchy.database.entity.metatraining.MetaTrainingWithTrainingsEntity

@Dao
interface MetaTrainingWithTrainingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(metaTrainingWithTrainingsCrossRef: MetaTrainingWithTrainingsCrossRef)

    @Transaction
    @Query("SELECT * FROM meta_training")
    fun getMetaTrainings(): List<MetaTrainingWithTrainingsEntity>

    @Transaction
    @Query("SELECT * FROM meta_training WHERE metaTrainingId LIKE :id ")
    fun getMetaTraining(id: Long): MetaTrainingWithTrainingsEntity

    @Query("SELECT * FROM meta_training_trainings_cross_ref WHERE metaTrainingId LIKE :metaTrainingId ")
    fun getTrainingIds(metaTrainingId : Long) : List<MetaTrainingWithTrainingsCrossRef>
    @Delete
    fun delete(metaTrainingWithTrainingsCrossRef: MetaTrainingWithTrainingsCrossRef)

    @Update
    fun update(metaTrainingWithTrainingsEntity: MetaTrainingWithTrainingsCrossRef)
}