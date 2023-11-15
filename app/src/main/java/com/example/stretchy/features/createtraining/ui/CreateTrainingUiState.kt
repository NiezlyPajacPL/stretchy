package com.example.stretchy.features.createtraining.ui

import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks

sealed class CreateTrainingUiState(
    open val saveButtonCanBeClicked: Boolean,
    open val isTrainingChanged: Boolean,
    open val isAutomaticBreakButtonClicked: Boolean
) {
    data class Success(
        val trainingId: Long?,
        val editingTraining: Boolean,
        val currentName: String,
        val exercisesWithBreaks: List<ExercisesWithBreaks>,
        val trainingType: TrainingType,
        override val isTrainingChanged: Boolean = false,
        override val saveButtonCanBeClicked: Boolean = false,
        override val isAutomaticBreakButtonClicked: Boolean = false
    ) : CreateTrainingUiState(saveButtonCanBeClicked, isTrainingChanged,isAutomaticBreakButtonClicked)

    data class Error(val reason: Reason) : CreateTrainingUiState(false, false, false) {
        sealed class Reason {
            object MissingTrainingName : Reason()
            object NotEnoughExercises : Reason()
            class Unknown(val exception: Exception) : Reason()
        }
    }

    object Init : CreateTrainingUiState(false, false, false)

    object Done : CreateTrainingUiState(true, true, false)
}

sealed class CreateTrainingActivityItem(
    open val duration: Float,
) {
    data class Exercise(
        val name: String,
        override val duration: Float,
    ) : CreateTrainingActivityItem(duration)

    data class Break(
        override val duration: Float,
    ) : CreateTrainingActivityItem(duration)
}