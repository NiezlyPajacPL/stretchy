package com.example.stretchy.features.createtraining.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.theme.WhiteSmoke

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExerciseNameControls(
    currentName: String,
    onNameEntered: (value: String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var exerciseName = currentName
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
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
            singleLine = true,
            onValueChange = {
                exerciseName = it
                onNameEntered(it)
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide() // Hide the keyboard when the "Done" action is triggered
                }
            )
        )
    }
}
