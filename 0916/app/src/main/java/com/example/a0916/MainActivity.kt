package com.example.a0916

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.a0916.ui.theme._0916Theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),  // fill the page to the max size of device
        horizontalAlignment = Alignment.CenterHorizontally, // center horizontally
        verticalArrangement = Arrangement.Center    // center vertically
    ) {
        Text("Compose Coffee")
        Image(
            painter = painterResource(id = R.drawable.compose),
            contentDescription = "Jetpack Compose Logo",
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        )
        Text("위치: 우송대 정문 앞")
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

