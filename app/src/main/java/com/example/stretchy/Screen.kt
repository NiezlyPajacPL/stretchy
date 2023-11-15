package com.example.stretchy

sealed class Screen(val route: String) {
    object StretchingListScreen : Screen("stretchingListScreen")
    object ExerciseCreatorScreen : Screen("exerciseCreatorScreen?id={id}&trainingType={trainingType}")
    object ExecuteTrainingScreen : Screen("executeTraining?id={id}")
    object TrainingListScreen : Screen("trainingListScreen")
    object MetaTrainingScreen : Screen("metaTrainingScreen")
}
