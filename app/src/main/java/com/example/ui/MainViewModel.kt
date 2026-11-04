package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiBioscienceService
import com.example.data.model.BiotechLocationItem
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.LiteratureItem
import com.example.data.model.Project
import com.example.data.model.Protocol
import com.example.data.model.SequenceAnalysisResult
import com.example.data.repository.BioscienceRepository
import com.example.voice.VoiceLabAssistant
import com.example.voice.VoiceState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BioscienceRepository(application)
    val aiService = GeminiBioscienceService()
    val voiceAssistant = VoiceLabAssistant(application)

    // Current Navigation Screen
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    // Auth state check
    val currentUser = Firebase.auth.currentUser

    // 1. Observe Firestore Projects
    val projectsState: StateFlow<UiState<List<Project>>> = repository.observeProjects()
        .map<List<Project>, UiState<List<Project>>> { UiState.Success(it) }
        .catch { emit(UiState.Error(it.message ?: "Failed to observe projects")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    // 2. Observe Firestore Protocols
    val protocolsState: StateFlow<UiState<List<Protocol>>> = repository.observeProtocols()
        .map<List<Protocol>, UiState<List<Protocol>>> { UiState.Success(it) }
        .catch { emit(UiState.Error(it.message ?: "Failed to observe protocols")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    // 3. Observe Firestore Chat Sessions
    val chatSessionsState: StateFlow<UiState<List<ChatSession>>> = repository.observeChatSessions()
        .map<List<ChatSession>, UiState<List<ChatSession>>> { UiState.Success(it) }
        .catch { emit(UiState.Error(it.message ?: "Failed to observe chat sessions")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    // --- Genomics State ---
    private val _sequenceInput = MutableStateFlow("ATGCGATCGATCGATCGATCGATCGATCGAATCGATCGATCGCGATCGATCGATCGTAGCTAGCTAGCTA")
    val sequenceInput: StateFlow<String> = _sequenceInput.asStateFlow()

    private val _sequenceAnalysis = MutableStateFlow<SequenceAnalysisResult?>(null)
    val sequenceAnalysis: StateFlow<SequenceAnalysisResult?> = _sequenceAnalysis.asStateFlow()

    private val _highThinkingState = MutableStateFlow<UiState<Pair<String, String>>>(UiState.Success(Pair("", "")))
    val highThinkingState: StateFlow<UiState<Pair<String, String>>> = _highThinkingState.asStateFlow()

    fun updateSequence(seq: String) {
        _sequenceInput.value = seq
    }

    fun analyzeCurrentSequence() {
        viewModelScope.launch {
            val result = aiService.analyzeDnaSequence(_sequenceInput.value)
            _sequenceAnalysis.value = result
        }
    }

    fun runHighThinkingArgonAnalysis(variantNote: String, diseaseContext: String) {
        viewModelScope.launch {
            _highThinkingState.value = UiState.Loading
            try {
                val pair = aiService.analyzeGenomicVariantDeep(_sequenceInput.value, variantNote, diseaseContext)
                _highThinkingState.value = UiState.Success(pair)
            } catch (e: Exception) {
                _highThinkingState.value = UiState.Error(e.localizedMessage ?: "High thinking reasoning failed")
            }
        }
    }

    // --- Chatbot State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome",
                sender = "model",
                text = "Welcome to BioNexus. I am initialized with Molecular Genetics and Structural Biology reasoning suites. How can I assist your laboratory research today?",
                modelTag = "gemini-3.8-flash"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _selectedRole = MutableStateFlow("Molecular Geneticist")
    val selectedRole: StateFlow<String> = _selectedRole.asStateFlow()

    private val _isChatGenerating = MutableStateFlow(false)
    val isChatGenerating: StateFlow<Boolean> = _isChatGenerating.asStateFlow()

    private val _useHighThinkingInChat = MutableStateFlow(false)
    val useHighThinkingInChat: StateFlow<Boolean> = _useHighThinkingInChat.asStateFlow()

    fun setChatRole(role: String) {
        _selectedRole.value = role
    }

    fun toggleHighThinking(enabled: Boolean) {
        _useHighThinkingInChat.value = enabled
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank() || _isChatGenerating.value) return

        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "user",
            text = text,
            modelTag = "user"
        )
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(userMsg)
        _chatMessages.value = currentList
        _isChatGenerating.value = true

        val roleInstruction = when (_selectedRole.value) {
            "Molecular Geneticist" -> "Specialist in CRISPR-Cas9, genetic mutations, transcription factor binding, and epigenetics."
            "Structural Biologist" -> "Specialist in protein folding, cryo-EM resolution, ligand docking pockets, and allosteric kinetics."
            "Bioinformatics Lead" -> "Specialist in Next-Gen Sequencing (NGS), FASTA/FASTQ pipelines, variant calling, and Python Biopython algorithms."
            "Biosafety Officer" -> "Specialist in BSL-1 to BSL-4 biosafety containment, OSHA biohazard SOPs, PPE protocols, and autoclave validation."
            else -> "Elite Bioscience Consultant."
        }

        val historyPairs = currentList.map { Pair(it.sender, it.text) }
        val modelTag = if (_useHighThinkingInChat.value) "gemini-4-argon (Thinking HIGH)" else "gemini-3.8-flash"

        viewModelScope.launch {
            val responseText = StringBuilder()
            val modelMsgId = (System.currentTimeMillis() + 1).toString()
            val initialModelMsg = ChatMessage(
                id = modelMsgId,
                sender = "model",
                text = "",
                modelTag = modelTag
            )
            val updatedWithPlaceholder = _chatMessages.value.toMutableList()
            updatedWithPlaceholder.add(initialModelMsg)
            _chatMessages.value = updatedWithPlaceholder

            try {
                aiService.streamChatResponse(
                    history = historyPairs,
                    userMessage = text,
                    rolePrompt = roleInstruction,
                    useHighThinking = _useHighThinkingInChat.value
                ).collect { chunk ->
                    responseText.append(chunk)
                    val activeList = _chatMessages.value.toMutableList()
                    val index = activeList.indexOfFirst { it.id == modelMsgId }
                    if (index != -1) {
                        activeList[index] = activeList[index].copy(text = responseText.toString())
                        _chatMessages.value = activeList
                    }
                }

                // Save session in Firestore
                repository.saveChatSession(
                    ChatSession(
                        userId = currentUser?.uid.orEmpty(),
                        title = text.take(40),
                        modelUsed = modelTag,
                        role = _selectedRole.value,
                        lastMessage = responseText.toString().take(120)
                    )
                )
            } catch (e: Exception) {
                val activeList = _chatMessages.value.toMutableList()
                val index = activeList.indexOfFirst { it.id == modelMsgId }
                if (index != -1) {
                    activeList[index] = activeList[index].copy(text = "Inference Error: ${e.localizedMessage}")
                    _chatMessages.value = activeList
                }
            } finally {
                _isChatGenerating.value = false
            }
        }
    }

    // --- Search & Grounding State ---
    private val _literatureQuery = MutableStateFlow("CRISPR prime editing cancer therapeutics")
    val literatureQuery: StateFlow<String> = _literatureQuery.asStateFlow()

    private val _literatureResults = MutableStateFlow<UiState<List<LiteratureItem>>>(UiState.Loading)
    val literatureResults: StateFlow<UiState<List<LiteratureItem>>> = _literatureResults.asStateFlow()

    private val _biotechCity = MutableStateFlow("Boston")
    val biotechCity: StateFlow<String> = _biotechCity.asStateFlow()

    private val _biotechLocations = MutableStateFlow<UiState<List<BiotechLocationItem>>>(UiState.Loading)
    val biotechLocations: StateFlow<UiState<List<BiotechLocationItem>>> = _biotechLocations.asStateFlow()

    fun searchLiterature(query: String) {
        _literatureQuery.value = query
        viewModelScope.launch {
            _literatureResults.value = UiState.Loading
            val items = aiService.searchScientificLiterature(query)
            _literatureResults.value = UiState.Success(items)
        }
    }

    fun searchBiotechLocations(city: String, type: String = "All") {
        _biotechCity.value = city
        viewModelScope.launch {
            _biotechLocations.value = UiState.Loading
            val list = aiService.searchBiotechHubsAndLabs(city, type)
            _biotechLocations.value = UiState.Success(list)
        }
    }

    // --- Protocol Management ---
    fun saveNewProtocol(name: String, category: String, reagents: List<String>, steps: List<String>, safetyNotes: String) {
        viewModelScope.launch {
            repository.saveProtocol(
                Protocol(
                    userId = currentUser?.uid.orEmpty(),
                    name = name,
                    category = category,
                    reagents = reagents,
                    steps = steps,
                    safetyNotes = safetyNotes,
                    status = "Active"
                )
            )
        }
    }

    fun deleteProtocol(id: String) {
        viewModelScope.launch {
            repository.deleteProtocol(id)
        }
    }

    // --- Project Management ---
    fun saveNewProject(title: String, category: String, summary: String, content: String, tags: List<String>) {
        viewModelScope.launch {
            repository.saveProject(
                Project(
                    userId = currentUser?.uid.orEmpty(),
                    title = title,
                    category = category,
                    summary = summary,
                    content = content,
                    tags = tags
                )
            )
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }

    // --- Voice Live & TTS ---
    private val _liveAudioTranscript = MutableStateFlow("")
    val liveAudioTranscript: StateFlow<String> = _liveAudioTranscript.asStateFlow()

    private val _liveAudioResponse = MutableStateFlow("Tap the microphone or say 'Hey BioNexus' to begin hands-free lab assistance.")
    val liveAudioResponse: StateFlow<String> = _liveAudioResponse.asStateFlow()

    init {
        // Initial defaults
        analyzeCurrentSequence()
        searchLiterature("CRISPR prime editing cancer therapeutics")
        searchBiotechLocations("Boston")

        voiceAssistant.onSpeechRecognizedCallback = { transcript ->
            handleLiveVoiceQuery(transcript)
        }
    }

    fun handleLiveVoiceQuery(query: String) {
        _liveAudioTranscript.value = query
        viewModelScope.launch {
            _liveAudioResponse.value = "Analyzing lab observation with gemini-3.8-live..."
            val answer = aiService.converseLiveAudio(query)
            _liveAudioResponse.value = answer
            // Speak response aloud via TTS
            voiceAssistant.speak(answer)
        }
    }

    fun speakProtocolStep(stepText: String) {
        voiceAssistant.speak(stepText)
    }

    override fun onCleared() {
        super.onCleared()
        voiceAssistant.destroy()
    }
}
