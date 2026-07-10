package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

class FlowViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ProductivityRepository(
        database.taskDao(),
        database.focusSessionDao(),
        database.userPreferenceDao()
    )

    // UI Navigation State
    private val _currentTab = MutableStateFlow("Dashboard")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _showInfoDialog = MutableStateFlow(false)
    val showInfoDialog: StateFlow<Boolean> = _showInfoDialog.asStateFlow()

    fun setShowInfoDialog(show: Boolean) {
        _showInfoDialog.value = show
    }

    // DB States
    val allTasks = repository.allTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val activeTasks = repository.activeTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allSessions = repository.allSessions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val userPreferences = repository.userPreferences.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    // Focus Timer States
    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(1476) // 24:36 initially, just like screenshot!
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _totalSessionSeconds = MutableStateFlow(1500) // 25-min default
    val totalSessionSeconds: StateFlow<Int> = _totalSessionSeconds.asStateFlow()

    private val _selectedTaskForTimer = MutableStateFlow<TaskEntity?>(null)
    val selectedTaskForTimer: StateFlow<TaskEntity?> = _selectedTaskForTimer.asStateFlow()

    private var timerJob: Job? = null

    // Chat / Flow AI States
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _flowAiInsight = MutableStateFlow(
        "Short breaks every 25 minutes increase sustained output by 16%."
    )
    val flowAiInsight: StateFlow<String> = _flowAiInsight.asStateFlow()

    init {
        viewModelScope.launch {
            // Seeding logic on first launch if empty
            repository.userPreferences.first().let { prefs ->
                if (prefs == null) {
                    repository.seedMockData()
                }
            }
        }
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    // --- Timer Actions ---
    fun toggleTimer() {
        if (_timerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _timerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0) {
                delay(1000L)
                _timerSecondsRemaining.value -= 1
            }
            onTimerCompleted()
        }
    }

    fun pauseTimer() {
        _timerRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        _timerSecondsRemaining.value = _totalSessionSeconds.value
    }

    fun setTimerDuration(minutes: Int) {
        pauseTimer()
        val seconds = minutes * 60
        _totalSessionSeconds.value = seconds
        _timerSecondsRemaining.value = seconds
    }

    fun selectTaskForFocus(task: TaskEntity?) {
        _selectedTaskForTimer.value = task
    }

    private suspend fun onTimerCompleted() {
        _timerRunning.value = false
        timerJob?.cancel()
        timerJob = null

        // Save session in Room
        val session = FocusSessionEntity(
            startTime = System.currentTimeMillis() - (_totalSessionSeconds.value * 1000),
            durationSeconds = _totalSessionSeconds.value.toLong(),
            category = _selectedTaskForTimer.value?.category ?: "Study",
            completed = true
        )
        repository.insertSession(session)

        // Increment task actualMinutes if a task was active
        _selectedTaskForTimer.value?.let { task ->
            val minutesSpent = _totalSessionSeconds.value / 60
            val updatedTask = task.copy(
                actualMinutes = task.actualMinutes + minutesSpent,
                isCompleted = if (task.actualMinutes + minutesSpent >= task.estimatedMinutes) true else task.isCompleted
            )
            repository.updateTask(updatedTask)
        }

        // Increment streak if last active day is not today
        val prefs = repository.getPreferences()
        if (prefs != null) {
            val todayStr = "2026-07-09" // Static demo date matching system meta
            if (prefs.lastActiveDate != todayStr) {
                val updatedPrefs = prefs.copy(
                    streakDays = prefs.streakDays + 1,
                    lastActiveDate = todayStr
                )
                repository.updatePreferences(updatedPrefs)
            }
        }

        // Set to default duration and refresh
        _timerSecondsRemaining.value = _totalSessionSeconds.value

        // Trigger an AI response congratulating the user
        generateAiWelcomeOrInsight()
    }

    // --- Database Actions ---
    fun addTask(title: String, priority: String, category: String, estimatedMinutes: Int) {
        viewModelScope.launch {
            val newTask = TaskEntity(
                title = title,
                priority = priority,
                isCompleted = false,
                category = category,
                dueDate = System.currentTimeMillis() + 4 * 60 * 60 * 1000L, // 4 hours from now
                estimatedMinutes = estimatedMinutes
            )
            repository.insertTask(newTask)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    fun updatePreferences(name: String, aiPersonality: String, focusSound: String, dailyGoal: Int) {
        viewModelScope.launch {
            val current = repository.getPreferences() ?: UserPreferenceEntity()
            val updated = current.copy(
                userName = name,
                aiPersonality = aiPersonality,
                focusSound = focusSound,
                dailyGoalCount = dailyGoal
            )
            repository.updatePreferences(updated)
        }
    }

    fun resetDatabase() {
        viewModelScope.launch {
            repository.clearAll()
            repository.seedMockData()
            _timerSecondsRemaining.value = 1476
            _totalSessionSeconds.value = 1500
            _chatHistory.value = emptyList()
        }
    }

    // --- Flow AI Integration ---
    fun sendChatMessage(messageContent: String) {
        if (messageContent.isBlank()) return

        val userMsg = ChatMessage(sender = "USER", content = messageContent)
        _chatHistory.value = _chatHistory.value + userMsg

        _isAiLoading.value = true
        viewModelScope.launch {
            try {
                // Fetch context
                val tasks = activeTasks.value
                val prefs = userPreferences.value ?: UserPreferenceEntity()
                val sessions = allSessions.value

                // Compile system context instruction
                val systemContextPrompt = """
                    You are 'Flow AI', an emotionally intelligent, high-productivity companion for ${prefs.userName}.
                    You specialize in behavioral psychology, cognitive science, and study/work planning.
                    The current time is 2026-07-09.
                    
                    Here is ${prefs.userName}'s live productivity status:
                    - Current study streak: ${prefs.streakDays} days
                    - Focus mode AI personality set to: ${prefs.aiPersonality}
                    - Soundscape chosen: ${prefs.focusSound}
                    - Pending high-importance tasks: ${tasks.filter { it.priority == "HIGH" }.joinToString { it.title }}
                    - Number of focus sessions logged this week: ${sessions.size} sessions
                    
                    Your tone MUST match ${prefs.aiPersonality}:
                    - If "Analytical Advisor": Professional, data-driven, highlighting break frequencies and trends.
                    - If "Strict Mentor": Disciplined, direct, challenging them to limit avoidance behaviors and stop procrastinating.
                    - If "Encouraging Coach": Highly empathetic, positive, focusing on micro-steps and preventing burnout.
                    
                    Provide deep, premium guidance. Avoid dry lists or bullet summaries. Speak with conversational elegance. Limit replies to 2 coherent paragraphs.
                """.trimIndent()

                // Compile conversation history
                val conversationList = mutableListOf<Content>()
                
                // Append chat logs
                _chatHistory.value.takeLast(10).forEach { chat ->
                    conversationList.add(Content(parts = listOf(Part(text = "${chat.sender}: ${chat.content}"))))
                }

                val apiKey = BuildConfig.GEMINI_API_KEY
                val request = GenerateContentRequest(
                    contents = conversationList,
                    systemInstruction = Content(parts = listOf(Part(text = systemContextPrompt)))
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }

                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "I apologize, Jordan. I could not connect to my analytical core. Let's refocus on our current high-priority tasks."

                _chatHistory.value = _chatHistory.value + ChatMessage(sender = "AI", content = aiText)

                // Also update the short insight card
                val shortInsight = if (aiText.length > 80) aiText.take(80) + "..." else aiText
                _flowAiInsight.value = shortInsight

            } catch (e: Exception) {
                _chatHistory.value = _chatHistory.value + ChatMessage(
                    sender = "AI",
                    content = "I encountered a minor network latency, Jordan. Let's maintain our focus flow. Your current streak is safe."
                )
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    private fun generateAiWelcomeOrInsight() {
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val prefs = userPreferences.value ?: UserPreferenceEntity()
                val prompt = "Generate a single sentence of highly motivating and scientifically backed productivity advice for Jordan (who has a ${prefs.streakDays}-day streak). Keep it under 15 words."
                
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }
                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!aiText.isNullOrBlank()) {
                    _flowAiInsight.value = aiText.trim().replace("\"", "")
                }
            } catch (e: Exception) {
                // Fallback
                _flowAiInsight.value = "Short breaks every 25 minutes increase sustained output by 16%."
            }
        }
    }
}
