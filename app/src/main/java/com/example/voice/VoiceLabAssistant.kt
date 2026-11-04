package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

private const val TAG = "VoiceLabAssistant"

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

class VoiceLabAssistant(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private var speechRecognizer: SpeechRecognizer? = null

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _audioWaveform = MutableStateFlow(listOf(0.2f, 0.4f, 0.6f, 0.3f, 0.5f, 0.7f, 0.4f))
    val audioWaveform: StateFlow<List<Float>> = _audioWaveform.asStateFlow()

    var onSpeechRecognizedCallback: ((String) -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
        initRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "TTS Language not supported")
            } else {
                isTtsReady = true
                tts?.setPitch(1.05f)
                tts?.setSpeechRate(1.0f)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _voiceState.value = VoiceState.SPEAKING
                    }
                    override fun onDone(utteranceId: String?) {
                        _voiceState.value = VoiceState.IDLE
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _voiceState.value = VoiceState.IDLE
                    }
                })
            }
        } else {
            Log.e(TAG, "TTS Initialization failed")
        }
    }

    private fun initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _voiceState.value = VoiceState.LISTENING
                    }
                    override fun onBeginningOfSpeech() {
                        _voiceState.value = VoiceState.LISTENING
                    }
                    override fun onRmsChanged(rmsdB: Float) {
                        val normalized = (rmsdB / 10f).coerceIn(0.1f, 1.0f)
                        _audioWaveform.value = listOf(
                            normalized * 0.5f,
                            normalized * 0.8f,
                            normalized,
                            normalized * 0.9f,
                            normalized * 0.7f,
                            normalized * 0.4f
                        )
                    }
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        _voiceState.value = VoiceState.PROCESSING
                    }
                    override fun onError(error: Int) {
                        Log.w(TAG, "SpeechRecognizer error: $error")
                        _voiceState.value = VoiceState.IDLE
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull().orEmpty()
                        _recognizedText.value = text
                        _voiceState.value = VoiceState.IDLE
                        if (text.isNotBlank()) {
                            onSpeechRecognizedCallback?.invoke(text)
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull().orEmpty()
                        if (text.isNotBlank()) {
                            _recognizedText.value = text
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            initRecognizer()
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        try {
            speechRecognizer?.startListening(intent)
            _voiceState.value = VoiceState.LISTENING
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start listening", e)
            _voiceState.value = VoiceState.ERROR
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _voiceState.value = VoiceState.IDLE
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop listening", e)
        }
    }

    fun speak(text: String, onFinished: (() -> Unit)? = null) {
        if (!isTtsReady || text.isBlank()) {
            onFinished?.invoke()
            return
        }
        stopSpeaking()
        val cleanText = text.replace(Regex("[#*_`\\[\\]()]"), "")
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_LAB_AUDIO")
    }

    fun stopSpeaking() {
        tts?.stop()
        _voiceState.value = VoiceState.IDLE
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying VoiceLabAssistant", e)
        }
    }
}
