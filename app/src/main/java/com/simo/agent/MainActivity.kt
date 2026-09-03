package com.simo.agent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.simo.agent.ui.navigation.SimoNavHost
import com.simo.agent.ui.theme.SimoAgentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimoAgentTheme {
                SimoNavHost()
            }
        }
    }
}
