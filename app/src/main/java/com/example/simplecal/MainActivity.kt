package com.example.simplecal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplecal.ui.theme.SimpleCalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCalTheme {
                val viewModel = viewModel<CalculatorViewModel>()
                val state = viewModel.state
                val history = viewModel.history.value
                val isScientificMode = viewModel.isScientificMode
                val configuration = LocalConfiguration.current

                if (isScientificMode) {
                    CalculatorScientificLayout(
                        state = state,
                        history = history,
                        onAction = viewModel::onAction,
                        onToggleMode = { viewModel.toggleScientificMode() } // ✅ Tetap
                    )
                } else {
                    CalculatorBasicLayout(
                        state = state,
                        history = history,
                        onAction = viewModel::onAction,
                        onToggleMode = { viewModel.toggleScientificMode() } // ✅ Tetap
                    )
                }
            }
        }
    }
}
