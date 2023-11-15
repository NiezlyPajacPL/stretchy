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
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep
import java.util.*

class ExecuteTrainingViewModel(val repository: Repository, val trainingId: Long) : ViewModel() {
    private val _uiState = initUiState()
    val uiState: StateFlow<ExecuteTrainingUiState> = _uiState

    private var timer: Timer = Timer()
    private var isPaused = true

    private var startingTimestampSaved = false
    private var currentExerciseLoaded: Long? = null
    private var activityFinishedEventPosted: Long? = null
    private var lastActivityBreakSoundPosted: Long? = null
    private var startingTimestamp = 0L
    private var index = 0

    private var skippedByUser = false
    private lateinit var trainingWithActivities: TrainingWithActivity


    init {
        if (!startingTimestampSaved) {
            saveStartingTimeStamp()
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            trainingWithActivities = repository.getTrainingWithActivitiesById(trainingId)
            initializeDisplayableList()

            trainingWithActivities.activities.let { activitiesWithBreaks ->
                while (!trainingFinished()) {
                    activitiesWithBreaks[index].let {
                        presetCurrentExercise(it)
                        startExerciseTrainingFlow(it)
                        changePageAfterActivityEnds(it)
                    }
                }
            }
        }
    }

    fun changePage(destinationPage: Int, isSkippedByUser: Boolean) {
        val currentPage = _uiState.value.currentDisplayPage
        if (currentPage != destinationPage) {
            viewModelScope.launch {
                restorePreviousExerciseOnPreviousPage(page = currentPage)
            }
            if (isSkippedByUser) {
                handleUserSkip(currentPage, destinationPage)
            }
            _uiState.value = _uiState.value.copy(
                trainingProgressPercent = getPercentageForPage(destinationPage),
                currentDisplayPage = destinationPage
            )
        }
    }

    fun endTraining() {
        setTrainingFinishedState(_uiState.value.displayableActivityItemListWithBreakMerged!!.size)
    }

    fun toggleStartStopTimer() {
        Log.e(TIMER_LOG_TAG, "toggle start stop")
        if (!isPaused) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private suspend fun startExerciseTrainingFlow(currentActivity: Activity) {
        timer.flow.takeWhile { it >= 0 && !skippedByUser }
            .collect { currentSeconds ->
                _uiState.value = _uiState.value.copy(currentSeconds = currentSeconds)
                when (currentActivity.activityType) {
                    ActivityType.STRETCH, ActivityType.EXERCISE -> {
                        handleTimedActivity(
                            isFirstExercise = index == 0,
                            currentActivity = currentActivity,
                            currentSeconds = currentSeconds
                        )
                    }
                    ActivityType.TIMELESS_EXERCISE -> {
                        handleTimedActivity(
                            isFirstExercise = index == 0,
                            currentActivity = currentActivity,
                            currentSeconds = 0F
                        )
                    }
                    ActivityType.BREAK -> {
                        handleBreak(
                            currentActivity = currentActivity,
                            currentSeconds = currentSeconds,
                        )
                    }
                }
            }
    }

    private fun handleTimedActivity(
        isFirstExercise: Boolean,
        currentActivity: Activity,
        currentSeconds: Float
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = null,
            readExerciseNameEvent = if (isFirstExercise) {
                postReadExerciseNameEvent(
                    currentActivity.activityId,
                    currentActivity.name
                )
            } else {
                _uiState.value.readExerciseNameEvent
            },
            activityFinishesEvent = postActivityFinishesEvent(
                currentActivity.activityId,
                currentSeconds
            )
        )
    }

