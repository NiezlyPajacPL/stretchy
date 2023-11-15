package com.example.stretchy.features.executetraining.ui.composable.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.stretchy.theme.LapisLazuli


@Composable
fun AnimatedTrainingProgressBar(percentage: Float) {
    val initialProgressBarPosition =
        1f * percentage //0f - start of the progress bar, 1f - target position
    val animateLine =
        remember { Animatable(initialProgressBarPosition) }

    LaunchedEffect(animateLine) {
        animateLine.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearEasing
            ),
        )
    }
    TrainingProgressBar(percentage = percentage, progressBarFillingAmount = animateLine.value)
}

@Composable
fun TrainingProgressBar(
    progressBarFillingAmount: Float = 1f, // Used for animations
    percentage: Float
) {
    val thickness = 16.dp
    Canvas(Modifier.fillMaxWidth()) {
        drawLine(
            color = Color(LapisLazuli.toArgb()),
            start = Offset.Zero,
            end = Offset(progressBarFillingAmount * (size.width * percentage), 0f),
            strokeWidth = thickness.toPx()
        )
    }
}
