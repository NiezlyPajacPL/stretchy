package com.example.stretchy.features.executetraining.ui.composable.pager.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.ui.composable.TextSpacer
import com.example.stretchy.features.executetraining.ui.composable.timer.TimerComposable


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExerciseComposable(
    exerciseName: String,
    nextExerciseName: String?,
    currentTime: Float,
    totalTime: Int
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextSpacer(fontSize = 16.sp)
        AnimatedContent(
            targetState = exerciseName
        ) {
            Box(
                Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .weight(1f, fill = false)
            ) {
                Text(text = it, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
        TimerComposable(
            totalSeconds = totalTime.toFloat() * 1000,
            modifier = Modifier.size(300.dp),
            currentSeconds = currentTime
        )
        Spacer(modifier = Modifier.height(36.dp))
        if (!nextExerciseName.isNullOrBlank()) {
            Text(
                text = stringResource(id = R.string.nxt_exercise),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
        } else {
            TextSpacer(fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedContent(
            targetState = nextExerciseName
        ) {
            Box(
                Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .weight(1f, fill = false)
            ) {
                Text(text = it ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}