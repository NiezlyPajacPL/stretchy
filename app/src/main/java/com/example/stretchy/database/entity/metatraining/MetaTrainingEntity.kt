package com.example.stretchy.database.entity.metatraining

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "meta_training")
data class MetaTrainingEntity(
    @PrimaryKey
    val metaTrainingId: Long,
    val name: String,
    val lastExecuted: LocalDateTime?
)