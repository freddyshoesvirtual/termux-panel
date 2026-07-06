package com.fred.termuxpanel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fred.termuxpanel.network.ApiClient
import com.fred.termuxpanel.network.ScriptInfo
import com.fred.termuxpanel.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ScriptsScreen() {
    var scripts by remember { mutableStateOf<List<ScriptInfo>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                scripts = ApiClient.api.getScripts().scripts
            } catch (e: Exception) {
                error = "No se pudo leer pm2 — revisa que esté instalado y con procesos registrados."
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Scripts.", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        error?.let { Text(it, color = Red, style = MaterialTheme.typography.bodySmall) }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(scripts) { script ->
                ScriptRow(script) {
                    scope.launch {
                        try {
                            ApiClient.api.postAction(
                                com.fred.termuxpanel.network.ActionRequest("restart", script.name)
                            )
                        } catch (_: Exception) { }
                    }
                }
            }
        }
    }
}

@Composable
fun ScriptRow(script: ScriptInfo, onRestart: () -> Unit) {
    val (pillColor, pillText) = when (script.status) {
        "online" -> Teal to "RUN"
        "errored" -> Red to "ERROR"
        else -> Amber to "IDLE"
    }

    Card(
        onClick = onRestart,
        colors = CardDefaults.cardColors(containerColor = Graphite2),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(script.name, style = MaterialTheme.typography.bodyMedium)
                Text("pid ${script.pid ?: "—"}", style = MaterialTheme.typography.bodySmall, color = TextLo)
            }
            StatusPill(pillText, pillColor)
        }
    }
}

@Composable
fun StatusPill(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
