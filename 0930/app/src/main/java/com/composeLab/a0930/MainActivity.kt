package com.composeLab.a0930

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ComposeLab.a0930.ui.theme._0930Theme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _0930Theme {
                val count = remember { mutableIntStateOf(0) }

                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CounterApp(count)
                    Spacer(modifier = Modifier.height(32.dp))
                    StopWatchApp()
                }
            }
        }
    }
}

@Composable
fun CounterApp(count: MutableState<Int>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Count: ${count.value}")
        Row {
            Button(onClick = { count.value++  }) {
                Text("Increase")
            }
            Button(onClick = { count.value = 0  }) {
                Text("Reset")
            }
        }

    }
}

@Composable
fun StopWatchApp() {
    // 초깃값= 15:22
    var seconds by remember { mutableIntStateOf(15 * 60 + 22) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                delay(1000)
                seconds++
            }
        }
    }

    StopWatchScreen(
        seconds = seconds,
        isRunning = isRunning,
        onStartClick = { isRunning = true },
        onStopClick = { isRunning = false },
        onResetClick = {
            seconds = 0
            isRunning = false
        }
    )
}


@Composable
fun StopWatchScreen(
    seconds: Int,
    isRunning: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val minutes = seconds / 60
        val secondsToDisplay = seconds % 60
        val timeFormatted = String.format("%02d:%02d", minutes, secondsToDisplay)
        Text(
            text = timeFormatted,
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onStartClick,
                enabled = !isRunning
            ) {
                Text("Start")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onStopClick,
                enabled = isRunning
            ) {
                Text("Stop")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onResetClick) {
                Text("Reset")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CounterAppPreview() {
    CounterApp(remember { mutableIntStateOf(0) })
}

@Preview(showBackground = true)
@Composable
fun StopWatchPreview() {
    StopWatchApp()
}
