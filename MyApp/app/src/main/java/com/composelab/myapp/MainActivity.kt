package com.composelab.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composelab.myapp.ui.theme.MyAppTheme
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- 1. Data Models (FIXED for robust JSON parsing) ---

// Original simple model, now used only for the UI display layer
@Serializable
data class HistoryEvent(
    val date: String,
    val description: String,
    val link: String? = null
)

// Model to parse the complex AWS Health data from the API response
@Serializable
data class HealthEvent(
    // ⭐️ FIX 1: Made 'summary' nullable to prevent "missing field" error.
    val summary: String? = null,
    val arn: String,
    val status: String,
    val date: String, // Unix timestamp as a String
    // ⭐️ FIX 2: Made 'event_log' nullable to prevent issues if it's missing.
    val event_log: List<EventLogEntry>? = null
)

@Serializable
data class EventLogEntry(
    // ⭐️ FIX 3: Made 'summary' nullable in EventLogEntry as well, for safety.
    val summary: String? = null,
    val message: String,
    val status: String,
    val timestamp: Long
)

// Extension function to map the complex API model to the simple UI model
fun HealthEvent.toHistoryEvent(): HistoryEvent {
    // Convert Unix timestamp (String) to a readable date
    // Note: AWS Health timestamps are often in seconds, hence the * 1000L
    val timestamp = this.date.toLongOrNull() ?: System.currentTimeMillis() / 1000L
    val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp * 1000L))

    // ⭐️ FIX 4: Improved fallback logic for description using the nullable fields
    val finalMessage = this.event_log?.lastOrNull()?.message
        ?: this.summary
        ?: "Event description not available (ARN: ${this.arn})" // Final fallback

    // Use the ARN as the "link" placeholder
    return HistoryEvent(
        date = dateString,
        description = finalMessage,
        link = this.arn
    )
}

// --- 2. Retrofit API Interface and Client ---
// NOTE: BASE_URL and the API GET endpoint must point to the actual JSON file
private const val BASE_URL = "https://history-events-ap-northeast-1-prod.s3.amazonaws.com/" // Placeholder for a real endpoint
private val json = Json {
    ignoreUnknownKeys = true // CRITICAL: Ignores fields like 'impacted_services'
    isLenient = true
    // New: Allow special floating-point values like NaN/Infinity
    allowSpecialFloatingPointValues = true
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

// The API service returns a Map<String, List<HealthEvent>>
interface HistoryApiService {
    @retrofit2.http.GET("historyevents.json") // Placeholder for a real file path
    suspend fun getHealthEvents(): Map<String, List<HealthEvent>>
}

object HistoryApi {
    val service: HistoryApiService by lazy {
        retrofit.create(HistoryApiService::class.java)
    }
}

// --- 3. ViewModel for State Management and Data Fetching ---
sealed class UiState {
    object Loading : UiState()
    data class Success(val events: List<HistoryEvent>) : UiState()
    data class Error(val message: String) : UiState()
}

class HistoryViewModel : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Loading)
    val uiState: State<UiState> = _uiState

    init {
        fetchHealthEvents()
    }

    private fun fetchHealthEvents() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            _uiState.value = try {
                // 1. Fetch the Map of service-to-event-list
                val mapResult = HistoryApi.service.getHealthEvents()

                // 2. Flatten the Map into a single list of all HealthEvents
                val allHealthEvents = mapResult.values.flatten()

                // 3. Map the complex HealthEvents to simple HistoryEvents
                val allHistoryEvents = allHealthEvents.map { it.toHistoryEvent() }

                UiState.Success(allHistoryEvents)

            } catch (e: IOException) {
                // This is often the actual parsing error wrapped inside
                UiState.Error("Network/IO Error: ${e.localizedMessage}")
            } catch (e: HttpException) {
                UiState.Error("Server Error: ${e.code()}. Check the API URL.")
            } catch (e: Exception) {
                // Catch all other errors, including the FIXED JSON parsing error
                e.printStackTrace()
                // Provide the specific error message from the exception
                UiState.Error("Data parsing error: ${e.localizedMessage}")
            }
        }
    }
}

// --- 4. Main Activity and Composable Functions (Original UI components) ---
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("AWS Health Events") }
                        )
                    }
                ) { innerPadding ->
                    HistoryScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel()
) {
    val state = viewModel.uiState.value

    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        when (state) {
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is UiState.Error -> {
                ErrorDisplay(message = state.message, modifier = Modifier.align(Alignment.Center))
            }
            is UiState.Success -> {
                EventList(events = state.events)
            }
        }
    }
}

@Composable
fun EventList(events: List<HistoryEvent>) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events) { event ->
            EventCard(event = event)
        }
    }
}

@Composable
fun EventCard(event: HistoryEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.date,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium
            )
            event.link?.let {
                Spacer(modifier = Modifier.height(4.dp))
                // Displaying the ARN as a link placeholder
                Text(
                    text = "ARN: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ErrorDisplay(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Error Loading Data",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    MyAppTheme {
        // Mock data for preview
        val mockEvents = listOf(
            HistoryEvent("2023-10-20 18:36", "[RESOLVED] Increased error rates and latencies for DynamoDB, which is affecting other AWS services in the US-EAST-1 Region.", "arn:aws:health:us-east-1::event/MULTIPLE_SERVICES/..."),
            HistoryEvent("2023-10-18 10:00", "[RESOLVED] Glue 5.0 Job Run Failures.", "arn:aws:health:ap-northeast-1::event/GLUE/..."),
        )
        Scaffold(topBar = { TopAppBar(title = { Text("AWS Health Events") }) }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                EventList(events = mockEvents)
            }
        }
    }
}