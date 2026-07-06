package com.fred.termuxpanel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fred.termuxpanel.network.ApiClient
import com.fred.termuxpanel.network.ServerStatus
import com.fred.termuxpanel.ui.theme.*
import kotlinx.coroutines.launch

data class QuickAction(val label: String, val sub: String, val icon: String, val route: String)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    var serverStatus by remember { mutableStateOf<ServerStatus?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                serverStatus = ApiClient.api.getServerStatus()
            } catch (e: Exception) {
                error = "Backend no responde — ¿está corriendo node server.js en Termux?"
            }
        }
    }

    val actions = listOf(
        QuickAction("Scripts", "ver procesos", "⚡", "scripts"),
        QuickAction("Bot WhatsApp", "estado Baileys", "🤖", "bot"),
        QuickAction("Servidor", "Oracle Cloud", "🖥️", "server"),
    )

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Panel.", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        error?.let {
            Text(it, color = Red, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = serverStatus?.let { "${it.ram_used_pct}%" } ?: "—",
                label = "RAM usada",
                color = Amber
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = serverStatus?.uptime_days?.let { "${it}d" } ?: "—",
                label = "Uptime servidor",
                color = Teal
            )
        }

        Spacer(Modifier.height(20.dp))
        Text("ACCESOS RÁPIDOS", style = MaterialTheme.typography.labelSmall, color = TextLo)
        Spacer(Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(actions) { action ->
                ActionTile(action) { onNavigate(action.route) }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, value: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Graphite2),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextLo)
        }
    }
}

@Composable
fun ActionTile(action: QuickAction, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Graphite2),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(100.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(action.icon, style = MaterialTheme.typography.titleLarge)
            Column {
                Text(action.label, style = MaterialTheme.typography.bodyMedium)
                Text(action.sub, style = MaterialTheme.typography.bodySmall, color = TextLo)
            }
        }
    }
}
