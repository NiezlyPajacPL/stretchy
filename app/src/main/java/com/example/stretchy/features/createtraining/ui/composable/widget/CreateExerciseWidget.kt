package com.example.stretchy.features.createtraining.ui.composable.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.composable.ExerciseNameControls
import com.example.stretchy.features.createtraining.ui.composable.buttons.AddOrEditExerciseButton
import com.example.stretchy.features.createtraining.ui.composable.buttons.IncreaseOrDecreaseActivityDurationButtons
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.theme.BananaMania
import com.example.stretchy.theme.Caramel
import com.example.stretchy.theme.Purple500

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExerciseAndBreakTabsWidget(
    onAddOrEditButtonClick: () -> Unit,
    onTabSizeChange: (activityType: ActivityType) -> Unit,
    addExerciseButtonHandler: AddExerciseButtonHandler,
    exerciseWithBreakToEdit: ExercisesWithBreaks,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs =
        listOf(stringResource(id = R.string.exercise), stringResource(id = R.string.exercise_break))

    val doesExerciseExists = exerciseWithBreakToEdit.exercise.name != ""

    val currentExerciseWithBreak by remember {
        mutableStateOf(exerciseWithBreakToEdit)
    }
    var isTimelessExercise: Boolean by remember {
        mutableStateOf(exerciseWithBreakToEdit.exercise.duration == 0)
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .padding(top = 4.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)

    ) {
        Column(
            Modifier
                .background(color = Color(BananaMania.toArgb()))
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        color = Color(Purple500.toArgb()),
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(text = title, color = Color.Black) },
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                        },
                        modifier = Modifier.background(Color(Caramel.toArgb()))
                    )
                }
            }

            Column(
                Modifier
                    .padding(start = 12.dp, end = 12.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        ExerciseTab(
                            editedExercise = currentExerciseWithBreak.exercise,
                            onNameChange = {

                                if (it.isNotBlank()) {
                                    currentExerciseWithBreak.exercise.name = it
                                    addExerciseButtonHandler.showButton()
                                } else {
                                    addExerciseButtonHandler.hideButton()
                                }
                            },
                            onDurationChange = {
                                currentExerciseWithBreak.exercise.duration = it
                            },
                            isTimelessExercise = isTimelessExercise,
                            onCheckboxChange = {
                                isTimelessExercise = !isTimelessExercise
                                if (isTimelessExercise) {
                                    onTabSizeChange(ActivityType.TIMELESS_EXERCISE)
                                } else {
                                    onTabSizeChange(ActivityType.EXERCISE)
                                }
                            }
                        )
                        if (isTimelessExercise) {
                            onTabSizeChange(ActivityType.TIMELESS_EXERCISE)
                        } else {
                            onTabSizeChange(ActivityType.EXERCISE)
                        }
                    }
                    1 -> {
                        BreakTab(
                            breakToEditDuration = currentExerciseWithBreak.nextBreakDuration,
                            onDurationChange = {
                                if (currentExerciseWithBreak.nextBreakDuration == null) {
                                    currentExerciseWithBreak.nextBreakDuration = 0
                                }
                                currentExerciseWithBreak.nextBreakDuration = it
                            },
                        )
                        onTabSizeChange(ActivityType.BREAK)
                    }
                }
                val keyboardController = LocalSoftwareKeyboardController.current
                AddOrEditExerciseButton(
                    onClick = {
                        if (currentExerciseWithBreak.exercise.name.isNotEmpty()) {
                            onAddOrEditButtonClick()
                            keyboardController?.hide()
                        }
                    },
                    isExerciseOrBreakBeingEdited = doesExerciseExists
                )
            }
        }
    }
}

