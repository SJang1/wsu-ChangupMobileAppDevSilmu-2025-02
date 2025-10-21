package com.composelab.stopwatchgame.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composelab.stopwatchgame.ui.theme.StopwatchGameTheme
import java.util.concurrent.TimeUnit

// 이 Composable은 View 역할을 합니다.
@Composable
fun GameScreen(
    // ViewModel을 생성하고, Composable의 Lifecycle에 연결합니다.
    viewModel: GameViewModel = viewModel()
) {
    // 1. 상태 관찰 (State Collection):
    // ViewModel의 단일 StateFlow<GameUiState>를 관찰하여 상태가 변경될 때마다 UI를 재구성합니다.
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 사용자 및 점수 정보
        Text(
            text = "플레이어: ${uiState.currentUserData.userName} (총점: ${uiState.currentUserData.totalScore}점)",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "현재 라운드 포인트: ${uiState.gameData.currentPoint}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 설정 정보
        Text(
            text = "목표 시간: ${formatTime(uiState.config.targetTimeMs)}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "오차 범위: ±${formatTime(uiState.config.toleranceMs)}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // 2. 스톱워치 디스플레이 컴포넌트 (State Hoisting)
        StopwatchDisplay(currentTimeMs = uiState.gameData.currentTimeMs)

        Spacer(modifier = Modifier.height(64.dp))

        // 3. 컨트롤 버튼 (Event Forwarding)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.onStartClicked() }, // 이벤트 전달
                enabled = !uiState.gameData.isRunning // 실행 중이 아닐 때만 활성화
            ) {
                Text("Start", fontSize = 20.sp)
            }

            Button(
                onClick = { viewModel.onStopClicked(uiState.gameData.currentTimeMs) }, // 이벤트 전달
                enabled = uiState.gameData.isRunning // 실행 중일 때만 활성화
            ) {
                Text("Stop", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 피드백 메시지 출력
        uiState.feedbackMessage?.let { message ->
            Text(
                text = message,
                color = if (message.contains("정확")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontSize = 22.sp
            )
        }
    }
}

// 스톱워치 시간을 표시하는 하위 Composable (재사용 가능한 UI 요소)
@Composable
fun StopwatchDisplay(currentTimeMs: Long) {
    Text(
        text = formatTime(currentTimeMs),
        fontSize = 72.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

// 시간 포맷팅 유틸리티 함수
fun formatTime(timeMs: Long): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMs)
    val totalMillis = timeMs % 1000

    val seconds = totalSeconds
    val millis = totalMillis / 10 // 100분의 1초 단위로 표시

    return String.format("%02d.%02d", seconds, millis)
}

/**
 * Android Studio에서 GameScreen을 미리보기 위한 Composable 함수.
 */
@Preview(showBackground = true) // 미리보기 기능을 활성화하는 어노테이션
@Composable
fun GameScreenPreview() {
    // 1. Theme 적용: 미리보기에서도 실제 앱의 디자인을 볼 수 있도록 Theme을 적용합니다.
    StopwatchGameTheme {

        // 2. 가상의 상태 생성:
        // Preview에서는 ViewModel을 직접 사용할 수 없으므로,
        // GameScreen이 필요로 하는 UI 상태를 직접 모방하여 전달해야 합니다.
        // 현재 GameScreen은 인자 없이 ViewModel을 생성하므로,
        // 여기서는 GameScreen을 직접 호출하는 대신, 테스트용 ViewModel을 사용하거나
        // 간단한 Mock 데이터를 사용하여 컴포넌트를 호출하는 것이 일반적입니다.

        // 하지만 GameScreen이 ViewModel을 내부에서 생성하므로,
        // 여기서는 앱의 최종 화면을 간단히 호스팅하는 방식으로 Preview를 구성합니다.

        // 주의: Preview에서는 실제 LiveData나 StateFlow의 데이터 흐름이 작동하지 않습니다.
        // 따라서, ViewModel을 인자로 받는 별도의 테스트용 Preview Composable을 만드는 것이 더 좋습니다.

        // 현재 구조상 GameScreen을 호출하면 ViewModel이 생성되지만,
        // Preview에서는 Mock ViewModel이 필요합니다.

        // 여기서는 간단히 GameScreen을 직접 호출합니다.
        // 만약 Preview가 로딩되지 않는다면, GameScreen의 인자를 Mock ViewModel로 변경해야 합니다.
        // 현재는 인자 없이 호출하는 구조이므로, 기본 구조를 따릅니다.

        GameScreen() // 실제 앱 실행 화면과 동일하게 GameScreen 호출
    }
}

/**
 * 스톱워치 디스플레이 컴포넌트만 미리보기.
 */
@Preview(name = "Stopwatch Only", showBackground = true)
@Composable
fun StopwatchDisplayPreview() {
    StopwatchGameTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Running State Mock")
            StopwatchDisplay(currentTimeMs = 4567L)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Stop State Mock")
            StopwatchDisplay(currentTimeMs = 5002L)
        }
    }
}