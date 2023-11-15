package com.example.stretchy.features.createtraining.ui.composable.list.listitem

import androidx.compose.runtime.Composable
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.composable.widget.AddExerciseButtonHandler

@Composable
fun ActivityListItem(
    exerciseWithBreaks: ExercisesWithBreaks,
    position: Int,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    addExerciseButtonHandler: AddExerciseButtonHandler
) {

    if (!isExpanded) {
        CollapsedActivityItem(exerciseWithBreaks = exerciseWithBreaks, position = position) {
            onExpand()
        }
    } else {
        ExpandedItem(
            exerciseWithBreaks,
            onCollapse,
            addExerciseButtonHandler
        )
    }
}