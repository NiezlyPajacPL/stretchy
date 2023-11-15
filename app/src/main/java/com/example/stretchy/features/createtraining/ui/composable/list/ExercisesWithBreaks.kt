package com.example.stretchy.features.createtraining.ui.composable.list

import com.example.stretchy.features.createtraining.ui.data.Exercise


data class ExercisesWithBreaks(
    val listId: Int,
    val exercise: Exercise,
    var nextBreakDuration: Int?,
    var isExpanded: Boolean
)