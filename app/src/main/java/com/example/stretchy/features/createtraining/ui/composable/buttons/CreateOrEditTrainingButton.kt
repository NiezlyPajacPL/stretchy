package com.example.stretchy.features.createtraining.ui.composable.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.stretchy.R
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.theme.BananaMania

@Composable
fun CreateOrEditTrainingButton(
    viewModel: CreateOrEditTrainingViewModel,
    isTrainingBeingEdited: Boolean,
    trainingId: Long?,
    exerciseList: List<ExercisesWithBreaks>
) {
    val buttonCanBeClicked = viewModel.uiState.collectAsState().value.saveButtonCanBeClicked
    val buttonColor =
        if (buttonCanBeClicked) ButtonDefaults.buttonColors(backgroundColor = Color(BananaMania.toArgb())) else ButtonDefaults.buttonColors(
            backgroundColor = Color.Gray
        )
    val textColors = if (buttonCanBeClicked) Color.Black else Color.DarkGray
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {
            if (buttonCanBeClicked) {
                if (isTrainingBeingEdited) {
                    viewModel.editTraining(exerciseList = exerciseList, trainingId = trainingId!!)
                } else {
                    viewModel.createTraining(exerciseList = exerciseList)
                }
            }
        },
        colors = buttonColor
    ) {
        if (isTrainingBeingEdited) {
            if (viewModel.uiState.collectAsState().value.isTrainingChanged) {
                Text(
                    stringResource(id = R.string.save_changes),
                    fontWeight = FontWeight.Bold,
                    color = textColors
                )
            } else {
                Text(
                    stringResource(id = R.string.close_editing),
                    fontWeight = FontWeight.Bold,
                    color = textColors
                )
            }

        } else {
            Text(
                stringResource(id = R.string.create_training),
                fontWeight = FontWeight.Bold,
                color = textColors
            )
        }
    }
}