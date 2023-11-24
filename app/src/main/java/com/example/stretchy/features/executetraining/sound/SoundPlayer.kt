package com.example.stretchy.features.executetraining.sound

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.sound.data.SoundTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*


class SoundPlayer(
    val context: Context,
    locale: Locale = Locale.ENGLISH,
    pitch: Float = 1f,
    speechSpeed: Float = 1.1f,
) : SoundFlowEvent {
    private lateinit var textToSpeech: TextToSpeech
    private var isInitializedFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val players: MutableList<MediaPlayer> = mutableListOf()
    override val soundFlow: MutableSharedFlow<SoundFlow> = MutableSharedFlow(replay = 0)
    val soundList = mutableListOf<SoundType>()

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = textToSpeech.setLanguage(locale)
                if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported")
                } else {
                    val voice = Voice(
                        "en-us-x-tpc-local",
                        Locale("en", "US"),
                        1000,
                        0,
                        true,
                        emptySet()
                    )
                    textToSpeech.voice = voice
                    textToSpeech.setPitch(pitch)
                    textToSpeech.setSpeechRate(speechSpeed)


                    GlobalScope.launch {
                        isInitializedFlow.emit(true)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        soundFlow.collect {
                            Log.e("soundflow",it.toString())
                        }
                    }
                }
            }
        }

    }

    fun queueSound(sound: SoundType) {
        when (sound) {
            is SoundType.Sound -> {
                soundList.add(SoundType.Sound(sound.soundTrack))
                if (soundList.size == 1) {
                    playSound(sound.soundTrack)
                }
            }
            is SoundType.Speech -> {
                if(soundList.getOrNull(0) is SoundType.Speech){
                    soundList.removeAt(0)
                    textToSpeech.stop()
                }else if(soundList.getOrNull(0) == SoundType.Sound(SoundTrack.EXERCISE_ENDING)){
                    soundList.removeAt(0)
                    stopSound()
                }
                soundList.add(SoundType.Speech(sound.text))
                if (soundList.size == 1) {
                    CoroutineScope(Dispatchers.IO).launch {
                        sound.text?.let { say(it) }
                    }
                }
            }
        }

    }

    fun stopSound() {
        for (player in players) {
            if (player.isPlaying) {
                player.stop()
                player.reset()
                player.release()
            }
        }
        players.clear()
    }


    private suspend fun say(text: String) {
        isInitializedFlow
            .filter { it }
            .first()

        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_FLUSH, null, "UniqueID"
        )
        textToSpeech.setOnUtteranceProgressListener(object :
            UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    soundFlow.emit(SoundFlow.Playing(SoundType.Speech(text)))
                }
            }

            override fun onDone(utteranceId: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    soundFlow.emit(SoundFlow.Done(SoundType.Speech(text)))
                    soundList.remove(SoundType.Speech(text))
                    playNextQueuedSound()
                }
            }

            override fun onError(utteranceId: String?) {
                CoroutineScope(Dispatchers.IO).launch {
                    soundFlow.emit(SoundFlow.Error(SoundType.Speech(text)))
                }
            }

        })
    }

    private fun playSound(soundTrack: SoundTrack) {
        val mp = MediaPlayer.create(context, soundTrack.toRawWav()).apply {
            setOnCompletionListener { mp ->
                mp.reset()
                mp.release()
                CoroutineScope(Dispatchers.IO).launch {
                    soundFlow.emit(SoundFlow.Done(SoundType.Sound(soundTrack)))
                }
                soundList.remove(SoundType.Sound(soundTrack))
                players.remove(mp)
                playNextQueuedSound()
            }
            setOnErrorListener { _, _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    soundFlow.emit(SoundFlow.Error(SoundType.Sound(soundTrack)))
                }
                true
            }
        }

        players.add(mp)
        mp.start()
        CoroutineScope(Dispatchers.IO).launch {
            soundFlow.emit(SoundFlow.Playing(SoundType.Sound(soundTrack)))
        }
    }

    private fun SoundTrack.toRawWav(): Int {
        return when (this) {
            SoundTrack.BREAK_ENDED -> R.raw.break_ended
            SoundTrack.EXERCISE_ENDING -> R.raw.exercise_ending
        }
    }

    fun playNextQueuedSound() {
        when (val sound = soundList.getOrNull(0)) {
            is SoundType.Sound -> {
                playSound(sound.soundTrack)
            }
            is SoundType.Speech -> {
                CoroutineScope(Dispatchers.IO).launch {
                    sound.text?.let { say(it) }
                }
            }
            null -> {}
        }
    }

    companion object {
        const val TAG = "Speaker"
        val LOCALE_PL = Locale("pl_PL")

        //todo nice voice lines
        //en-us-x-sfg-network
        //en-us-x-iob-local
        //en-us-x-tpc-local
        //en-us-x-tpd-network
        //en-us-x-iom-local
        //en-us-x-iom-network
    }

    //nice
    //en-us-x-iob-local - female
    //en-us-x-tpc-local - calm female
    //en-us-x-iom-local - calm male
}

