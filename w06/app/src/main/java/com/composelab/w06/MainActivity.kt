package com.composelab.w06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.composelab.w06.ui.theme.W06Theme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Bubble(
    val id: Int,
    var position: Offset,
    val radius: Float,
    val color: Color,
    val creationTime: Long = System.currentTimeMillis(),
    val velocityX: Float = 0f,
    val velocityY: Float = 0f
)

class GameState(
    initialBubbles: List<Bubble> = emptyList()
) {
    var bubbles by mutableStateOf(initialBubbles)
    var score by mutableStateOf(0)
    var isGameOver by mutableStateOf(false)
    var timeLeft by mutableStateOf(60) // 남은 시간: 60초로 시작
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            W06Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BubbleGameScreen()
                }
            }
        }
    }
}

// 게임의 전체 화면
@Composable
fun BubbleGameScreen() {
    // 1. 게임에 필요한 상태 변수들 선언
    val gameState: GameState = remember {
        // 1개의 버블 상태 생성
        val newBubble = Bubble(
            id = Random.nextInt(),
            position = Offset(
                x = Random.nextFloat() * 150,
                y = Random.nextFloat() * 150
            ),
            radius = Random.nextFloat() * 50,
            color = Color(
                red = Random.nextInt(256),
                green = Random.nextInt(256),
                blue = Random.nextInt(256),
                alpha = 200
            )
        )
        GameState(listOf(newBubble))
    }

    // 2. 타이머 로직 추가
    LaunchedEffect(gameState.isGameOver) {
        // 게임이 진행 중일 때만 타이머 작동
        if (!gameState.isGameOver && gameState.timeLeft > 0) {
            while (true) {
                delay(1000L) // 1초 대기
                gameState.timeLeft-- // 시간 1초 감소
                if (gameState.timeLeft == 0) {
                    gameState.isGameOver = true // 시간이 0이 되면 게임 오버
                    break
                }

                // 3초가 지난 버블 제거
                val currentTime = System.currentTimeMillis()
                gameState.bubbles = gameState.bubbles.filter { // filter()는 원본 리스트를 변경하지 않고 새 리스트 생성
                    currentTime - it.creationTime < 3000
                }


            }
        }
    }

    // 3. 버블의 상태를 관리
    Column(modifier = Modifier.fillMaxSize()) {
        // 1. 상단 상태 바 UI 추가
        GameStatusRow(score = gameState.score, timeLeft = gameState.timeLeft) // gameTime으로 수정

        Box(modifier = Modifier.fillMaxSize()) {
            // ✅ gameState.bubbles 리스트를 루프로 돌며 각 버블을 그립니다.
            gameState.bubbles.forEach { currentBubble ->
                BubbleComposable(bubble = currentBubble) { // canvas에 버블 그리기
                    if (!gameState.isGameOver) {
                        gameState.score++
                        // 클릭된 버블을 리스트에서 제거하는 로직 추가
                        // gameState.bubbles = gameState.bubbles.filterNot { it.id == currentBubble.id }
                    }
                }
            }
        }
    }



}

// 상단 UI를 별도의 Composable로 분리 (가독성 향상)
@Composable
fun GameStatusRow(score: Int, timeLeft: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Time: ${timeLeft}s", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun BubbleComposable(bubble: Bubble, onClick: () -> Unit) {
    Canvas(
        modifier = Modifier
            .size((bubble.radius * 2).dp)
            .offset(x = bubble.position.x.dp, y = bubble.position.y.dp)
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = null, // 클릭 시 물결 효과 제거
                onClick = onClick
            )
    ) {
        drawCircle(
            color = bubble.color,
            radius = size.width / 2, // / size.width는 Canvas의 실제 가로 픽셀(px) 크기
            center = center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BubbleGamePreview() {
    W06Theme {
        BubbleGameScreen()
    }
}