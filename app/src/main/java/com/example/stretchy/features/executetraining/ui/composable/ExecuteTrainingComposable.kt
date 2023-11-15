package com.example.stretchy.features.executetraining.ui.composable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.sound.Player
import com.example.stretchy.features.executetraining.sound.SoundTrack
import com.example.stretchy.features.executetraining.sound.Speaker
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.components.AnimatedTrainingProgressBar
import com.example.stretchy.features.executetraining.ui.composable.components.QuittingSnackbar
import com.example.stretchy.features.executetraining.ui.composable.pager.ActivityPager
import com.example.stretchy.features.executetraining.ui.data.event.ActivityFinishesEvent
import com.example.stretchy.features.executetraining.ui.data.event.BreakEndsEvent
import com.example.stretchy.features.executetraining.ui.data.event.ReadExerciseNameEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ExecuteTrainingComposable(
    viewModel: ExecuteTrainingViewModel,
    speaker: Speaker,
    player: Player,
    navController: NavController
) {

    var showSnackbar by remember { mutableStateOf(false) }
    var numberOfBackButtonClick = 0
    BackHandler {
        numberOfBackButtonClick++
        showSnackbar = true
        if (numberOfBackButtonClick == 2) {
            navController.popBackStack()
        }
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    var isTogglingTimerEnabled by remember { mutableStateOf(true) }

    Scaffold(scaffoldState = scaffoldState) { padding ->
        if (showSnackbar) {
            QuittingSnackbar(
                scaffoldState = scaffoldState,
                navController = navController,
                onDismiss = {
                    showSnackbar = false
                    numberOfBackButtonClick = 0
                })
        }
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            verticalArrangement = Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isTogglingTimerEnabled) {
                            viewModel.toggleStartStopTimer()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val state = viewModel.uiState.collectAsState().value
                consumeReadExerciseEvent(state.readExerciseNameEvent, coroutineScope, speaker)
                consumeActivityFinishedState(state.activityFinishesEvent, coroutineScope, player)
                consumeBreakEndsState(state.breakEndsEvent, coroutineScope, player)
                if (state.isLoading) {
                    Text(
                        text = stringResource(id = R.string.loading),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (state.displayableActivityItemListWithBreakMerged != null) {
                    ActivityPager(
                        state,
                        viewModel,
                        onTimedActivity = { isTogglingTimerEnabled = true },
                        onTimelessExercise = { isTogglingTimerEnabled = false })
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(10.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        AnimatedTrainingProgressBar(percentage = state.trainingProgressPercent)
                    }
                } else if (state.error != null) {
                    Text(
                        text = stringResource(id = R.string.error),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (state.trainingCompleted != null) {
                    state.trainingCompletedEvent?.consume()?.let {
                        coroutineScope.launch {
                            speaker.say(context.resources.getString(R.string.training_finished))
                        }
                    }
                    TrainingSummaryComposable(
                        numberOfExercises = state.trainingCompleted.numberOfExercises,
                        timeSpent = state.trainingCompleted.currentTrainingTime,
                        navController = navController
                    )
                }
            }
        }
    }
}

private fun consumeReadExerciseEvent(
    readExerciseNameEvent: ReadExerciseNameEvent?,
    coroutineScope: CoroutineScope,
    speaker: Speaker
) {
    readExerciseNameEvent?.consume()?.let {
        coroutineScope.launch {
            speaker.say(it)
        }
    }
}

private fun consumeActivityFinishedState(
    activityFinishesEvent: ActivityFinishesEvent?,
    coroutineScope: CoroutineScope,
    player: Player
) {
    activityFinishesEvent?.consume()?.let {
        coroutineScope.launch {
            player.playSound(SoundTrack.EXERCISE_ENDING)
        }
    }
}

private fun consumeBreakEndsState(
    breakEndsEvent: BreakEndsEvent?,
    coroutineScope: CoroutineScope,
    player: Player
) {
    breakEndsEvent?.consume()?.let {
        coroutineScope.launch {
            player.playSound(SoundTrack.BREAK_ENDED)
        }
    }
}

@Composable
fun TextSpacer(fontSize: TextUnit) {
    Text("", fontSize = fontSize)
}
