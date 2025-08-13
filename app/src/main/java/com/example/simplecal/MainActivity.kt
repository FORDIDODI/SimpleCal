package com.example.simplecal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplecal.ui.theme.AppTheme
import com.example.simplecal.ui.theme.ThemeMode
import com.example.simplecal.ui.theme.rememberThemeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel = viewModel<CalculatorViewModel>()
            val themeMode by viewModel.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            val themeState = rememberThemeState(
                initialTheme = themeMode,
                onThemeUpdated = { newTheme -> viewModel.updateTheme(newTheme) }
            )

            AppTheme(themeState = themeState) {
                val state = viewModel.state
                val history = viewModel.history.collectAsState(initial = emptyList())
                val isScientificMode = viewModel.isScientificMode

                if (isScientificMode) {
                    CalculatorScientificLayout(
                        state = state,
                        history = history.value,
                        onAction = viewModel::onAction,
                        onToggleMode = { viewModel.toggleScientificMode() },
                        viewModel = viewModel
                    )
                } else {
                    CalculatorBasicLayout(
                        state = state,
                        history = history.value,
                        onAction = viewModel::onAction,
                        onToggleMode = { viewModel.toggleScientificMode() },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
