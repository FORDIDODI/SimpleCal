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
            is CalculatorAction.Number -> {
                currentInput += action.number
                updateStateFromText(currentInput)
            }
            is CalculatorAction.Decimal -> {
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
            is CalculatorAction.Clear -> {
                currentInput = ""
                state = CalculatorState()
            }
            is CalculatorAction.ClearHistory -> {
                _history.value = emptyList()
            }
            is CalculatorAction.LoadFromHistory -> {
                // Extract just the result part from the history entry (part after '=')
                val result = action.entry.split(" = ").lastOrNull() ?: ""
                currentInput = result
                updateStateFromText(currentInput)
            }
            is CalculatorAction.Parenthesis -> {
                currentInput += action.symbol
                updateStateFromText(currentInput)
            }
            is CalculatorAction.Operation -> {
                if (currentInput.isNotEmpty()) {
                    val lastChar = currentInput.lastOrNull()
                    // If last character is an operator, replace it
                    if (lastChar in listOf('+', '-', '×', 'x', '/', '÷', '^')) {
                        currentInput = currentInput.dropLast(1) + action.operation.symbol
                    } else {
                        currentInput += action.operation.symbol
                    }
                    updateStateFromText(currentInput)
                }
            }
            is CalculatorAction.Calculate -> {
                try {
                    val result = evaluateExpression(currentInput)
                    val expressionText = "$currentInput = $result"
                    _history.value = listOf(expressionText) + _history.value.take(19) // Keep last 20 entries
                    currentInput = result.toString()
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
            is CalculatorAction.Delete -> {
                if (currentInput.isNotEmpty()) {
                    currentInput = currentInput.dropLast(1)
                    updateStateFromText(currentInput)
                }
            }
            is CalculatorAction.Percent -> {
                if (currentInput.isEmpty()) return@onAction

                try {
                    // If there's an operation, calculate percentage of the first number
                    if (state.operation != null && state.number2.isNotEmpty()) {
                        val num1 = state.number1.toDoubleOrNull() ?: 0.0
                        val num2 = state.number2.toDoubleOrNull() ?: 0.0
                        val percentValue = (num1 * num2) / 100
                        currentInput = "$currentInput%"
                        state = state.copy(
                            number2 = percentValue.toString(),
                            expression = currentInput
                        )
                    } else {
                        // If no operation, convert to decimal
                        val number = currentInput.toDoubleOrNull() ?: 0.0
                        val decimalValue = number / 100
                        currentInput = decimalValue.toString()
                        updateStateFromText(currentInput)
                    }
                } catch (e: Exception) {
                    currentInput = "Error"
                    updateStateFromText(currentInput)
                }
            }
            is CalculatorAction.ToggleSign -> toggleSign()
            is CalculatorAction.Sin -> appendFunction("sin(")
            is CalculatorAction.Cos -> appendFunction("cos(")
            is CalculatorAction.Tan -> appendFunction("tan(")
            is CalculatorAction.Log -> appendFunction("log(")
            is CalculatorAction.Ln -> appendFunction("ln(")
            is CalculatorAction.Sqrt -> appendFunction("√(")
            is CalculatorAction.Exp -> applyExp()
            is CalculatorAction.Factorial -> applyFactorial()
            is CalculatorAction.Power -> applyPower()
            is CalculatorAction.NumberE -> enterE()
            is CalculatorAction.Parentheses -> handleParentheses()
            is CalculatorAction.CloseParenthesis -> handleCloseParenthesis()
            is CalculatorAction.Cube -> applyCube()
            is CalculatorAction.Square -> applySquare()
            is CalculatorAction.Reciprocal -> applyReciprocal()
            is CalculatorAction.NumberPi -> enterPi()
            else -> {}
        }
    }

    private fun handleCloseParenthesis() {
        val lastChar = currentInput.lastOrNull()
        val lastNumber = state.number2.ifEmpty { state.number1 }
        val operators = setOf('+', '-', '×', 'x', '/', '÷', '^', '(')

        // If there are open parentheses and we have a number, add closing parenthesis
        if (state.openParentheses > 0 && lastNumber.isNotEmpty() && lastNumber.last().isDigit()) {
            currentInput += ")"
            state = state.copy(
                number1 = if (state.operation == null) state.number1 + ")" else state.number1,
                number2 = if (state.operation != null) state.number2 + ")" else state.number2,
                openParentheses = state.openParentheses - 1,
                expression = currentInput
            )
        }
    }

    private fun performDeletion() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateStateFromText(currentInput)
        }
    }

    private fun handleParentheses() {
        val lastChar = currentInput.lastOrNull()
        val lastNumber = state.number2.ifEmpty { state.number1 }
        val operators = setOf('+', '-', '×', 'x', '/', '÷', '^', '(')

        // If no open parentheses or last character is an operator, add opening parenthesis
        if (state.openParentheses == 0 || (lastChar != null && lastChar in operators)) {
            currentInput += "("
            state = state.copy(
                number1 = if (state.operation == null) state.number1 + "(" else state.number1,
                number2 = if (state.operation != null) state.number2 + "(" else state.number2,
                openParentheses = state.openParentheses + 1,
                expression = currentInput
            )
        }
        // If there are open parentheses and we have a number, add closing parenthesis
        else if (state.openParentheses > 0 && lastNumber.isNotEmpty() && lastNumber.last().isDigit()) {
            currentInput += ")"
            state = state.copy(
                number1 = if (state.operation == null) state.number1 + ")" else state.number1,
                number2 = if (state.operation != null) state.number2 + ")" else state.number2,
                openParentheses = state.openParentheses - 1,
                expression = currentInput
            )
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
            expr = expr.replace(Regex("(?<=[+\\-×/^(]|^)-"), "0-")
            // Handle remaining simple expressions
            return evaluateSimpleExpression(expr)
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

    private fun appendFunction(functionName: String) {
        currentInput += functionName
        updateStateFromText(currentInput)
    }

    private fun applyExp() {
        if (state.operation == null) {
            val number = state.number1.toDoubleOrNull() ?: 0.0
            val result = exp(number)
            currentInput = result.toString()
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = state.number2.toDoubleOrNull() ?: 0.0
            val result = exp(number)
            currentInput = state.number1 + state.operation?.symbol + result.toString()
            state = state.copy(number2 = result.toString())
        }
    }

    private fun toggleSign() {
        if (state.operation == null) {
            val number = state.number1.toDoubleOrNull() ?: 0.0
            val result = -number
            currentInput = result.toString()
            state = state.copy(number1 = currentInput)
        } else {
            val number = state.number2.toDoubleOrNull() ?: 0.0
            val result = -number
            currentInput = state.number1 + state.operation?.symbol + result.toString()
            state = state.copy(number2 = result.toString())
        }
    }

    private fun applySquare() {
        if (currentInput.isEmpty()) {
            currentInput = "0"
        }
        
        // If the last character is a digit, add the square operation
        if (currentInput.lastOrNull()?.isDigit() == true || currentInput.lastOrNull() == ')') {
            currentInput = "($currentInput)²"
            updateStateFromText(currentInput)
        }
        // If we have an operation and number2 is not empty, square the second number
        else if (state.operation != null && state.number2.isNotEmpty()) {
            currentInput = "${state.number1}${state.operation?.symbol}(${state.number2})²"
            updateStateFromText(currentInput)
        }
    }

    private fun applyReciprocal() {
        if (currentInput.isEmpty()) {
            currentInput = "1/"
            updateStateFromText(currentInput)
            return
        }
        
        // If we have a number, wrap it in parentheses and add 1/
        if (currentInput.lastOrNull()?.isDigit() == true || currentInput.lastOrNull() == ')') {
            currentInput = "1/($currentInput)"
            updateStateFromText(currentInput)
        }
        // If we have an operation and number2 is not empty, take reciprocal of the second number
        else if (state.operation != null && state.number2.isNotEmpty()) {
            currentInput = "${state.number1}${state.operation?.symbol}1/(${state.number2})"
            updateStateFromText(currentInput)
        }
    }

    private fun enterPi() {
        val piValue = "π"
        if (state.operation == null) {
            currentInput = piValue
            state = state.copy(number1 = piValue, number2 = "", operation = null)
        } else {
            currentInput = state.number1 + state.operation?.symbol + piValue
            state = state.copy(number2 = piValue)
        }
    }

    private fun applyFactorial() {
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

    private fun applyPower() {
        if (state.operation == null) {
            val number = state.number1.toDoubleOrNull() ?: 0.0
            currentInput = "$number^"
            state = state.copy(operation = CalculatorOperation.Power, number1 = number.toString())
        } else {
            val base = state.number1.toDoubleOrNull() ?: 0.0
            val exponent = state.number2.toDoubleOrNull() ?: 0.0
            val result = base.pow(exponent)
            currentInput = result.toString()
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        }
    }

    private fun enterE() {
        val eValue = "2.718281828459045"
        if (state.operation == null) {
            currentInput = eValue
            state = state.copy(number1 = eValue, number2 = "", operation = null)
        } else {
            currentInput = state.number1 + state.operation?.symbol + eValue
            state = state.copy(number2 = eValue)
        }
    }

    private fun applyCube() {
        if (state.operation == null) {
            val number = state.number1.toDoubleOrNull() ?: 0.0
            val result = number * number * number
            currentInput = result.toString()
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = state.number2.toDoubleOrNull() ?: 0.0
            val result = number * number * number
            currentInput = state.number1 + state.operation?.symbol + result.toString()
            state = state.copy(number2 = result.toString())
        }
    }

    fun updateStateFromText(text: String) {
        if (text.isEmpty()) {
            state = CalculatorState()
            return
        }

        // Keep the full expression in the state for display
        state = state.copy(expression = text)

        // For calculation purposes, we still need to parse the numbers and operation
        var parenthesesCount = 0
        var currentNumber1 = ""
        var currentNumber2 = ""
        var currentOperation: String? = null
        val operators = setOf('+', '-', '×', 'x', '/', '÷')
        var lastWasOperator = false

        // Find the last operator that's not inside parentheses
        var lastOpIndex = -1
        var parenLevel = 0

        for (i in text.indices) {
            when (text[i]) {
                '(' -> parenLevel++
                ')' -> parenLevel--
                in "+-×x/÷" -> if (parenLevel == 0) lastOpIndex = i
            }
        }

        if (lastOpIndex != -1) {
            currentNumber1 = text.substring(0, lastOpIndex)
            currentOperation = text[lastOpIndex].toString()
            currentNumber2 = text.substring(lastOpIndex + 1)
        } else {
            currentNumber1 = text
        }

        // Count open parentheses for the state
        parenthesesCount = text.count { it == '(' } - text.count { it == ')' }

        val operation = when (currentOperation) {
            "+" -> CalculatorOperation.Add
            "-" -> CalculatorOperation.Subtract
            "×", "x" -> CalculatorOperation.Multiply
            "/", "÷" -> CalculatorOperation.Divide
            else -> null
        }

        state = state.copy(
            number1 = currentNumber1,
            number2 = currentNumber2,
            operation = operation,
            openParentheses = maxOf(0, parenthesesCount)  // Ensure we don't have negative count
        )
    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}
