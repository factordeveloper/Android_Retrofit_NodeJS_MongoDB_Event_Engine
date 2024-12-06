package com.factor.dev.cyberalerts_wifi_ip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.factor.dev.cyberalerts_wifi_ip.ui.theme.CyberAlerts_Wifi_IPTheme


import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Retrofit Service
interface AlertService {
    @POST("alerts")
    suspend fun postAlert(@Body alert: Alert): Response
}

// Data Classes
data class Alert(val type: String, val message: String, val timestamp: String)
data class Response(val success: Boolean, val alert: Alert?)

class MainActivity : ComponentActivity() {
    private lateinit var alertService: AlertService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://d613-186-170-212-103.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        alertService = retrofit.create(AlertService::class.java)

        setContent {
            CyberAlertApp(alertService)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberAlertApp(alertService: AlertService) {
    var alertMessage by remember { mutableStateOf("No alerts sent yet.") }

    // Coroutine to send alert every second
    LaunchedEffect(Unit) {
        while (true) {
            try {
                val alert = Alert(
                    type = "IP Connection",
                    message = "A new IP connection was detected.",
                    timestamp = System.currentTimeMillis().toString()
                )
                val response = alertService.postAlert(alert)
                if (response.success) {
                    alertMessage = "Alert sent successfully: ${response.alert?.message}"
                } else {
                    alertMessage = "Failed to send alert."
                }
            } catch (e: Exception) {
                alertMessage = "Error: ${e.message}"
            }
            delay(1000L) // 1 second delay
        }
    }

    // UI
    Scaffold(
        topBar = { TopAppBar(title = { Text("Cyber Alerts") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = alertMessage, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
