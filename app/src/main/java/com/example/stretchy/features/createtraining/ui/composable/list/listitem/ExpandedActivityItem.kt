package com.example.stretchy.features.createtraining.ui.composable.list.listitem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.composable.widget.AddExerciseButtonHandler
import com.example.stretchy.features.createtraining.ui.composable.widget.ExerciseAndBreakTabsWidget

@Composable
fun ExpandedItem(
    exerciseWithBreaks: ExercisesWithBreaks,
    onCollapse: () -> Unit,
    addExerciseButtonHandler: AddExerciseButtonHandler
) {
    var layoutHeight by remember { mutableStateOf(if (exerciseWithBreaks.exercise.duration == 0) 230.dp else 300.dp) }

    Box(androidx.compose.ui.Modifier.height(layoutHeight)) {
        ExerciseAndBreakTabsWidget(
            exerciseWithBreakToEdit = exerciseWithBreaks,
            onAddOrEditButtonClick = {
                onCollapse()
            },
            onTabSizeChange = {
                layoutHeight = when (it) {
                    ActivityType.STRETCH -> 300.dp
                    ActivityType.EXERCISE -> 300.dp
                    ActivityType.TIMELESS_EXERCISE -> 230.dp
                    ActivityType.BREAK -> 230.dp
                }
            },
            addExerciseButtonHandler = addExerciseButtonHandler
        )
    }
}