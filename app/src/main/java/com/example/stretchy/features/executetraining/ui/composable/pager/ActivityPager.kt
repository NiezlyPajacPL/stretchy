package com.example.stretchy.features.executetraining.ui.composable.pager

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.pager.pages.ExerciseComposable
import com.example.stretchy.features.executetraining.ui.composable.pager.pages.TimelessExerciseComposable
import com.example.stretchy.features.executetraining.ui.composable.pager.pages.BreakComposable
import com.example.stretchy.features.executetraining.ui.data.ActivityItemExerciseAndBreakMerged
import com.example.stretchy.features.executetraining.ui.data.DisplayableActivityItem
import com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityPager(
    uiState: ExecuteTrainingUiState,
    viewModel: ExecuteTrainingViewModel,
    onTimedActivity: () -> Unit,
    onTimelessExercise: () -> Unit
) {
    val pagerState = rememberPagerState()
    val updatedInitialPage by rememberUpdatedState(uiState.currentDisplayPage)
    LaunchedEffect(updatedInitialPage) {
        pagerState.animateScrollToPage(
            updatedInitialPage,
            animationSpec = (tween(250))
        )
    }
    var getMaxSecondsFromList by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { currentPage ->
                getMaxSecondsFromList = true
                viewModel.changePage(currentPage, true)
            }
    }

    HorizontalPager(
        state = pagerState,
        pageCount = uiState.displayableActivityItemListWithBreakMerged!!.size
    ) { page ->
        val item = uiState.displayableActivityItemListWithBreakMerged!![page]
        if (page == uiState.currentDisplayPage) {
            FocusedPage(
                item = item,
                page = page,
                viewModel = viewModel,
                getMaxSecondsFromList = getMaxSecondsFromList,
                uiState = uiState,
                onTimedActivity = onTimedActivity,
                onTimelessExercise = onTimelessExercise,
                onSecondsTakenFromList = { getMaxSecondsFromList = false }
            )
        } else {
            PagesNotFocused(
                item = item,
                activityTypes = uiState.activityTypes!!,
                page = page,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun FocusedPage(
    item: ActivityItemExerciseAndBreakMerged,
    page: Int,
    viewModel: ExecuteTrainingViewModel,
    getMaxSecondsFromList: Boolean,
    uiState: ExecuteTrainingUiState,
    onTimedActivity: () -> Unit,
    onTimelessExercise: () -> Unit,
    onSecondsTakenFromList: () -> Unit
) {
    when (uiState.activityTypes?.get(page)) {
        ActivityType.STRETCH, ActivityType.EXERCISE -> {
            val activity =
                item.exercise as DisplayableActivityItem.Exercise
            ExerciseComposable(
                exerciseName = activity.name,
                nextExerciseName = activity.nextExercise,
                currentTime = if (!getMaxSecondsFromList) {
                    uiState.currentSeconds
                } else {
                    activity.currentTime
                },
                totalTime = activity.totalExerciseTime
            )
            onSecondsTakenFromList()
            onTimedActivity()
        }
        ActivityType.TIMELESS_EXERCISE -> {
            val exercise =
                item.exercise as DisplayableActivityItem.TimelessExercise
            TimelessExerciseComposable(
                exerciseName = exercise.name,
                nextExerciseName = exercise.nextExercise,
                viewModel = viewModel
            )
            onTimelessExercise()
        }
        ActivityType.BREAK -> {
            BreakComposable(
                nextExerciseName = item.exercise.nextExercise ?: "",
                currentTime = uiState.currentSeconds,
                totalTime = item.exercise.totalExerciseTime
            )
            onTimedActivity()
        }
        else -> {}
    }
}

@Composable
fun PagesNotFocused(
    item: ActivityItemExerciseAndBreakMerged,
    activityTypes: List<ActivityType>,
    page: Int,
    viewModel: ExecuteTrainingViewModel
) {
    when (activityTypes[page]) {
        ActivityType.STRETCH -> {
            with(item.exercise as DisplayableActivityItem.Exercise) {
                ExerciseComposable(
                    exerciseName = name,
                    nextExerciseName = nextExercise,
                    currentTime = currentTime,
                    totalTime = totalExerciseTime
                )
            }
        }
        ActivityType.EXERCISE -> {
            with(item.exercise as DisplayableActivityItem.Exercise) {
                ExerciseComposable(
                    exerciseName = name,
                    nextExerciseName = nextExercise,
                    currentTime = currentTime,
                    totalTime = totalExerciseTime
                )
            }
        }
        ActivityType.TIMELESS_EXERCISE ->
            with(item.exercise as DisplayableActivityItem.TimelessExercise) {
                TimelessExerciseComposable(
                    exerciseName = name,
                    nextExerciseName = nextExercise,
                    viewModel = viewModel
                )
            }
        ActivityType.BREAK -> {
            BreakComposable(
                nextExerciseName = item.exercise.nextExercise ?: "",
                currentTime = item.breakItem?.currentTime ?: 0F,
                totalTime = item.exercise.totalExerciseTime
            )
        }
    }
}