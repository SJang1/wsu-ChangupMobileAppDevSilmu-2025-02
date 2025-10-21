package com.composelab.stopwatchgame.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composelab.stopwatchgame.data.GameConfig
import com.composelab.stopwatchgame.data.GameData
import com.composelab.stopwatchgame.data.GameUiState
import com.composelab.stopwatchgame.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * 게임의 비즈니스 로직을 처리하고 UI 상태를 관리하는 ViewModel입니다.
 * 모든 UI 상태는 단일 MutableStateFlow<GameUiState>로 관리됩니다.
 */
class GameViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var stopwatchJob: Job? = null

    init {
        // ⭐️ 변경: config와 단일 user Flow를 결합합니다.
        viewModelScope.launch {
            combine(repository.config, repository.user) { config, user ->
                _uiState.update { currentState ->
                    currentState.copy(
                        config = config,
                        currentUserData = user // ⭐️ 단일 UserData 객체로 업데이트
                    )
                }
            }.collect()
        }
    }

    fun onStartClicked() {
        if (_uiState.value.gameData.isRunning) return

        stopwatchJob?.cancel()
        _uiState.update {
            it.copy(
                gameData = GameData(isRunning = true),
                feedbackMessage = null
            )
        }

        stopwatchJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (_uiState.value.gameData.isRunning) {
                delay(10)
                val elapsed = System.currentTimeMillis() - startTime
                _uiState.update {
                    it.copy(gameData = it.gameData.copy(currentTimeMs = elapsed))
                }
            }
        }
    }

    fun onStopClicked(finalTimeMs: Long) {
        stopwatchJob?.cancel()
        _uiState.update { it.copy(gameData = it.gameData.copy(isRunning = false)) }

        val config = _uiState.value.config
        val diff = abs(finalTimeMs - config.targetTimeMs)

        val isSuccess = diff <= config.toleranceMs

        if (isSuccess) {
            val newPoint = _uiState.value.gameData.currentPoint + 1
            val currentTotalScore = _uiState.value.currentUserData.totalScore

            // ⭐️ 변경: Repository에 사용자 ID 없이 바로 점수 추가
            repository.addPoint(1)

            _uiState.update {
                it.copy(
                    gameData = it.gameData.copy(currentPoint = newPoint),
                    // 다음 totalScore는 repository.addPoint() 후 Flow를 통해 자동으로 갱신됨
                    feedbackMessage = "정확! $diff ms 오차. 누적 점수: ${currentTotalScore + 1}점"
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    feedbackMessage = "실패... $diff ms 오차."
                )
            }
        }
    }

    fun onConfigUpdated(newConfig: GameConfig) {
        repository.updateConfig(newConfig)
    }
}