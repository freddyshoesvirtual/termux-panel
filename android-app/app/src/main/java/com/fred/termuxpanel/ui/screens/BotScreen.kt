package com.fred.termuxpanel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fred.termuxpanel.network.ApiClient
import com.fred.termuxpanel.network.BotStatus
import com.fred.termuxpanel.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun BotScreen() {
    var status by remember { mutableStateOf<BotStatus?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                status = ApiClient.api.getBotStatus()
            } catch (e: Exception) {
                error = "No se pudo leer estado del bot."
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Bot WhatsApp.", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        error?.let { Text(it, color = Red, style = MaterialTheme.typography.bodySmall) }

        Card(
            colors = CardDefaults.cardColors(containerColor = Graphite2),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(status?.name ?: "Baileys — WhatsApp", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        if (status?.connected == true) "Sesión conectada" else "Desconectado",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLo
                    )
                }
                Switch(
                    checked = status?.connected ?: false,
                    onCheckedChange = { /* acción real: llamar a /api/action restart del proceso del bot */ },
                    colors = SwitchDefaults.colors(checkedTrackColor = Amber)
                )
            }
        }
    }
}
