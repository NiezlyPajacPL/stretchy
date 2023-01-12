package com.example.stretchy.features.createtraining.ui.composable

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.CreateTrainingUiState
import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.repository.Activity
import com.example.stretchy.theme.BananaMania
import com.example.stretchy.theme.WhiteSmoke
import kotlin.math.roundToInt

@Composable
fun CreateTrainingComposable(
    navController: NavController,
    viewModel: CreateTrainingViewModel
) {
    var trainingId: Long by remember { mutableStateOf(-1) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            Modifier
                .padding(top = 16.dp)
        ) {
            TrainingName(viewModel)
            Spacer(modifier = Modifier.height(24.dp))
            when (val state = viewModel.uiState.collectAsState().value) {
                is CreateTrainingUiState.Success -> {
                    ExerciseList(exercises = state.training, viewModel = viewModel)
                }
                is CreateTrainingUiState.Editing -> {
                    trainingId = state.trainingId
                    ExerciseList(exercises = state.activities, viewModel = viewModel)
                }
                else -> {
                    ExerciseList(emptyList(), viewModel = viewModel)
                }
            }
        }
        Spacer(modifier = Modifier.height(200.dp))
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    if (isTrainingBeingEdited(trainingId)) {
                        viewModel.editTraining(trainingId = trainingId)
                    } else {
                        viewModel.createTraining()
                    }
                    navController.navigate(Screen.TrainingListScreen.route)
                }
            ) {
                if (isTrainingBeingEdited(trainingId)) {
                    Text("Edit")
                } else {
                    Text(stringResource(id = R.string.create_training))
                }
            }
        }
    }
}

@Composable
private fun ExerciseList(exercises: List<Activity>, viewModel: CreateTrainingViewModel) {
    var editedExercise by remember { mutableStateOf(Exercise()) }
    var widgetVisible by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.heightIn(0.dp, 240.dp),
        verticalArrangement = Arrangement.Top
    ) {
        itemsIndexed(exercises) { listPosition, exercise ->
            SwipeableExerciseItem(
                vm = viewModel,
                item = Exercise(exercise.name, exercise.duration, listPosition),
                listId = listPosition,
                onEditClick = {
                    editedExercise = it
                    if (widgetVisible) {
                        widgetVisible = false
                    }
                    widgetVisible = true
                }
            )
        }
    }
    CreateExerciseWidget(
        viewModel = viewModel,
        editedExercise = editedExercise,
        widgetVisible = widgetVisible,
        onAddClick = { widgetVisible = !widgetVisible })
}

@Composable
fun CreateExerciseWidget(
    viewModel: CreateTrainingViewModel,
    editedExercise: Exercise,
    widgetVisible: Boolean,
    onAddClick: () -> Unit
) {
    val sliderMinValue = 10
    val sliderMaxValue = 300
    var exerciseDuration: Int by remember { mutableStateOf(sliderMinValue) }
    val context = LocalContext.current
    val exerciseIsBeingEdited: Boolean = editedExercise.name != ""

    var exerciseName = editedExercise.name
    exerciseDuration = editedExercise.duration

    AnimatedVisibility(visible = !widgetVisible) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(BananaMania.toArgb()))
                .clickable {
                    onAddClick()
                }
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
        }
    }
    AnimatedVisibility(
        visible = widgetVisible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Column(
            Modifier
                .padding(start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(BananaMania.toArgb()))
                .padding(start = 12.dp, end = 12.dp)
        ) {
            ExerciseNameControls(asd = exerciseName, onNameEntered = { exerciseName = it })
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = "Duration: ${toDisplayableLength(exerciseDuration)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = exerciseDuration.toFloat(),
                onValueChange = {
                    exerciseDuration = it.toInt()
                },
                valueRange = 10f..sliderMaxValue.toFloat(),
            )
            AddOrSubtractButtons { changeValue ->
                if (exerciseDuration + changeValue in 10..300) {
                    exerciseDuration += changeValue
                }
            }
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 16.dp),
                onClick = {
                    if (exerciseName.isNotEmpty() && exerciseDuration != 0) {
                        onAddClick()
                        if (exerciseIsBeingEdited) {
                            viewModel.editActivity(
                                Activity(
                                    exerciseName,
                                    exerciseDuration,
                                    ActivityType.STRETCH
                                ), editedExercise.listId!!
                            )
                            Toast.makeText(context, "Exercise edited", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.addActivity(
                                Activity(
                                    exerciseName,
                                    exerciseDuration,
                                    ActivityType.STRETCH
                                )
                            )
                            Toast.makeText(context, "Exercise added", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "You need to specify exercise name!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                if (exerciseIsBeingEdited) {
                    Text(text = "Edit Exercise")
                } else {
                    Text(text = "Add Exercise")
                }
            }
        }
    }
}

