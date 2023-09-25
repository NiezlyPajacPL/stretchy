package com.example.stretchy.database.dao

import androidx.room.*
import com.example.stretchy.database.entity.metatraining.MetaTrainingEntity

@Dao
interface MetaTrainingDao {
    @Query("SELECT * FROM meta_training")
    fun getAll(): List<MetaTrainingEntity>

    @Insert
    fun add(metaTrainingEntity: MetaTrainingEntity)

    @Delete
    fun delete(metaTrainingEntity: MetaTrainingEntity)

    @Query("DELETE FROM meta_training WHERE metaTrainingId = :metaTrainingId")
    fun deleteById(metaTrainingId: Long)

    @Update
    fun update(metaTrainingEntity: MetaTrainingEntity)
}