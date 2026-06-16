package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey val levelNumber: Int,
    val unlocked: Boolean = false,
    val stars: Int = 0, // 0 to 3
    val timeTakenSeconds: Long = 0,
    val hintsUsed: Int = 0,
    val themeIdea: String = "Vibrant Math Magic"
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val unlocked: Boolean = false
)

data class UserProfile(
    val uid: String,
    val displayName: String,
    val currentLevel: Int = 1,
    val currentStreak: Int = 0,
    val lastPlayedDateStr: String = "",
    val totalScore: Int = 0
)

@JsonClass(generateAdapter = true)
data class Puzzle(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val hints: List<String>,
    val explanation: String
)