@Composable
fun AddOrSubtractButtons(onTextEntered: (value: Int) -> Unit) {
    val modifier = Modifier
        .width(44.dp)
        .padding(end = 6.dp)
    Row {
        Button(modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(-10) })
        {
            Text(text = "-10")
        }
        Button(modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(-5) })
        {
            Text(text = "-5")
        }
        Button(modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(-1) })
        {
            Text(text = "-1")
        }
        Spacer(modifier = Modifier.width(52.dp))
        Button(modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(+1) })
        {
            Text(text = "+1")
        }
        Button(
            modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(+5) })
        {
            Text(text = "+5")
        }
        Button(
            modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(+10) })
        {
            Text(text = "+10")
        }
    }
}

@Composable
fun TrainingName(viewModel: CreateTrainingViewModel) {
    var trainingName by remember { mutableStateOf("") }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 16.dp, end = 16.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White
        ),
        label = { Text(stringResource(id = R.string.training_name)) },
        value = trainingName,
        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
        singleLine = true,
        onValueChange = {
            trainingName = it
            viewModel.setTrainingName(it)
        }
    )
}

@Composable
fun ExerciseNameControls(
    asd: String,
    onNameEntered: (value: String) -> Unit
) {
    var exerciseName by remember { mutableStateOf(asd) }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        text = stringResource(id = R.string.name),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Box(
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(percent = 10),
                color = Color(WhiteSmoke.toArgb()),
            )
            .height(36.dp)
            .padding(start = 12.dp, end = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            value = exerciseName,
            textStyle = TextStyle(fontSize = 16.sp),
            onValueChange = {
                exerciseName = it
                onNameEntered(it)
            },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableExerciseItem(
    vm: CreateTrainingViewModel,
    item: Exercise,
    listId: Int,
    onEditClick: (exercise: Exercise) -> Unit
) {
    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states
    fun isItemSwiped(): Boolean {
        return swipeableState.currentValue == 1
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(WhiteSmoke.toArgb()))
    ) {
        if (isItemSwiped()) {
            SwipeActions(vm, item, listId, onEditClick)
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
            .background(Color(BananaMania.toArgb()))
            .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(item.name)
        }
    }
}

@Composable
fun SwipeActions(
    viewModel: CreateTrainingViewModel,
    exercise: Exercise,
    exerciseId: Int,
    onEditClick: (exercise: Exercise) -> Unit
) {
    Row {
        IconButton(
            modifier = Modifier.padding(top = 2.dp),
            onClick = { viewModel.deleteExercise(exerciseId) }) {
            Icon(Icons.Filled.Delete, "Delete exercise")
        }
        IconButton(modifier = Modifier.padding(top = 2.dp), onClick = { onEditClick(exercise) }) {
            Icon(Icons.Filled.Edit, "asd")
        }
    }
}

private fun toDisplayableLength(exerciseDuration: Int): String {
    return if (exerciseDuration >= 60) {
        val mins = exerciseDuration / 60
        val rest = exerciseDuration.mod(60)
        "$mins min $rest sec"
    } else {
        "$exerciseDuration sec"
    }
}

private fun isTrainingBeingEdited(trainingId: Long): Boolean {
    return trainingId >= 0
}