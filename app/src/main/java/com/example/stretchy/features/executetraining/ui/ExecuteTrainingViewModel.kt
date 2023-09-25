package com.example.stretchy.features.executetraining.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.common.convertSecondsToMinutes
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.Timer
import com.example.stretchy.features.executetraining.ui.data.*
import com.example.stretchy.features.executetraining.ui.data.event.ActivityFinishesEvent
import com.example.stretchy.features.executetraining.ui.data.event.BreakEndsEvent
import com.example.stretchy.features.executetraining.ui.data.event.ReadExerciseNameEvent
import com.example.stretchy.features.executetraining.ui.data.event.TrainingCompletedEvent
import com.example.stretchy.features.traininglist.ui.data.getExercisesWithBreak
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.util.*

class ExecuteTrainingViewModel(val repository: Repository, val trainingId: Long) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ExecuteTrainingUiState(
            true,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    )
    val uiState: StateFlow<ExecuteTrainingUiState> = _uiState

    private var timer: Timer = Timer()
    private var isPaused = true

    private var startingTimestampSaved = false
    private var currentExerciseLoaded: Long? = null
    private var activityFinishedEventPosted: Long? = null
    private var lastActivityBreakSoundPosted: Long? = null
    private var startingTimestamp = 0L
    private var trainingProgressPercent = 0f


    init {
        if (!startingTimestampSaved) {
            saveStartingTimeStamp()
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val trainingWithActivities = repository.getTrainingWithActivitiesById(trainingId)
            getExercisesWithBreak(trainingWithActivities.activities).let { activitiesWithBreaks ->
                activitiesWithBreaks.forEachIndexed { index, activity ->
                    timer.setDuration(activity.duration)
                    timer.flow.takeWhile { it >= 0 }.collect { currentSeconds ->
                        when (activity.activityType) {
                            ActivityType.STRETCH -> {
                                handleStretch(
                                    activity,
                                    activitiesWithBreaks.getOrNull(index + 2)?.name,
                                    currentSeconds,
                                    index == 0
                                )
                            }
                            ActivityType.BREAK -> {
                                handleBreak(
                                    activity,
                                    activitiesWithBreaks.getOrNull(index + 1)?.name,
                                    currentSeconds
                                )
                            }
                            else -> {}
                        }
                    }
                    if (activity.activityType == ActivityType.STRETCH) {
                        increaseTrainingPercentageCount(trainingWithActivities.activities.size)
                    }
                    if (isTrainingFinished(trainingWithActivities.activities, index)) {
                        handleTrainingFinishedState(trainingWithActivities.activities.size)
                    }
                }
            }
        }
    }

    private fun handleStretch(
        activity: Activity,
        nextExerciseName: String?,
        currentSeconds: Float,
        isFirstExercise: Boolean,
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = null,
            success = ActivityItem.Exercise(
                activity.name,
                nextExerciseName,
                currentSeconds,
                activity.duration,
                trainingProgressPercent
            ),
            readExerciseNameEvent = if (isFirstExercise) {
                postReadExerciseNameEvent(
                    activity.activityId,
                    activity.name
                )
            } else {
                _uiState.value.readExerciseNameEvent
            },
            activityFinishesEvent = postActivityFinishesEvent(
                activity.activityId,
                currentSeconds
            )
        )
    }

    private fun handleBreak(
        activity: Activity,
        nextExerciseName: String?,
        currentSeconds: Float,
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = null,
            success = ActivityItem.Break(
                nextExerciseName!!,
                currentSeconds,
                activity.duration,
                trainingProgressPercent
            ),
            readExerciseNameEvent = postReadExerciseNameEvent(
                activity.activityId,
                activity.name
            ),
            breakEndsEvent = postBreakFinishesEvent(
                activity.activityId,
                currentSeconds
            )
        )
    }

    private fun handleTrainingFinishedState(allExercisesCount: Int) {
        val currentTime = Calendar.getInstance()
        val seconds = (currentTime.timeInMillis - startingTimestamp) / 1000
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = null,
            success = null,
            readExerciseNameEvent = null,
            trainingCompleted = TrainingCompleted(
                currentTrainingTime = convertSecondsToMinutes(
                    seconds
                ),
                numberOfExercises = allExercisesCount
            ),
            trainingCompletedEvent = TrainingCompletedEvent()
        )
    }

    private fun isTrainingFinished(activities: List<Activity>, index: Int): Boolean {
        return activities.getOrNull(index + 1) == null
    }

    private fun increaseTrainingPercentageCount(activitiesCount: Int) {
        trainingProgressPercent += 1 / activitiesCount.toFloat()
    }

    private fun postActivityFinishesEvent(
        activityId: Long,
        currentSeconds: Float
    ): ActivityFinishesEvent? {
        return if (activityFinishedEventPosted != activityId && currentSeconds <= 3000) {
            activityFinishedEventPosted = activityId
            ActivityFinishesEvent()
        } else {
            _uiState.value.activityFinishesEvent
        }
    }

    private fun postBreakFinishesEvent(
        activityId: Long,
        currentSeconds: Float
    ): BreakEndsEvent? {
        return if (lastActivityBreakSoundPosted != activityId && currentSeconds <= 100) {
            lastActivityBreakSoundPosted = activityId
            BreakEndsEvent()
        } else {
            _uiState.value.breakEndsEvent
        }
    }

    private fun postReadExerciseNameEvent(
        activityId: Long,
        name: String
    ): ReadExerciseNameEvent? {
        return if (currentExerciseLoaded != activityId) {
            currentExerciseLoaded = activityId
            ReadExerciseNameEvent(name)
        } else {
            _uiState.value.readExerciseNameEvent
        }
    }

    fun toggleStartStopTimer() {
        if (!isPaused) {
            isPaused = true
            Log.i(TIMER_LOG_TAG, "Timer is paused.")
            timer.pause()
        } else {
            isPaused = false
            Log.i(TIMER_LOG_TAG, "Timer is resumed.")
            timer.start()
        }
    }

    private fun saveStartingTimeStamp() {
        val startDate = Calendar.getInstance()
        Log.i("Start date", "Started on ${startDate.time}")
        startingTimestamp = startDate.timeInMillis
        startingTimestampSaved = true
    }

    companion object {
        private const val TIMER_LOG_TAG = "TIMER"
    }
}