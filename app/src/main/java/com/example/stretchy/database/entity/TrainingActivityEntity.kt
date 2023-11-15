package com.example.stretchy.database.entity

import androidx.room.Entity

@Entity(
    tableName = "training_activities",
    primaryKeys = ["tId", "activityOrder"]
)
class TrainingActivityEntity(
    val tId: Long,
    val aId: Long,
    val activityOrder: Int
)