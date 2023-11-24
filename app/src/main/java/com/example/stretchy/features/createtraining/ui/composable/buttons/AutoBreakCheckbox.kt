package com.example.stretchy.features.createtraining.ui.composable.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.stretchy.R
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel

@Composable
fun AutoBreakCheckbox(viewModel: CreateOrEditTrainingViewModel) {
    var isAutoBreakChecked by remember { mutableStateOf(true) }
    var autoBreakDuration by remember { mutableStateOf("${viewModel.getAutoBreakDuration()}") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isAutoBreakChecked,
            onCheckedChange = {
                isAutoBreakChecked = it
                if (isAutoBreakChecked) {
                    viewModel.enableAutoBreaks()
                } else {
                    viewModel.disableAutoBreaks()
                }
            },
        )
        Text(
            modifier = Modifier,
            maxLines = 1,
            text = stringResource(id = R.string.append_auto_break),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        BasicTextField(
            modifier = Modifier
                .padding(start = 4.dp)
                .width(32.dp),
            value = autoBreakDuration,
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = true,
            onValueChange = {
                if (it.length <= 3 && it.isDigitsOnly()) {
                    autoBreakDuration = it
                    if (it.isNotBlank()) {
                        viewModel.updateAutoBreakDuration(it.toInt())
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(
            modifier = Modifier,
            maxLines = 1,
            text = stringResource(id = R.string.secs),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}