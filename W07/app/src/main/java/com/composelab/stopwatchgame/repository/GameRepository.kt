package com.composelab.stopwatchgame.repository

import com.composelab.stopwatchgame.data.DEFAULT_GAME_CONFIG
import com.composelab.stopwatchgame.data.GameConfig
import com.composelab.stopwatchgame.data.INITIAL_USER_DATA
import com.composelab.stopwatchgame.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 게임의 영구적인 데이터(설정, 사용자 점수)를 관리하는 Repository입니다.
 * 1인용 게임이므로 사용자 데이터는 단일 객체로 관리됩니다.
 */
class GameRepository {
    // 1. 게임 설정 (GameConfig) 관리
    private val _config = MutableStateFlow(DEFAULT_GAME_CONFIG)
    val config: StateFlow<GameConfig> = _config.asStateFlow()

    fun updateConfig(newConfig: GameConfig) {
        _config.value = newConfig
    }

    // 2. 사용자 점수 (UserData) 관리
    private val _user = MutableStateFlow(INITIAL_USER_DATA)
    // ViewModel에 단일 사용자 데이터를 StateFlow로 노출합니다.
    val user: StateFlow<UserData> = _user.asStateFlow()

    // 현재 사용자의 총 점수를 갱신합니다.
    fun addPoint(point: Int = 1) {
        _user.update { currentUser ->
            // 현재 사용자 데이터에 점수만 추가하여 업데이트
            currentUser.copy(totalScore = currentUser.totalScore + point)
        }
    }
}