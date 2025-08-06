package com.example.simplecal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

class CalculatorViewModel : ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    val history = mutableStateOf<List<String>>(emptyList())

    var isScientificMode by mutableStateOf(false)
        private set

    var currentInput by mutableStateOf("")

    fun toggleScientificMode() {
        isScientificMode = !isScientificMode
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
            is CalculatorAction.Parenthesis -> {
                currentInput += action.symbol
                updateStateFromText(currentInput)
            }
            is CalculatorAction.Operation -> {
                if (currentInput.isNotEmpty()) {
                    val lastChar = currentInput.lastOrNull()
                    if (lastChar != null && (lastChar.isDigit() || lastChar == ')')) {
                        currentInput += action.operation.symbol
                    } else if (lastChar != null && lastChar in "+-*/×÷") {
                        if (currentInput.length > 1 && currentInput.getOrNull(currentInput.length - 2) != '(') {
                            currentInput = currentInput.dropLast(1) + action.operation.symbol
                        }
                    }
                    updateStateFromText(currentInput)
                }
            }
            is CalculatorAction.Clear -> {
                state = CalculatorState()
                currentInput = ""
            }
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
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
            is CalculatorAction.Sqrt -> applySqrt()
            is CalculatorAction.Square -> applySquare()
            is CalculatorAction.Reciprocal -> applyReciprocal()
            is CalculatorAction.NumberPi -> enterPi()
            is CalculatorAction.Sin -> applyTrig("sin")
            is CalculatorAction.Cos -> applyTrig("cos")
            is CalculatorAction.Tan -> applyTrig("tan")
            is CalculatorAction.Log -> applyLog()
            is CalculatorAction.Ln -> applyLn()
            is CalculatorAction.Exp -> applyExp()
            is CalculatorAction.Factorial -> applyFactorial()
            is CalculatorAction.Power -> applyPower()
            is CalculatorAction.NumberE -> enterE()
            is CalculatorAction.Parentheses -> handleParentheses()
            is CalculatorAction.CloseParenthesis -> handleCloseParenthesis()
            is CalculatorAction.Cube -> applyCube()
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
            history.value = listOf(expressionText) + history.value.take(9)
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
        // First, handle percentages
        var expr = expression.replace("%", "/100")
        
        // Handle negative numbers by adding 0 before negative signs that are at the start or after an operator
        expr = expr.replace(Regex("(?<=[+\\-×x/÷(]|^)(?=-\\d)"), "0")
        
        // Handle percentage calculations first
        val processedExpression = if ('%' in expr) {
            val parts = expr.split('+')
            if (parts.size == 2 && parts[1].trim().endsWith('%')) {
                val base = parts[0].trim().toDoubleOrNull() ?: 0.0
                val percent = parts[1].trim().removeSuffix("%").toDoubleOrNull() ?: 0.0
                return base * (1 + percent / 100)
            }
            expr.replace("%", "").toDoubleOrNull()?.div(100)?.toString() ?: expr
        } else {
            expr
        }

        val tokens = tokenize(processedExpression)
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

    private fun applyLog() {
        currentInput += "log("
        state = state.copy(
            number1 = if (state.operation == null) currentInput else state.number1,
            expression = currentInput
        )
    }

    private fun applyTrig(type: String) {
        currentInput += "$type("
        state = state.copy(
            number1 = if (state.operation == null) currentInput else state.number1,
            expression = currentInput
        )
    }

    private fun applySqrt() {
        currentInput += "√("
        state = state.copy(
            number1 = if (state.operation == null) currentInput else state.number1,
            expression = currentInput
        )
    }

    private fun applyLn() {
        if (state.operation == null) {
            val number = state.number1.toDoubleOrNull() ?: 0.0
            val result = if (number > 0) ln(number) else Double.NaN
            currentInput = result.toString()
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = state.number2.toDoubleOrNull() ?: 0.0
            val result = if (number > 0) ln(number) else Double.NaN
            currentInput = state.number1 + state.operation?.symbol + result.toString()
            state = state.copy(number2 = result.toString())
        }
    }

    private fun applyPercent() {
        if (state.operation == null) {
            val number = state.number1.toDoubleOrNull() ?: 0.0
            val result = number / 100
            currentInput = result.toString()
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = state.number2.toDoubleOrNull() ?: 0.0
            val percentage = state.number1.toDoubleOrNull() ?: 0.0
            val result = percentage * (number / 100)
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
        if (state.operation == null) {
            val number = state.number1.toDoubleOrNull() ?: 0.0
            val result = number * number
            currentInput = result.toString()
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = state.number2.toDoubleOrNull() ?: 0.0
            val result = number * number
            currentInput = state.number1 + state.operation?.symbol + result.toString()
            state = state.copy(number2 = result.toString())
        }
    }

    private fun applyReciprocal() {
        if (state.operation == null) {
            val number = state.number1.toDoubleOrNull() ?: 0.0
            val result = if (number != 0.0) 1.0 / number else Double.POSITIVE_INFINITY
            currentInput = result.toString()
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = state.number2.toDoubleOrNull() ?: 0.0
            val result = if (number != 0.0) 1.0 / number else Double.POSITIVE_INFINITY
            currentInput = state.number1 + state.operation?.symbol + result.toString()
            state = state.copy(number2 = result.toString())
        }
    }

    private fun enterPi() {
        val piValue = "3.141592653589793"
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
