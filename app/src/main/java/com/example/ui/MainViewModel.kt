package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.GameRepository
import com.example.data.api.GeminiRepository
import com.example.model.LevelEntity
import com.example.model.Puzzle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: GameRepository) : ViewModel() {

    val allLevels = repository.getAllLevels().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userProfile = repository.userProfile

    private val _currentPuzzle = MutableStateFlow<Puzzle?>(null)
    val currentPuzzle = _currentPuzzle.asStateFlow()

    private val _puzzleLoading = MutableStateFlow(false)
    val puzzleLoading = _puzzleLoading.asStateFlow()

    private val _generatedImageBase64 = MutableStateFlow<String?>(null)
    val generatedImageBase64 = _generatedImageBase64.asStateFlow()

    private val _imageLoading = MutableStateFlow(false)
    val imageLoading = _imageLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeFirstRun()
        }
    }

    fun loadPuzzle(levelNumber: Int) {
        viewModelScope.launch {
            _puzzleLoading.value = true
            val puzzle = GeminiRepository.generatePuzzle(levelNumber)
            _currentPuzzle.value = puzzle
            _puzzleLoading.value = false
        }
    }

    fun clearPuzzle() {
        _currentPuzzle.value = null
    }

    fun finishLevel(levelNumber: Int, stars: Int, timeTaken: Long, hints: Int) {
        viewModelScope.launch {
            repository.completeLevel(levelNumber, stars, timeTaken, hints)
        }
    }

    fun requestImage(prompt: String, size: String) {
        viewModelScope.launch {
            _imageLoading.value = true
            val base64 = GeminiRepository.generateImage(prompt, size)
            _generatedImageBase64.value = base64
            _imageLoading.value = false
        }
    }
}
