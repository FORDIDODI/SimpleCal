package com.example.simplecal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplecal.ui.theme.SimpleCalTheme
import net.objecthunter.exp4j.ExpressionBuilder

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
                        onToggleMode = { viewModel.toggleScientificMode() },
                        viewModel = viewModel
                    )
                } else {
                    CalculatorBasicLayout(
                        state = state,
                        history = history,
                        onAction = viewModel::onAction,
                        onToggleMode = { viewModel.toggleScientificMode() },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

fun evaluateExpression(expression: String): Double {
    return ExpressionBuilder(expression).build().evaluate()
}

fun evaluateSimpleExpression(expr: String): Double {
    val tokens = Regex("(?<=[-+*/])|(?=[-+*/])").split(expr).map { it.trim() }
    val numbers = mutableListOf<Double>()
    val operators = mutableListOf<String>()

    var i = 0
    while (i < tokens.size) {
        val token = tokens[i]
        if (token in listOf("+", "-", "*", "/")) {
            operators.add(token)
        } else {
            numbers.add(token.toDouble())
        }
        i++
    }

    // Proses *, /
    var idx = 0
    while (idx < operators.size) {
        if (operators[idx] == "*" || operators[idx] == "/") {
            val left = numbers[idx]
            val right = numbers[idx + 1]
            val result = if (operators[idx] == "*") left * right else left / right
            numbers[idx] = result
            numbers.removeAt(idx + 1)
            operators.removeAt(idx)
        } else {
            idx++
        }
    }

    // Proses +, -
    var result = numbers[0]
    for (j in operators.indices) {
        result = if (operators[j] == "+") result + numbers[j + 1] else result - numbers[j + 1]
    }
    return result
}
