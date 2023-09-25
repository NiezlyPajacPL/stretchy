package com.example.stretchy.database.entity.metatraining

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.stretchy.database.entity.TrainingEntity

data class MetaTrainingWithTrainingsEntity(
    @Embedded val metaTrainingEntity: MetaTrainingEntity,
    @Relation(
        parentColumn = "metaTrainingId",
        entityColumn = "trainingId",
        entity = TrainingEntity::class,
        associateBy = Junction(
            value = MetaTrainingWithTrainingsCrossRef::class,
            parentColumn = "metaTrainingId",
            entityColumn = "trainingId"
        )
    )
    val trainings: List<TrainingEntity>
)