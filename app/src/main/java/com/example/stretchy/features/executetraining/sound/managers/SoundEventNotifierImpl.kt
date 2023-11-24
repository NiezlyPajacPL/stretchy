package com.example.stretchy.features.executetraining.sound.managers

import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.sound.data.TrainingEvent
import com.example.stretchy.features.executetraining.sound.data.SoundEvent
import com.example.stretchy.repository.Activity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SoundEventNotifierImpl : SoundEventNotifier {
    private val soundsEventFlow: MutableSharedFlow<SoundEvent> = MutableSharedFlow(replay = 0)
    private val readExerciseNameEvent: MutableSharedFlow<SoundEvent.ReadExerciseNameEvent> =
        MutableSharedFlow(replay = 0)
    override val soundEventFlow: Flow<SoundEvent> = merge(soundsEventFlow, readExerciseNameFlow())

    private var currentActivity: Activity? = null
    private var lastActivityNamePosted: String? = null

    @OptIn(FlowPreview::class)
    fun readExerciseNameFlow(): Flow<SoundEvent> = readExerciseNameEvent
        .debounce(600)

    override suspend fun notifyEvent(event: TrainingEvent) {
        when (event) {
            TrainingEvent.ActivityEndsIn3Sec -> onActivityEndsIn3Sec()
            is TrainingEvent.NewActivityStarts -> onActivityUpdate(
                event.newActivity,
                event.isFirstExercise,
                event.nextExerciseName,
                event.isSwipedByUser
            )

            TrainingEvent.TrainingEnded -> onTrainingEnd()
            TrainingEvent.BreakEnds -> onBreakEnd()
        }
    }

    private suspend fun onActivityUpdate(
        updatedActivity: Activity,
        isFirstExercise: Boolean,
        nextExerciseName: String?,
        swipedByUser: Boolean
    ) {
        if (swipedByUser) {
            onActivitySwiped()
        }
        if (this.currentActivity != updatedActivity) {
            currentActivity = updatedActivity
            if (isFirstExercise) {
                readExerciseNameEvent.emit(SoundEvent.ReadExerciseNameEvent(updatedActivity.name))
            } else if (shouldPostReadExerciseNameEvent(nextExerciseName)) {
                lastActivityNamePosted = nextExerciseName
                readExerciseNameEvent.emit(SoundEvent.ReadExerciseNameEvent(nextExerciseName!!))
            }
        }
    }

    private suspend fun onActivityEndsIn3Sec() {
        soundsEventFlow.emit(SoundEvent.ActivityEndedEvent())
    }

    private suspend fun onBreakEnd(){
        soundsEventFlow.emit(SoundEvent.BreakEndedEvent())
    }

    private suspend fun onActivitySwiped() {
        if (lastActivityNamePosted != currentActivity?.name) {
            lastActivityNamePosted = currentActivity?.name
            readExerciseNameEvent.emit(
                SoundEvent.ReadExerciseNameEvent(currentActivity?.name!!)
            )
        }
    }

    private suspend fun onTrainingEnd() {
        soundsEventFlow.emit(SoundEvent.TrainingCompletedEvent())
    }

    private fun shouldPostReadExerciseNameEvent(nextExerciseName: String?): Boolean =
        currentActivity?.activityType == ActivityType.BREAK && nextExerciseName != null && lastActivityNamePosted != nextExerciseName
}