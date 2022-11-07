package com.example.stretchy.features.createtraining.ui.compose

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.Screen

@Composable
fun CreateTrainingComposable(navController: NavController) {
    val temp = remember { mutableStateListOf<Exercises>()}
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(Modifier.padding(top = 16.dp)) {
            ExerciseList(exercises = temp)
            CreateSequence(temp)
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), onClick = {
                navController.navigate(
                    Screen.ExercisePlansScreen.route
                )
            }) {
                //send exercises list to db
                Text("Create training!")
            }
        }
    }
}

@Composable
private fun ExerciseList(exercises: List<Exercises>) {
    LazyColumn {
        items(exercises) { exercise ->
            ExerciseItem(item = exercise)
        }
    }
}

@Composable
private fun ExerciseItem(item: Exercises) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color.LightGray)

    ) {
        Text(text = item.exerciseName, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CreateSequence(temp: MutableList<Exercises>) {
    var visible by remember { mutableStateOf(false) }
    val sliderMaxValue = 300
    val sliderSteps: Int = (sliderMaxValue / 60) - 1
    var sliderValue: Int by remember { mutableStateOf(0) }
    var exerciseDuration: Int by remember { mutableStateOf(0) }
    var exerciseName = ""
    val context = LocalContext.current
    AnimatedVisibility(visible = !visible) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.LightGray)
                .clickable {
                    visible = !visible
                }
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Column(
            Modifier
                .padding(start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.LightGray)
                .padding(start = 12.dp, end = 12.dp)
        ) {
            ExerciseNameControls(onNameEntered = {exerciseName = it})
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = "Duration: ${toDisplayableLength(exerciseDuration)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = sliderValue.toFloat(),
                onValueChange = {
                    sliderValue = it.toInt()
                    exerciseDuration = sliderValue
                },
                valueRange = 0f..sliderMaxValue.toFloat(),
                steps = sliderSteps,
            )
            AddOrSubtractButtons { changeValue ->
                sliderValue += changeValue
                exerciseDuration = sliderValue
            }
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 16.dp),
                onClick = {
                    if(exerciseName.isNotEmpty() && sliderValue != 0){
                        visible = !visible
                        temp.add(Exercises(exerciseName))
                        sliderValue = 0
                        exerciseDuration = 0
                        Toast.makeText(context, "Exercise added", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(context, "You need to specify exercise properties!", Toast.LENGTH_LONG).show()
                    }
                }
            ) { Text(text = "Add Exercise") }
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
fun ExerciseNameControls(
    onNameEntered: (value: String) -> Unit
) {
    var exerciseName by remember { mutableStateOf("") }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        text = "Name:",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        value = exerciseName,
        textStyle = TextStyle(fontSize = 12.sp),
        onValueChange = {
            exerciseName = it
            onNameEntered(it)
        },
    )
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