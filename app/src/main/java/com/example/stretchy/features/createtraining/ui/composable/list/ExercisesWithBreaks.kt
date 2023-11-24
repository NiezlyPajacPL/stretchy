package com.example.stretchy.features.createtraining.ui.composable.list

import com.example.stretchy.features.createtraining.ui.data.Exercise


data class ExercisesWithBreaks(
    var listId: Int,
    val exercise: Exercise,
    var nextBreakDuration: Int?,
    var isExpanded: Boolean
)