package com.example.stretchy.features.traininglist.ui.data

import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.repository.Activity


fun getExercisesWithBreak(training: List<Activity>): List<Activity> {
    val exercisesWithBreaks: MutableList<Activity> = mutableListOf()
    training.forEachIndexed { i, exercise ->
        exercisesWithBreaks.add(exercise)
        if (i != training.lastIndex) {
            val nextExercise = training[i + 1]
            exercisesWithBreaks.add(Activity(nextExercise.name,i, 5, ActivityType.BREAK).apply {
                activityId = nextExercise.activityId
            })
        }
    }
    return exercisesWithBreaks
}