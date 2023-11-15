package com.example.stretchy.features.executetraining.ui.composable.components

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.stretchy.R

@Composable
fun QuittingSnackbar(
    scaffoldState: ScaffoldState,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(scaffoldState.snackbarHostState) {
        val snackbarResult =
            scaffoldState.snackbarHostState.showSnackbar(
                message = context.resources.getString(R.string.do_you_want_to_quit),
                actionLabel = context.resources.getString(R.string.quit),
                duration = SnackbarDuration.Short
            )
        when (snackbarResult) {
            SnackbarResult.Dismissed -> {
                onDismiss()
            }
            SnackbarResult.ActionPerformed -> navController.popBackStack()
        }
    }
}