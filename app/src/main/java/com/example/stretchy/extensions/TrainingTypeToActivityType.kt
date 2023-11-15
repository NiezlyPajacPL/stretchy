package com.example.stretchy.extensions

import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.database.data.TrainingType

fun TrainingType.toActivityType(isTimelessExercise: Boolean): ActivityType {
    return when (this) {
        TrainingType.STRETCH -> ActivityType.STRETCH
        TrainingType.BODYWEIGHT -> if (isTimelessExercise) ActivityType.TIMELESS_EXERCISE else ActivityType.EXERCISE
    }
}