package com.fred.termuxpanel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fred.termuxpanel.network.ApiClient
import com.fred.termuxpanel.network.ServerStatus
import com.fred.termuxpanel.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ServerScreen() {
    var status by remember { mutableStateOf<ServerStatus?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                status = ApiClient.api.getServerStatus()
            } catch (e: Exception) {
                error = "Backend no responde."
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Servidor.", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        error?.let { Text(it, color = Red, style = MaterialTheme.typography.bodySmall) }

        Card(
            colors = CardDefaults.cardColors(containerColor = Graphite2),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("RAM en uso", style = MaterialTheme.typography.bodySmall, color = TextLo)
                LinearProgressIndicator(
                    progress = (status?.ram_used_pct ?: 0) / 100f,
                    color = Amber,
                    trackColor = Graphite4,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 14.dp)
                )
                Text("Load average: ${status?.load_avg ?: "—"}", style = MaterialTheme.typography.bodySmall, color = TextLo)
                Spacer(Modifier.height(4.dp))
                Text("Uptime: ${status?.uptime_days ?: "—"} días", style = MaterialTheme.typography.bodySmall, color = TextLo)
                Spacer(Modifier.height(4.dp))
                Text("Cloudflare Tunnel: ${status?.tunnel_status ?: "—"}", style = MaterialTheme.typography.bodySmall, color = TextLo)
            }
        }
    }
}
