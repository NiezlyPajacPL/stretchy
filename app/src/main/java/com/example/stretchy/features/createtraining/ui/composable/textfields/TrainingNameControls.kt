package com.example.stretchy.features.createtraining.ui.composable.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel

@Composable
fun TrainingName(viewModel: CreateOrEditTrainingViewModel, initialTrainingName: String) {
    var trainingName by remember { mutableStateOf(initialTrainingName) }

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