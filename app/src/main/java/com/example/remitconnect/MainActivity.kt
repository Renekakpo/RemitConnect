package com.example.remitconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.remitconnect.navigation.RemitNavGraph
import com.example.remitconnect.ui.theme.RemitConnectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RemitConnectTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    RemitNavGraph(navController = rememberNavController())
                }
            }
        }
    }
}