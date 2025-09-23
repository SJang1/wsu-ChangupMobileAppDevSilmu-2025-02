package com.example.a0923

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a0923.ui.theme._0923Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _0923Theme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "Home",
                    modifier = Modifier.fillMaxSize() // 중요
                ) {
                    composable("Home") { HomeScreen(navController) }
                    composable("ProfileCardScreen") { ProfileCardScreen(Profile("김세진", "알 수 없는 개발자스러운 무언가")) }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) { // 중요
        Box(
            modifier = Modifier.fillMaxSize(), // 중요
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { navController.navigate("ProfileCardScreen") }) {
                Text("프로필 화면으로 이동하기")
            }
        }
    }
}
@Preview(
    name = "Home",
    showBackground = true
)
@Composable
fun HomePreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}




