package com.example.teacare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.teacare.ui.navigation.TeaCareNavGraph
import com.example.teacare.ui.theme.TeaCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userPreferences = TeaCareApplication.instance.userPreferences
            val isDarkModePref by userPreferences.isDarkMode.collectAsState(initial = false)
            val isSystemDark = isSystemInDarkTheme()
            val useDarkTheme = isDarkModePref || isSystemDark

            TeaCareTheme(darkTheme = isDarkModePref) {
                TeaCareNavGraph()
            }
        }
    }
}
