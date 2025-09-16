package com.example.composelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.composelab.ui.theme.ComposeLabTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeLabTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column {
                        Greeting(
                        name = "Android",
                        deviceName = android.os.Build.MODEL,
                        modifier = Modifier.padding(innerPadding),
                        )
                        Counter()
                    }

                }
            }
        }
    }
}

class CounterViewModel : ViewModel() {
    private val _randomNumber = mutableIntStateOf(-1)
    val randomNumber: State<Int> = _randomNumber

    fun generateRandomNumber() {
        _randomNumber.value = Random.nextInt(0, 99)
    }
}

// Composable
@Composable
fun Counter(modifier: Modifier = Modifier, viewModel: CounterViewModel = CounterViewModel()) {
    val randomNumber by viewModel.randomNumber // Observe the state from ViewModel

    Column(verticalArrangement = Arrangement.Top, modifier = modifier) {
        Button(
            onClick = { viewModel.generateRandomNumber() }, // Call ViewModel function
        ) {
            if(randomNumber == -1) {
                Text("Generate Random Number")
            } else {
                Text("Number: $randomNumber")
            }
        }
    }
}

@Composable
fun Greeting(name: String, deviceName: String, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.Top, modifier = modifier) {
        Text(
            text = "Hello $name!",
        )
        Text(
            text = "You are from \"$deviceName\" Device",
        )
        Button(onClick = {  }) {
            Text("Button")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Column {
        Greeting(
            name = "Android",
            deviceName = android.os.Build.MODEL
        )
        Counter()
    }
}