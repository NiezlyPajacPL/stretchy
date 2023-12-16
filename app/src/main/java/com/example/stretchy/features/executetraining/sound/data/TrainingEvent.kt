package com.example.stretchy.features.executetraining.sound.data

import com.example.stretchy.repository.Activity


sealed class TrainingEvent {
    object ActivityEndsIn3Sec : TrainingEvent()
    object BreakEnds : TrainingEvent()
    object TrainingEnded : TrainingEvent()
    data class NewActivityStarts(
        val newActivity: Activity,
        val isFirstExercise: Boolean = false,
        val nextExerciseName: String? = null,
        val isSwipedByUser: Boolean,
    ) : TrainingEvent()
}