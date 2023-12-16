package com.example.stretchy.features.executetraining.sound

import com.example.stretchy.features.executetraining.sound.data.SoundTrack
import kotlinx.coroutines.flow.Flow

interface SoundPlaybackStateEvent {
    val soundPlaybackState: Flow<SoundPlaybackState>?
}

sealed class SoundPlaybackState(open val soundType: SoundType) {

    data class Playing(override val soundType: SoundType) : SoundPlaybackState(soundType)

    data class Done(override val soundType: SoundType) : SoundPlaybackState(soundType)

    data class Error(override val soundType: SoundType) : SoundPlaybackState(soundType)
}

sealed class SoundType {
    data class Speech(val text: String?) : SoundType()

    data class Sound(val soundTrack: SoundTrack) : SoundType()
}