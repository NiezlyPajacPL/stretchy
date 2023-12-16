package com.example.stretchy.features.executetraining.sound.managers

import com.example.stretchy.features.executetraining.sound.data.TrainingEvent
import com.example.stretchy.features.executetraining.sound.data.SoundEvent
import kotlinx.coroutines.flow.Flow

interface SoundEventNotifier {
    val soundEventFlow: Flow<SoundEvent>

    suspend fun notifyEvent(event: TrainingEvent)
}
