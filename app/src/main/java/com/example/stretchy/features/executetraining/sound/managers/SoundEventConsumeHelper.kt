package com.example.stretchy.features.executetraining.sound.managers

import android.content.Context
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.sound.SoundType
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.features.executetraining.sound.data.SoundEvent
import com.example.stretchy.features.executetraining.sound.data.SoundTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun consumeSoundEvents(
    soundEvent: SoundEvent?,
    coroutineScope: CoroutineScope,
    soundPlayer: SoundPlayer,
    context: Context
) {
    when (soundEvent) {
        is SoundEvent.ActivityEndedEvent -> consumeActivityFinishes(
            soundEvent,
            coroutineScope,
            soundPlayer
        )
        is SoundEvent.BreakEndedEvent -> consumeBreakEnds(
            soundEvent,
            coroutineScope,
            soundPlayer
        )
        is SoundEvent.ReadExerciseNameEvent -> consumeReadExerciseName(
            soundEvent,
            coroutineScope,
            soundPlayer
        )
        is SoundEvent.TrainingCompletedEvent -> consumeTrainingEnds(
            soundEvent,
            coroutineScope,
            soundPlayer,
            context
        )
        is SoundEvent.SkipSounds -> {
            consumeSkipSounds(soundPlayer)
        }
        null -> {}
    }
}

private fun consumeTrainingEnds(
    trainingCompletedEvent: SoundEvent.TrainingCompletedEvent,
    coroutineScope: CoroutineScope,
    soundPlayer: SoundPlayer,
    context: Context
) {
    trainingCompletedEvent.consume()?.let {
        coroutineScope.launch {
            soundPlayer.queueSound(SoundType.Speech(context.resources.getString(R.string.training_finished)))
        }
    }
}

private fun consumeSkipSounds(soundPlayer: SoundPlayer) {
    soundPlayer.stopSound()
}

private fun consumeReadExerciseName(
    readExerciseNameEvent: SoundEvent.ReadExerciseNameEvent,
    coroutineScope: CoroutineScope,
    soundPlayer: SoundPlayer
) {
    val name = readExerciseNameEvent.name
    if (!readExerciseNameEvent.isConsumed) {
        readExerciseNameEvent.consume().let {
            coroutineScope.launch {
                soundPlayer.queueSound(SoundType.Speech(name))
            }
        }
    }
}

private fun consumeActivityFinishes(
    activityFinishesEvent: SoundEvent?,
    coroutineScope: CoroutineScope,
    soundPlayer: SoundPlayer
) {
    activityFinishesEvent?.consume()?.let {
        coroutineScope.launch {
            soundPlayer.queueSound(SoundType.Sound(SoundTrack.EXERCISE_ENDING))
        }
    }
}

private fun consumeBreakEnds(
    breakEndsEvent: SoundEvent?,
    coroutineScope: CoroutineScope,
    soundPlayer: SoundPlayer
) {
    breakEndsEvent?.consume()?.let {
        coroutineScope.launch {
            soundPlayer.queueSound(SoundType.Sound(SoundTrack.BREAK_ENDED))
        }
    }
}