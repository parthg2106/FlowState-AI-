package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val isCompleted: Boolean,
    val category: String, // "Study", "Work", "Life", "Personal"
    val dueDate: Long, // timestamp
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val estimatedMinutes: Int = 25,
    val actualMinutes: Int = 0
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val durationSeconds: Long,
    val category: String, // "Study", "Work", "Life"
    val completed: Boolean,
    val notes: String? = null
)

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Jordan Smith",
    val streakDays: Int = 7,
    val aiPersonality: String = "Analytical Advisor", // "Strict Mentor", "Encouraging Coach"
    val focusSound: String = "None", // "None", "Rain", "White Noise", "Forest"
    val dailyGoalCount: Int = 6,
    val lastActiveDate: String = ""
)
