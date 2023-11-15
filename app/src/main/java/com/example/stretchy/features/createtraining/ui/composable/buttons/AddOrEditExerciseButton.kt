package com.example.stretchy.features.createtraining.ui.composable.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.stretchy.R

@Composable
fun AddOrEditExerciseButton(
    onClick: () -> Unit,
    isExerciseOrBreakBeingEdited: Boolean
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 2.dp),
        onClick = {
            onClick()
        }
    ) {
        if (isExerciseOrBreakBeingEdited) {
            Text(text = stringResource(id = R.string.save_changes))
        } else {
            Text(text = stringResource(id = R.string.add_exercise))
        }
    }
}

