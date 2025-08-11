package com.example.simplecal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplecal.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.tan
import kotlin.math.sqrt
import kotlin.math.log10
import net.objecthunter.exp4j.ExpressionBuilder


class CalculatorViewModel : ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history = _history.asStateFlow()

    var isScientificMode by mutableStateOf(false)
        private set

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    var currentInput by mutableStateOf("")
        private set

    fun toggleScientificMode() {
        isScientificMode = !isScientificMode
    }

    fun updateTheme(newTheme: ThemeMode) {
        viewModelScope.launch {
            _themeMode.emit(newTheme)
            // Here you can add code to save the theme preference
            // using DataStore or SharedPreferences
        }
    }

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number.toString())
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Parenthesis -> enterParenthesis(action.symbol)
            CalculatorAction.Clear -> state = CalculatorState()
            CalculatorAction.Calculate -> performCalculation()
            CalculatorAction.Decimal -> enterDecimal()
            CalculatorAction.Delete -> performDeletion()
            CalculatorAction.ToggleSign -> toggleSign()
            CalculatorAction.Percent -> performPercent()
            CalculatorAction.Ln -> performUnaryOperation(::ln)
            CalculatorAction.Log -> performUnaryOperation(::log10)
            CalculatorAction.Sin -> performUnaryOperation { sin(Math.toRadians(it)) }
            CalculatorAction.Cos -> performUnaryOperation { cos(Math.toRadians(it)) }
            CalculatorAction.Tan -> performUnaryOperation { tan(Math.toRadians(it)) }
            CalculatorAction.E -> setConstant(Math.E)
            CalculatorAction.Pi -> setConstant(Math.PI)
            CalculatorAction.Sqrt -> performUnaryOperation(::sqrt)
            CalculatorAction.Square -> performUnaryOperation { it.pow(2) }
            CalculatorAction.Cube -> performUnaryOperation { it.pow(3) }
            CalculatorAction.Factorial -> performFactorial()
            CalculatorAction.ClearHistory -> clearHistory()
            else -> {}
        }
    }

    private fun clearHistory() {
        _history.value = emptyList()
    }

    private fun performPercent() {
        if (state.number2.isNotBlank()) {
            val result = state.number2.toDouble() / 100
            state = state.copy(number2 = result.toString().take(15))
        } else if (state.number1.isNotBlank()) {
            val result = state.number1.toDouble() / 100
            state = state.copy(number1 = result.toString().take(15))
        }
    }

    private fun setConstant(value: Double) {
        if (state.operation == null && state.number1.isBlank()) {
            state = state.copy(number1 = value.toString())
        } else if (state.operation != null && state.number2.isBlank()) {
            state = state.copy(number2 = value.toString())
        }
    }

    private fun enterNumber(number: String) {
        if (state.operation == null) {
            state = state.copy(
                number1 = (state.number1 + number).take(15)
            )
        } else {
            state = state.copy(
                number2 = (state.number2 + number).take(15)
            )
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (currentInput.isNotEmpty()) {
            val lastChar = currentInput.lastOrNull()
            // If last character is an operator, replace it
            if (lastChar in listOf('+', '-', '×', 'x', '/', '÷', '^')) {
                currentInput = currentInput.dropLast(1) + operation.symbol
            } else {
                currentInput += operation.symbol
            }
            updateStateFromText(currentInput)
        }
    }

    private fun enterParenthesis(symbol: String) {
        currentInput += symbol
        updateStateFromText(currentInput)
    }

    private fun enterDecimal() {
        val currentNumber = if (state.operation == null) state.number1 else state.number2
        if (!currentNumber.contains(".")) {
            if (currentNumber.isEmpty()) {
                currentInput += "0."
            } else {
                currentInput += "."
            }
            updateStateFromText(currentInput)
        }
    }

    private fun performDeletion() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateStateFromText(currentInput)
        }
    }

    private fun performCalculation() {
        try {
            val result = evaluateExpression(currentInput)
            val expressionText = "$currentInput = $result"
            _history.value = listOf(expressionText) + _history.value.take(9)
            currentInput = result.toString().take(15)
            // Update state but keep the full expression for display
            state = state.copy(
                number1 = currentInput,
                number2 = "",
                operation = null,
                openParentheses = 0,
                expression = currentInput
            )
        } catch (e: Exception) {
            currentInput = ""
            state = state.copy(
                number1 = "Error",
                number2 = "",
                operation = null,
                openParentheses = 0,
                expression = ""
            )
        }
    }

    private fun evaluateExpression(expression: String): Double {
        try {
            var expr = expression
                .replace(" ", "")  // Remove all spaces
                .replace(Regex("π"), Math.PI.toString())
                .replace(Regex("e"), Math.E.toString())
                .replace(Regex("φ"), "1.618033988749895")  // Golden ratio
                .replace(Regex("%"), "/100")
                // Handle x² by converting to ^2 without extra parentheses
                .replace(Regex("\\u00B2"), "^2")
                // Handle 1/x by converting to (1/x) with proper grouping
                .replace(Regex("1/([\\d.]+(?:[eE][-+]?\\d+)?)"), "(1/$1)")
                // Replace × with * and ÷ with /
                .replace(Regex("×|x"), "*")
                .replace("÷", "/")
                // Handle scientific functions
                .replace(Regex("sin\\(([^)]+)\\)"), { match -> 
                    val angle = Math.toRadians(match.groupValues[1].toDouble())
                    Math.sin(angle).toString()
                })
                .replace(Regex("cos\\(([^)]+)\\)"), { match ->
                    val angle = Math.toRadians(match.groupValues[1].toDouble())
                    Math.cos(angle).toString()
                })
                .replace(Regex("tan\\(([^)]+)\\)"), { match ->
                    val angle = Math.toRadians(match.groupValues[1].toDouble())
                    Math.tan(angle).toString()
                })
                .replace(Regex("log\\(([^)]+)\\)"), { match ->
                    Math.log10(match.groupValues[1].toDouble()).toString()
                })
                .replace(Regex("ln\\(([^)]+)\\)"), { match ->
                    Math.log(match.groupValues[1].toDouble()).toString()
                })
                .replace(Regex("√\\(([^)]+)\\)"), { match ->
                    Math.sqrt(match.groupValues[1].toDouble()).toString()
                })

            // Handle power operations (^) with proper operator precedence
            while (true) {
                val powerMatch = Regex("(-?[\\d.]+(?:[eE][-+]?\\d+)?)\\^(-?[\\d.]+(?:[eE][-+]?\\d+)?)").find(expr) ?: break
                val base = powerMatch.groupValues[1].toDouble()
                val exponent = powerMatch.groupValues[2].toDouble()
                val result = Math.pow(base, exponent)
                // Replace just the matched part without adding parentheses
                expr = expr.replaceRange(powerMatch.range, result.toString())
            }

            // Handle negative numbers after power operations
            expr = expr.replace(Regex("(?<=[+\\-*/^(]|^)-"), "0-")
            
            // Evaluate the final expression
            return ExpressionBuilder(expr).build().evaluate()
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalArgumentException("Invalid expression")
        }
    }

    private fun evaluateSimpleExpression(expr: String): Double {
        // Handle basic arithmetic and constants
        var expression = expr

        // Tokenize and evaluate the expression
        val tokens = tokenize(expression)
        return evaluateTokens(tokens)
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var current = ""
        for (char in expression) {
            when (char) {
                '(', ')', '+', '-', '×', 'x', '/', '÷' -> {
                    if (current.isNotEmpty()) {
                        tokens.add(current)
                        current = ""
                    }
                    tokens.add(char.toString())
                }
                else -> current += char
            }
        }
        if (current.isNotEmpty()) tokens.add(current)
        return tokens
    }

    private fun evaluateTokens(tokens: List<String>): Double {
        val stack = mutableListOf<Double>()
        val operators = mutableListOf<String>()

        for (token in tokens) {
            when (token) {
                "(" -> operators.add(token)
                ")" -> {
                    while (operators.isNotEmpty() && operators.last() != "(") {
                        applyOperator(stack, operators.removeAt(operators.lastIndex))
                    }
                    if (operators.isNotEmpty()) operators.removeAt(operators.lastIndex)
                }
                "+", "-", "×", "x", "/", "÷" -> {
                    while (operators.isNotEmpty() && operators.last() != "(" &&
                        precedence(operators.last()) >= precedence(token)
                    ) {
                        applyOperator(stack, operators.removeAt(operators.lastIndex))
                    }
                    operators.add(token)
                }
                else -> token.toDoubleOrNull()?.let { stack.add(it) }
            }
        }

        while (operators.isNotEmpty()) {
            applyOperator(stack, operators.removeAt(operators.lastIndex))
        }

        return stack.lastOrNull() ?: 0.0
    }

    private fun precedence(operator: String) = when (operator) {
        "×", "x", "/", "÷" -> 2
        "+", "-" -> 1
        else -> 0
    }

    private fun applyOperator(stack: MutableList<Double>, operator: String) {
        if (stack.size < 2) return
        val b = stack.removeAt(stack.lastIndex)
        val a = stack.removeAt(stack.lastIndex)

        val result = when (operator) {
            "+" -> a + b
            "-" -> a - b
            "×", "x" -> a * b
            "/", "÷" -> a / b
            else -> 0.0
        }

        stack.add(result)
    }

    internal fun updateStateFromText(text: String) {
        val parts = text.split(Regex("[+\\-*/]"))
        state = if (parts.size == 1) {
            state.copy(number1 = parts[0])
        } else {
            state.copy(
                number1 = parts[0],
                operation = when {
                    text.contains('+') -> CalculatorOperation.Add
                    text.contains('-') -> CalculatorOperation.Subtract
                    text.contains('×') || text.contains('x') -> CalculatorOperation.Multiply
                    text.contains('÷') || text.contains('/') -> CalculatorOperation.Divide
                    else -> null
                },
                number2 = parts[1]
            )
        }
    }

    private fun performUnaryOperation(operation: (Double) -> Double) {
        if (currentInput.isNotEmpty()) {
            try {
                val number = currentInput.toDouble()
                currentInput = operation(number).toString()
                updateStateFromText(currentInput)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun performFactorial() {
        fun factorial(n: Double): Double {
            return if (n <= 1) 1.0 else n * factorial(n - 1)
        }

        if (state.operation == null) {
            val number = state.number1.toDoubleOrNull() ?: 0.0
            val result = if (number >= 0 && number == number.toInt().toDouble()) {
                factorial(number)
            } else {
                Double.NaN
            }
            currentInput = result.toString()
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = state.number2.toDoubleOrNull() ?: 0.0
            val result = if (number >= 0 && number == number.toInt().toDouble()) {
                factorial(number)
            } else {
                Double.NaN
            }
            currentInput = state.number1 + state.operation?.symbol + result.toString()
            state = state.copy(number2 = result.toString())
        }
    }

    private fun toggleSign() {
        if (state.number2.isNotBlank()) {
            val number = state.number2.toDoubleOrNull()
            if (number != null) {
                state = state.copy(
                    number2 = if (number > 0) "-${state.number2}" else state.number2.drop(1)
                )
            }
        } else if (state.number1.isNotBlank()) {
            val number = state.number1.toDoubleOrNull()
            if (number != null) {
                state = state.copy(
                    number1 = if (number > 0) "-${state.number1}" else state.number1.drop(1)
                )
            }
        }
    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}
