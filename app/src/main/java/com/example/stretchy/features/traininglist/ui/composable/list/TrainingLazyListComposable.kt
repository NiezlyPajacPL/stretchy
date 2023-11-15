package com.example.stretchy.features.traininglist.ui.composable.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.data.Training

@Composable
fun TrainingLazyListComposable(
    trainingList: List<Training>,
    navController: NavController,
    viewModel: TrainingListViewModel
) {
    LazyColumn {
        items(trainingList) { training ->
            Box(
                modifier = Modifier.clickable {
                    navController.navigate("executeTraining?id=${training.id}")
                },
            ) {
                TrainingListItemComposable(training = training, navController, viewModel)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
