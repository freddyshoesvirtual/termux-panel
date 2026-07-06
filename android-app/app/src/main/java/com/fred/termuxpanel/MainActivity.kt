package com.fred.termuxpanel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.fred.termuxpanel.ui.PanelNavHost
import com.fred.termuxpanel.ui.theme.Graphite1
import com.fred.termuxpanel.ui.theme.TermuxPanelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TermuxPanelTheme {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxSize().background(Graphite1)
                ) {
                    PanelNavHost()
                }
            }
        }
    }
}
