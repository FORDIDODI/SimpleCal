package com.example.simplecal

import android.os.Bundle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simplecal.ui.theme.MediumGray
import com.example.simplecal.ui.theme.SimpleCalTheme
import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.platform.LocalConfiguration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCalTheme {
                val viewModel = viewModel<CalculatorViewModel>()
                val state = viewModel.state
                val configuration = LocalConfiguration.current
                val buttonSpacing = 8.dp
                if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    CalculatorScientificLayout(
                        state = state,
                        history = viewModel.history.value,
                        onAction = viewModel::onAction
                    )
                } else {
                    CalculatorBasicLayout(
                        state = state,
                        history = viewModel.history.value,
                        onAction = viewModel::onAction
                    )
                }
            }
        }
    }
}

