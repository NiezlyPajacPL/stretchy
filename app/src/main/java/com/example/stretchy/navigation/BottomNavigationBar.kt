package com.example.stretchy.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.database.data.TrainingType


@Composable
fun BottomNavigationBar(
    activityComponent: ActivityComponent,
    onExportClick: @Composable () -> Unit,
    onImportClick: @Composable () -> Unit
) {
    val navController = rememberNavController()
    var showBottomNavBar by remember { (mutableStateOf(true)) }

    val screens = listOf(
        BottomNavScreen(
            stringResource(id = R.string.stretching),
            painterResource(id = R.drawable.ic_stretching),
            Screen.StretchingListScreen.route
        ),
        BottomNavScreen(
            stringResource(id = R.string.meta_training),
            painterResource(id = R.drawable.ic_meta_training),
            Screen.MetaTrainingScreen.route
        ),
        BottomNavScreen(
            stringResource(id = R.string.training),
            painterResource(id = R.drawable.ic_training),
            Screen.TrainingListScreen.route
        ),
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNavBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = shrinkVertically(),
                content = {
                    BottomNavigation {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        screens.forEach { screen ->
                            BottomNavigationItem(
                                icon = { Icon(screen.icon, contentDescription = "") },
                                label = { Text(text = screen.label) },
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route)
                                }
                            )
                        }
                    }
                })
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.StretchingListScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.StretchingListScreen.route) {
                Navigation(
                    activityComponent,
                    onExportClick = onExportClick,
                    onImportClick = onImportClick,
                    Screen.StretchingListScreen.route,
                    hideBottomNavBar = { showBottomNavBar = false },
                    showBottomNavBar = { showBottomNavBar = true },
                    trainingType = TrainingType.STRETCH
                )
            }
            composable(Screen.MetaTrainingScreen.route) {

            }
            composable(Screen.TrainingListScreen.route) {
                Navigation(
                    activityComponent,
                    onExportClick = onExportClick,
                    onImportClick = onImportClick,
                    Screen.TrainingListScreen.route,
                    hideBottomNavBar = { showBottomNavBar = false },
                    showBottomNavBar = { showBottomNavBar = true },
                    trainingType = TrainingType.BODYWEIGHT
                )
            }
        }
    }
}