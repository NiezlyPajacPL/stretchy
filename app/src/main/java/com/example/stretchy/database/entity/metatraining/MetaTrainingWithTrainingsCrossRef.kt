package com.example.stretchy.database.entity.metatraining

import androidx.room.Entity

@Entity(
    tableName = "meta_training_trainings_cross_ref",
    primaryKeys = ["metaTrainingId", "trainingId"]
)
data class MetaTrainingWithTrainingsCrossRef(
    val metaTrainingId: Long,
    val trainingId: Long
)