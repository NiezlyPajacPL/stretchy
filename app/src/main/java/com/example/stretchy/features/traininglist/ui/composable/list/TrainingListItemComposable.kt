package com.example.stretchy.features.traininglist.ui.composable.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.common.convertSecondsToMinutes
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.theme.WhiteSmoke

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrainingListItemComposable(
    training: Training,
    navController: NavController,
    vm: TrainingListViewModel
) {

    DismissState(initialValue = DismissValue.Default, confirmStateChange = {
        if (it == DismissValue.DismissedToEnd) {
            vm.deleteTraining(training)
        }
        true
    }).also {

        SwipeToDismiss(
            state = it,
            directions = setOf(DismissDirection.StartToEnd),
            dismissThresholds = { FractionalThreshold(0.2f) },
            background = {
                DismissBackgroundLayout(dismissState = it)
            },
            dismissContent = {
                Box(
                    Modifier
                        .background(color = Color.White)
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 20.dp),
                    Alignment.Center
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = training.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Row {
                                EditIconButton(
                                    navController = navController,
                                    trainingId = training.id,
                                    trainingType = training.type
                                )
                                Spacer(Modifier.width(16.dp))
                                CopyIconButton(vm = vm, training = training)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.LightGray, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        ExerciseDetailsRow(training = training)
                    }
                }
            }
        )
    }
}

@Composable
private fun EditIconButton(
    navController: NavController,
    trainingId: String,
    trainingType: Training.Type
) {
    IconButton(
        onClick = {
            navController.navigate("exerciseCreatorScreen?id=$trainingId&trainingType=$trainingType")
        },
        Modifier
            .size(20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_edit),
            contentDescription = stringResource(id = R.string.desc_edit_icon),
        )
    }
}

@Composable
private fun CopyIconButton(vm: TrainingListViewModel, training: Training) {
    IconButton(
        onClick = {
            vm.copyTraining(training)
        },
        Modifier
            .size(20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_copy),
            contentDescription = stringResource(id = R.string.desc_copy_icon),
        )
    }
}

@Composable
private fun ExerciseDetailsRow(training: Training) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = stringResource(R.string.exercises),
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${training.numberOfExercises}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(100.dp))
        Column {
            Text(
                text = stringResource(R.string.estimated_time),
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = convertSecondsToMinutes(training.timeInSeconds.toLong()),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DismissBackgroundLayout(dismissState: DismissState) {
    val color by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
            DismissValue.Default -> Color(WhiteSmoke.toArgb())
            DismissValue.DismissedToEnd -> Color.Red
            else -> {
                Color(WhiteSmoke.toArgb())
            }
        }
    )
    val icon = Icons.Default.Delete
    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp),
        Alignment.CenterStart
    ) {
        Icon(
            icon,
            contentDescription = stringResource(id = R.string.desc_delete_icon),
            Modifier.size(44.dp)
        )
    }
}