    private fun handleBreak(
        currentActivity: Activity,
        currentSeconds: Float,
    ) {
        val list = _uiState.value.activityTypes as MutableList<ActivityType>
        list[_uiState.value.currentDisplayPage] = ActivityType.BREAK
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = null,
            activityTypes = list,
            readExerciseNameEvent = postReadExerciseNameEvent(
                currentActivity.activityId,
                currentActivity.name
            ),
            breakEndsEvent = postBreakFinishesEvent(
                currentActivity.activityId,
                currentSeconds
            ),
        )
    }

    private fun changePageAfterActivityEnds(currentActivity: Activity) {
        var currentPage = _uiState.value.currentDisplayPage
        if (!skippedByUser) {
            index++
        }
        if (currentActivity.activityType == ActivityType.BREAK && !skippedByUser) {
            currentPage++
            changePage(destinationPage = currentPage, false)
        }
    }

    private fun handleUserSkip(currentPage: Int, destinationPage: Int) {
        skippedByUser = true
        val nextIndex = getNextActivityIndexByDestinationPage(currentPage, destinationPage)
        if (nextIndex != null) {
            index = nextIndex
        }
        val currentActivity = trainingWithActivities.activities[index]
        handleSwipeWhenTimerIsPausedEdgeCase(currentActivity)
    }

    private fun trainingFinished(): Boolean {
        if (isTrainingFinished(trainingWithActivities.activities, index)) {
            setTrainingFinishedState(_uiState.value.displayableActivityItemListWithBreakMerged!!.size)
            return true
        }
        return false
    }

    private fun getNextActivityIndexByDestinationPage(
        currentPage: Int,
        destinationPage: Int
    ): Int? {
        val activities = trainingWithActivities.activities
        var newIndex: Int? = index
        if (destinationPage > currentPage) {
            newIndex = newIndex?.plus(
                if (activities.getOrNull(index + 1)?.activityType != ActivityType.BREAK) {
                    1
                } else {
                    2
                }
            )
        } else {
            newIndex = newIndex?.minus(
                if (activities.getOrNull(index - 1)?.activityType != ActivityType.BREAK) {
                    1
                } else {
                    2
                }
            )
        }
        if (newIndex == null || activities.getOrNull(newIndex) == null) {
            return null
        }
        return newIndex
    }

    private suspend fun restorePreviousExerciseOnPreviousPage(page: Int) {
        withContext(Dispatchers.IO) {
            sleep(250)
            val list: MutableList<ActivityType> =
                _uiState.value.activityTypes as MutableList<ActivityType>
            _uiState.value.displayableActivityItemListWithBreakMerged?.get(page)?.exercise?.toActivityType()
                ?.let {
                    list.set(
                        page,
                        it
                    )
                }
            _uiState.value = _uiState.value.copy(activityTypes = list)
        }
    }

    private fun presetCurrentExercise(currentActivity: Activity) {
        if (skippedByUser) {
            presetActivityAfterSkipping()
        }
        setupTimer(currentActivity)
    }

    private fun setupTimer(currentActivity: Activity) {
        if (currentActivity.activityType != ActivityType.TIMELESS_EXERCISE) {
            timer.setDuration(currentActivity.duration)
        } else {
            pauseTimer()
        }
    }

    private fun setTrainingFinishedState(allExercisesCount: Int) {
        val currentTime = Calendar.getInstance()
        val seconds = (currentTime.timeInMillis - startingTimestamp) / 1000
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = null,
            displayableActivityItemListWithBreakMerged = null,
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

    private fun presetActivityAfterSkipping() {
        skippedByUser = false
        pauseTimer()
    }

    private fun getPercentageForPage(index: Int): Float {
        return index.toFloat() / _uiState.value.displayableActivityItemListWithBreakMerged!!.size.toFloat()
    }

    private fun isTrainingFinished(activities: List<Activity>, index: Int): Boolean {
        if (activities.lastIndex == index) {
            return activities[index].activityType == ActivityType.BREAK
        }
        return activities.getOrNull(index + 1) == null
    }

    private fun pauseTimer() {
        isPaused = true
        Log.i(TIMER_LOG_TAG, "Timer is paused.")
        timer.pause()
    }

    private fun startTimer() {
        isPaused = false
        Log.i(TIMER_LOG_TAG, "Timer is resumed.")
        timer.start()
    }

    private fun saveStartingTimeStamp() {
        val startDate = Calendar.getInstance()
        Log.i("Start date", "Started on ${startDate.time}")
        startingTimestamp = startDate.timeInMillis
        startingTimestampSaved = true
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

    private fun handleSwipeWhenTimerIsPausedEdgeCase(currentActivity: Activity) {
        //Timer has to be started if initially it's paused, so it can move to the next exercise.
        if (isPaused && currentActivity.activityType != ActivityType.TIMELESS_EXERCISE) {
            startTimer()
        }
    }

    private fun initializeDisplayableList() {
        _uiState.value =
            _uiState.value.copy(
                displayableActivityItemListWithBreakMerged = trainingWithActivities.activities.toActivityItemsExercisesWithBreaksMerged(),
                activityTypes = trainingWithActivities.activities.toActivityItemsExercisesWithBreaksMerged()
                    .initializeActivityTypes()
            )
    }

    private fun initUiState(): MutableStateFlow<ExecuteTrainingUiState> = MutableStateFlow(
        ExecuteTrainingUiState(
            true,
            null,
            null,
            null,
            0F,
            0F,
            null,
            0,
            null,
            null,
            null,
            null
        )
    )

    private fun List<Activity>.toActivityItemsExercisesWithBreaksMerged(): List<ActivityItemExerciseAndBreakMerged> {
        val activityItemList = mutableListOf<ActivityItemExerciseAndBreakMerged>()
        this.forEachIndexed { index, activity ->
            if (activity.activityType != ActivityType.BREAK) {
                val nextActivityName =
                    if (this.getOrNull(index + 1)?.activityType != ActivityType.BREAK) this.getOrNull(
                        index + 1
                    )?.name ?: "" else
                        this.getOrNull(index + 2)?.name ?: ""
                val breakActivity =
                    if (this.getOrNull(index + 1)?.activityType == ActivityType.BREAK) this.getOrNull(
                        index + 1
                    ) else null
                var breakAfterActivity: DisplayableActivityItem.Break? = null
                if (breakActivity != null) {
                    breakAfterActivity = breakActivity.toActivityItem(
                        nextActivityName,
                        breakActivity.duration.toFloat()
                    ) as DisplayableActivityItem.Break
                }

                activityItemList.add(
                    ActivityItemExerciseAndBreakMerged(
                        activity.toActivityItem(
                            nextActivityName,
                            activity.duration.toFloat() * 1000
                        ),
                        breakAfterActivity
                    )
                )
            }
        }
        return activityItemList
    }

    private fun List<ActivityItemExerciseAndBreakMerged>.initializeActivityTypes(): List<ActivityType> {
        val activityTypes = mutableListOf<ActivityType>()
        this.forEach {
            when (it.exercise) {
                is DisplayableActivityItem.Break -> {}
                is DisplayableActivityItem.Exercise -> activityTypes.add(ActivityType.EXERCISE)
                is DisplayableActivityItem.TimelessExercise -> activityTypes.add(ActivityType.TIMELESS_EXERCISE)
            }
        }
        return activityTypes
    }

    private fun Activity.toActivityItem(
        nextExerciseName: String?,
        currentSeconds: Float
    ): DisplayableActivityItem {
        return when (this.activityType) {
            ActivityType.STRETCH -> DisplayableActivityItem.Exercise(
                this.name,
                nextExerciseName,
                currentSeconds,
                this.duration
            )
            ActivityType.EXERCISE -> DisplayableActivityItem.Exercise(
                this.name,
                nextExerciseName,
                currentSeconds,
                this.duration
            )
            ActivityType.TIMELESS_EXERCISE -> DisplayableActivityItem.TimelessExercise(
                name = this.name,
                nextExerciseName
            )
            ActivityType.BREAK -> DisplayableActivityItem.Break(
                nextExerciseName ?: "",
                currentSeconds,
                this.duration
            )
        }
    }

    companion object {
        private const val TIMER_LOG_TAG = "TIMER"
    }
}



