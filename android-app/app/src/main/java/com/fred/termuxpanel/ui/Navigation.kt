package com.fred.termuxpanel.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fred.termuxpanel.ui.screens.*
import com.fred.termuxpanel.ui.theme.Amber
import com.fred.termuxpanel.ui.theme.Graphite2
import com.fred.termuxpanel.ui.theme.TextLo

data class NavItem(val route: String, val label: String, val icon: String)

val navItems = listOf(
    NavItem("home", "HOME", "🏠"),
    NavItem("scripts", "SCRIPTS", "⚡"),
    NavItem("bot", "BOT", "🤖"),
    NavItem("server", "SERVER", "🖥️"),
)

@Composable
fun PanelNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Graphite2) {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(item.icon) },
                        label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Amber,
                            selectedTextColor = Amber,
                            unselectedIconColor = TextLo,
                            unselectedTextColor = TextLo,
                            indicatorColor = Graphite2
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(onNavigate = { navController.navigate(it) }) }
            composable("scripts") { ScriptsScreen() }
            composable("bot") { BotScreen() }
            composable("server") { ServerScreen() }
        }
    }
}
