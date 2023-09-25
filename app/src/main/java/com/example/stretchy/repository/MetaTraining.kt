package com.example.stretchy.repository

import com.example.stretchy.features.traininglist.ui.data.Training
import java.time.LocalDateTime

data class MetaTraining(
    val name: String,
    val trainings: List<Training>,
    val lastExecuted : LocalDateTime?
){
    var id: Long = 0
}