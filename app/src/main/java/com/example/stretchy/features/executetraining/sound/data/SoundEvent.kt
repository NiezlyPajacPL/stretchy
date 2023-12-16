package com.example.stretchy.features.executetraining.sound.data

import com.example.stretchy.common.OneTimeEvent

sealed class SoundEvent(override var value: Unit? = Unit) : OneTimeEvent<Unit>() {
    class TrainingCompletedEvent : SoundEvent()

    class ActivityEndedEvent : SoundEvent()

    class BreakEndedEvent : SoundEvent()

    class SkipSounds : SoundEvent()

    data class ReadExerciseNameEvent(var name: String) : SoundEvent()

}