@Composable
private fun ExerciseTab(
    editedExercise: Exercise,
    onNameChange: (name: String) -> Unit,
    onDurationChange: (duration: Int) -> Unit,
    isTimelessExercise: Boolean,
    onCheckboxChange: (isChecked: Boolean) -> Unit //temp
) {
    val sliderMinValue = 1
    val sliderMaxValue = 300
    var exerciseDuration: Int by remember { mutableStateOf(sliderMinValue) }
    var exerciseName: String by remember { mutableStateOf("") }
    var durationBeforeTimelessExerciseSwitch: Int by remember {
        mutableStateOf(
            if (editedExercise.duration == 0) sliderMinValue
            else editedExercise.duration
        )
    }

    LaunchedEffect(editedExercise) {
        exerciseName = editedExercise.name
        exerciseDuration = editedExercise.duration
    }

    ExerciseNameControls(
        currentName = exerciseName,
        onNameEntered = {
            exerciseName = it
            onNameChange(exerciseName)
        })
    if (isTimelessExercise) {
        TimelessExerciseCheckbox(modifier = Modifier, onClick = {
            exerciseDuration = durationBeforeTimelessExerciseSwitch

            onDurationChange(exerciseDuration)
            onCheckboxChange(it)
        }, isTimelessExercise = true)
    } else {
        Spacer(Modifier.height(4.dp))
        Row(Modifier.height(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = Modifier
                    .padding(0.dp),
                text = stringResource(R.string.duration) + " ${
                    toDisplayableLength(
                        exerciseDuration
                    )
                }",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            TimelessExerciseCheckbox(modifier = Modifier.padding(top = 4.dp), onClick = {
                exerciseDuration = 0
                onDurationChange(exerciseDuration)
                onCheckboxChange(it)
            }, isTimelessExercise = false)
        }

        Slider(
            value = exerciseDuration.toFloat(),
            onValueChange = {
                durationBeforeTimelessExerciseSwitch = editedExercise.duration
                exerciseDuration = it.toInt()
                onDurationChange(exerciseDuration)
            },
            valueRange = sliderMinValue.toFloat()..sliderMaxValue.toFloat(),
            modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)
        )
        IncreaseOrDecreaseActivityDurationButtons { changeValue ->
            if (exerciseDuration + changeValue in sliderMinValue..300) {
                exerciseDuration += changeValue
                onDurationChange(exerciseDuration)
            }
        }
    }
}

@Composable
private fun BreakTab(
    breakToEditDuration: Int?,
    onDurationChange: (duration: Int) -> Unit,
) {
    val sliderMinValue = 0
    val sliderMaxValue = 300
    var breakDuration: Int by remember { mutableStateOf(sliderMinValue) }
    LaunchedEffect(breakToEditDuration) {
        if (breakToEditDuration != null) {
            breakDuration = breakToEditDuration
        }
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        text = stringResource(R.string.duration) + " ${
            toDisplayableLength(
                breakDuration
            )
        }",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Slider(
        value = breakDuration.toFloat(),
        onValueChange = {
            breakDuration = it.toInt()
        },
        valueRange = sliderMinValue.toFloat()..sliderMaxValue.toFloat(),
    )
    IncreaseOrDecreaseActivityDurationButtons { changeValue ->
        if (breakDuration + changeValue in sliderMinValue..sliderMaxValue) {
            breakDuration += changeValue
            onDurationChange(breakDuration)
        }
    }
}
fun toDisplayableLength(exerciseDuration: Int): String {
    return if (exerciseDuration >= 60) {
        val mins = exerciseDuration / 60
        val rest = exerciseDuration.mod(60)
        "$mins min $rest sec"
    } else {
        "$exerciseDuration sec"
    }
}

@Composable
private fun TimelessExerciseCheckbox(
    modifier: Modifier = Modifier,
    onClick: (isChecked: Boolean) -> Unit,
    isTimelessExercise: Boolean
) {
    var isChecked by remember { mutableStateOf(isTimelessExercise) }

    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onClick(isChecked)
            },
            modifier = Modifier.padding(0.dp)
        )

        Text(
            text = "TimelessExercise",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
    }
}
