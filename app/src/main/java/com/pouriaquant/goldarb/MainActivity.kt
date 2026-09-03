package com.pouriaquant.goldarb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pouriaquant.goldarb.ui.GoldArbApp
import com.pouriaquant.goldarb.ui.theme.GoldArbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoldArbTheme {
                GoldArbApp()
            }
        }
    }
}
