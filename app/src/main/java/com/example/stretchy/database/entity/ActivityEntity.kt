package com.example.stretchy.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.stretchy.database.data.ActivityType

@Entity(
    tableName = "activity",
    indices = [Index(value = ["name", "duration"], unique = true)]
)
data class ActivityEntity(
    @PrimaryKey
    val activityId: Long,
    val name: String,
    val duration: Int,
    val activityType: ActivityType
)