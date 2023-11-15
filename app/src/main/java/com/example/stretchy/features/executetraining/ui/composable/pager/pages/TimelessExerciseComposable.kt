package com.example.stretchy.features.executetraining.ui.composable.pager.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.TextSpacer


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimelessExerciseComposable(
    exerciseName: String,
    nextExerciseName: String?,
    viewModel: ExecuteTrainingViewModel
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
        Spacer(modifier = Modifier.height(130.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_no_break),
            contentDescription = "",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))
        if (!nextExerciseName.isNullOrBlank()) {
            Text(
                text = "Swipe for next exercise",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
        } else {
            Button(onClick = { viewModel.endTraining() }) {
                Text(
                    text = "End training",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            }
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