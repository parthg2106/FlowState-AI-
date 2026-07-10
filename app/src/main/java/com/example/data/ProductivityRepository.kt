package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class ProductivityRepository(
    private val taskDao: TaskDao,
    private val focusSessionDao: FocusSessionDao,
    private val userPreferenceDao: UserPreferenceDao
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasksFlow()
    val activeTasks: Flow<List<TaskEntity>> = taskDao.getActiveTasksFlow()
    val allSessions: Flow<List<FocusSessionEntity>> = focusSessionDao.getAllSessionsFlow()
    val userPreferences: Flow<UserPreferenceEntity?> = userPreferenceDao.getPreferencesFlow()

    suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun deleteTask(id: Long) = taskDao.deleteTaskId(id)

    suspend fun insertSession(session: FocusSessionEntity) = focusSessionDao.insertSession(session)
    suspend fun getPreferences(): UserPreferenceEntity? = userPreferenceDao.getPreferences()
    suspend fun updatePreferences(prefs: UserPreferenceEntity) = userPreferenceDao.updatePreferences(prefs)
    suspend fun savePreferences(prefs: UserPreferenceEntity) = userPreferenceDao.insertPreferences(prefs)

    suspend fun clearAll() {
        taskDao.clearAllTasks()
        focusSessionDao.clearAllSessions()
    }

    suspend fun seedMockData() {
        // Clear existing
        clearAll()

        // Seed Preferences
        val initialPrefs = UserPreferenceEntity(
            id = 1,
            userName = "Jordan Smith",
            streakDays = 7,
            aiPersonality = "Analytical Advisor",
            focusSound = "None",
            dailyGoalCount = 6,
            lastActiveDate = "2026-07-09"
        )
        userPreferenceDao.insertPreferences(initialPrefs)

        // Seed Tasks
        val now = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000L
        val oneDay = 24 * oneHour

        val tasks = listOf(
            TaskEntity(
                title = "Complete linear algebra problem set",
                priority = "HIGH",
                isCompleted = false,
                category = "Study",
                dueDate = now + 4 * oneHour,
                estimatedMinutes = 45,
                actualMinutes = 0
            ),
            TaskEntity(
                title = "Review presentation slides for Thursday",
                priority = "MEDIUM",
                isCompleted = true,
                category = "Work",
                dueDate = now - 2 * oneHour,
                estimatedMinutes = 30,
                actualMinutes = 30,
                completedAt = now - 1 * oneHour
            ),
            TaskEntity(
                title = "Reply to professor's email",
                priority = "LOW",
                isCompleted = true,
                category = "Study",
                dueDate = now - 5 * oneHour,
                estimatedMinutes = 15,
                actualMinutes = 10,
                completedAt = now - 4 * oneHour
            ),
            TaskEntity(
                title = "Draft project execution proposal",
                priority = "MEDIUM",
                isCompleted = false,
                category = "Work",
                dueDate = now + 24 * oneHour,
                estimatedMinutes = 60,
                actualMinutes = 0
            ),
            TaskEntity(
                title = "Prepare biology exam review notes",
                priority = "HIGH",
                isCompleted = false,
                category = "Study",
                dueDate = now + 48 * oneHour,
                estimatedMinutes = 90,
                actualMinutes = 0
            ),
            TaskEntity(
                title = "Review study materials for machine learning",
                priority = "HIGH",
                isCompleted = true,
                category = "Study",
                dueDate = now - 1 * oneDay,
                estimatedMinutes = 50,
                actualMinutes = 60,
                completedAt = now - 1 * oneDay + 2 * oneHour
            ),
            TaskEntity(
                title = "Organize digital filing cabinet",
                priority = "LOW",
                isCompleted = true,
                category = "Personal",
                dueDate = now - 2 * oneDay,
                estimatedMinutes = 30,
                actualMinutes = 40,
                completedAt = now - 2 * oneDay + 5 * oneHour
            )
        )

        for (task in tasks) {
            taskDao.insertTask(task)
        }

        // Seed Focus Sessions (Past 7 days)
        val sessions = listOf(
            // Today
            FocusSessionEntity(startTime = now - 3 * oneHour, durationSeconds = 1500, category = "Study", completed = true),
            FocusSessionEntity(startTime = now - 6 * oneHour, durationSeconds = 1800, category = "Work", completed = true),
            // Yesterday
            FocusSessionEntity(startTime = now - 1 * oneDay - 2 * oneHour, durationSeconds = 2700, category = "Study", completed = true),
            FocusSessionEntity(startTime = now - 1 * oneDay - 5 * oneHour, durationSeconds = 1800, category = "Study", completed = true),
            FocusSessionEntity(startTime = now - 1 * oneDay - 8 * oneHour, durationSeconds = 1500, category = "Personal", completed = true),
            // Day 3
            FocusSessionEntity(startTime = now - 2 * oneDay - 3 * oneHour, durationSeconds = 3600, category = "Study", completed = true),
            FocusSessionEntity(startTime = now - 2 * oneDay - 6 * oneHour, durationSeconds = 1800, category = "Work", completed = true),
            // Day 4
            FocusSessionEntity(startTime = now - 3 * oneDay - 4 * oneHour, durationSeconds = 3000, category = "Study", completed = true),
            FocusSessionEntity(startTime = now - 3 * oneDay - 7 * oneHour, durationSeconds = 1200, category = "Personal", completed = true),
            // Day 5
            FocusSessionEntity(startTime = now - 4 * oneDay - 2 * oneHour, durationSeconds = 3000, category = "Work", completed = true),
            FocusSessionEntity(startTime = now - 4 * oneDay - 5 * oneHour, durationSeconds = 2400, category = "Study", completed = true),
            // Day 6
            FocusSessionEntity(startTime = now - 5 * oneDay - 3 * oneHour, durationSeconds = 1800, category = "Personal", completed = true),
            // Day 7
            FocusSessionEntity(startTime = now - 6 * oneDay - 2 * oneHour, durationSeconds = 3600, category = "Study", completed = true),
            FocusSessionEntity(startTime = now - 6 * oneDay - 6 * oneHour, durationSeconds = 2700, category = "Work", completed = true)
        )

        for (session in sessions) {
            focusSessionDao.insertSession(session)
        }
    }
}
