package com.example.stretchy.features.traininglist.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.composable.list.TrainingLazyListComposable
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.theme.White80

@Composable
fun TrainingListComposable(
    viewModel: TrainingListViewModel,
    navController: NavController,
    onExportClick: @Composable () -> Unit,
    onImportClick: @Composable () -> Unit,
    trainingType: TrainingType
) {
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("exerciseCreatorScreen?trainingType=$trainingType")
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.desc_plus_icon)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.toolbar_title))
                },
                actions = {
                    Menu(
                        viewModel,
                        onExportPermissionsNeeded = { showExportDialog = true },
                        onImportPermissionsNeeded = { showImportDialog = true })
                }
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .background(White80)
                .fillMaxSize()
        ) {
            if (showImportDialog) {
                onImportClick()
                showImportDialog = false
            }
            if (showExportDialog) {
                onExportClick()
                showExportDialog = false
            }

            Column(modifier = Modifier.padding(top = 24.dp)) {
                when (val state = viewModel.uiState.collectAsState().value) {
                    is TrainingListUiState.Empty ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.exercises_not_added),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.do_it_by_add_button),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    is TrainingListUiState.Loading ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    is TrainingListUiState.Loaded -> TrainingLazyListComposable(
                        state.trainings,
                        navController,
                        viewModel
                    )
                }
            }
        }
    }
}
/*

private fun isPermissionsGranted(context: Context, permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val result =
            ContextCompat.checkSelfPermission(context, permission)
        result == PackageManager.PERMISSION_GRANTED
    }
}*/
