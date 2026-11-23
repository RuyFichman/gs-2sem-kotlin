package com.ruy.vielasegura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ruy.vielasegura.ui.navigation.VielaSeguraNavHost
import com.ruy.vielasegura.ui.theme.VielaSeguraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VielaSeguraTheme {
                VielaSeguraNavHost()
            }
        }
    }
}
