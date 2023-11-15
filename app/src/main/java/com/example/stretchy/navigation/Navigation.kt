package com.example.stretchy.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stretchy.Screen
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.extensions.daggerViewModel
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.CreateTrainingComposable
import com.example.stretchy.features.createtraining.ui.di.CreateTrainingComponent
import com.example.stretchy.features.executetraining.di.ExecuteTrainingComponent
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.ExecuteTrainingComposable
import com.example.stretchy.features.traininglist.di.TrainingListComponent
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.composable.TrainingListComposable

@Composable
fun Navigation(
    activityComponent: ActivityComponent,
    onExportClick: @Composable () -> Unit,
    onImportClick: @Composable () -> Unit,
    startDestination: String,
    hideBottomNavBar: () -> Unit,
    showBottomNavBar: () -> Unit,
    trainingType: TrainingType
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    handleBottomNavBarVisibility(navBackStackEntry, hideBottomNavBar, showBottomNavBar)

    NavHost(navController = navController, startDestination = startDestination) {
        composable(
            route = Screen.TrainingListScreen.route,
        ) {
            val component = TrainingListComponent.create(activityComponent, trainingType)
            val vm = createTrainingListViewModel(
                component,
                activityComponent.activity(),
                LocalViewModelStoreOwner.current!!
            )
            TrainingListComposable(
                navController = navController,
                viewModel = vm,
                onExportClick = onExportClick,
                onImportClick = onImportClick,
                trainingType = trainingType
            )
        }

        composable(
            route = Screen.StretchingListScreen.route
        ) {
            val component = TrainingListComponent.create(activityComponent, trainingType)
            val vm = createTrainingListViewModel(
                component,
                activityComponent.activity(),
                LocalViewModelStoreOwner.current!!
            )
            TrainingListComposable(
                navController = navController,
                viewModel = vm,
                onExportClick = onExportClick,
                onImportClick = onImportClick,
                trainingType = trainingType
            )
        }
        composable(
            route = Screen.ExerciseCreatorScreen.route,
            arguments = listOf(
                navArgument("id") { defaultValue = "-1" },
                navArgument("trainingType") {})
        ) {
            val trainingId = it.arguments?.getString("id")!!.toLong()
            val component =
                CreateTrainingComponent.create(activityComponent, trainingId, trainingType)
            val vm = createCreateTrainingViewModel(
                component,
                activityComponent.activity(),
                LocalViewModelStoreOwner.current!!
            )
            CreateTrainingComposable(
                navController = navController,
                viewModel = vm
            )
        }
        composable(
            route = Screen.ExecuteTrainingScreen.route,
            arguments = listOf(navArgument("id") { defaultValue = "-1" })
        ) {
            val trainingId = it.arguments?.getString("id")!!.toLong()
            val component = ExecuteTrainingComponent.create(activityComponent, trainingId)
            val vm = createExecuteTrainingViewModel(
                component,
                activityComponent.activity(),
                LocalViewModelStoreOwner.current!!,
            )
            ExecuteTrainingComposable(
                vm,
                activityComponent.speaker(),
                component.player(),
                navController
            )
        }
    }
}

private fun createExecuteTrainingViewModel(
    executeTrainingComponent: ExecuteTrainingComponent,
    componentActivity: ComponentActivity,
    viewModelStoreOwner: ViewModelStoreOwner,
): ExecuteTrainingViewModel {
    val provider = executeTrainingComponent.viewModelProvider()
    val vm by componentActivity.daggerViewModel(owner = viewModelStoreOwner) { provider }
    return vm
}

private fun createCreateTrainingViewModel(
    createTrainingComponent: CreateTrainingComponent,
    componentActivity: ComponentActivity,
    viewModelStoreOwner: ViewModelStoreOwner,
): CreateOrEditTrainingViewModel {
    val provider = createTrainingComponent.viewModelProvider()
    val vm by componentActivity.daggerViewModel(owner = viewModelStoreOwner) { provider }
    return vm
}

private fun createTrainingListViewModel(
    trainingListComponent: TrainingListComponent,
    componentActivity: ComponentActivity,
    viewModelStoreOwner: ViewModelStoreOwner,
): TrainingListViewModel {
    val provider = trainingListComponent.viewModelProvider()
    val vm by componentActivity.daggerViewModel(owner = viewModelStoreOwner) { provider }
    return vm
}

private fun handleBottomNavBarVisibility(
    navBackStackEntry: NavBackStackEntry?, hideBottomNavBar: () -> Unit,
    showBottomNavBar: () -> Unit
) {
    when (navBackStackEntry?.destination?.route) {
        Screen.ExerciseCreatorScreen.route -> {
            hideBottomNavBar()
        }
        Screen.StretchingListScreen.route -> {
            showBottomNavBar()
        }
        Screen.TrainingListScreen.route -> {
            showBottomNavBar()
        }
        Screen.ExecuteTrainingScreen.route -> {
            hideBottomNavBar()
        }
    }
}