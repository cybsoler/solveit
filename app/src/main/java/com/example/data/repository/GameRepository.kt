package com.example.data.repository

import com.example.data.local.LevelDao
import com.example.model.LevelEntity
import com.example.model.UserProfile
import com.example.model.Badge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GameRepository(private val levelDao: LevelDao) {
    
    // Simulate user state (should be linked to Firebase Auth/Firestore)
    private val _userProfile = MutableStateFlow(UserProfile(uid = "user123", displayName = "Player One"))
    val userProfile = _userProfile.asStateFlow()

    fun getAllLevels(): Flow<List<LevelEntity>> = levelDao.getAllLevels()

    suspend fun completeLevel(levelNumber: Int, stars: Int, timeTaken: Long, hintsUsed: Int) {
        val existing = levelDao.getLevel(levelNumber) ?: LevelEntity(levelNumber)
        val updated = existing.copy(
            unlocked = true, 
            stars = maxOf(existing.stars, stars),
            timeTakenSeconds = timeTaken,
            hintsUsed = hintsUsed
        )
        levelDao.insertLevel(updated)
        
        // Unlock next
        if (levelNumber < 50) {
            val next = levelDao.getLevel(levelNumber + 1) ?: LevelEntity(levelNumber + 1)
            levelDao.insertLevel(next.copy(unlocked = true))
        }

        // Update profile streak and level
        updateStreak()
        _userProfile.update { it.copy(currentLevel = maxOf(it.currentLevel, levelNumber + 1)) }
    }

    suspend fun getOrCreateLevel(levelNumber: Int): LevelEntity {
        var level = levelDao.getLevel(levelNumber)
        if (level == null) {
            level = LevelEntity(levelNumber = levelNumber, unlocked = levelNumber == 1)
            levelDao.insertLevel(level)
        }
        return level
    }

    suspend fun initializeFirstRun() {
        if (levelDao.getLevel(1) == null) {
            val defaultLevels = (1..50).map { 
                LevelEntity(levelNumber = it, unlocked = (it == 1), themeIdea = "Theme $it") 
            }
            levelDao.insertLevels(defaultLevels)
        }
    }

    private fun updateStreak() {
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        _userProfile.update { current ->
            if (current.lastPlayedDateStr != todayStr) {
                // Determine if it was consecutive using LocalDate (simplified here as naive increment)
                current.copy(currentStreak = current.currentStreak + 1, lastPlayedDateStr = todayStr)
            } else {
                current
            }
        }
    }
}
