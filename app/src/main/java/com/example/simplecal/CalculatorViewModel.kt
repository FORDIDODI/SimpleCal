package com.example.simplecal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

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
            is CalculatorAction.Parenthesis -> {
                currentInput += action.symbol
                updateStateFromText(currentInput)
            }
            is CalculatorAction.Operation -> {
                if (currentInput.isNotEmpty()) {
                    val lastChar = currentInput.last()
                    if (lastChar.isDigit() || lastChar == ')') {
                        currentInput += action.operation.symbol
                    } else if (lastChar in "+-*/×÷") {
                        if (currentInput.length > 1 && currentInput[currentInput.length - 2] != '(') {
                            currentInput = currentInput.dropLast(1) + action.operation.symbol
                        }
                    }
                    updateStateFromText(currentInput)
                }
            }
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> {
                state = CalculatorState()
                currentInput = ""
            }
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
            is CalculatorAction.Percent -> applyPercent()
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
        }
    }

    private fun performDeletion() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateStateFromText(currentInput)
        }
    }

    private fun handleParentheses() {
        if (state.operation == null) {
            state = state.copy(
                number1 = state.number1 + "(",
                openParentheses = state.openParentheses + 1
            )
        } else {
            if (state.openParentheses > 0) {
                state = state.copy(
                    number2 = state.number2 + ")",
                    openParentheses = state.openParentheses - 1
                )
            } else {
                state = state.copy(
                    number2 = state.number2 + "(",
                    openParentheses = state.openParentheses + 1
                )
            }
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

    private fun enterDecimal() {
        if (state.operation == null && !state.number1.contains(".")) {
            state = state.copy(number1 = state.number1 + ".")
        } else if (!state.number2.contains(".")) {
            state = state.copy(number2 = state.number2 + ".")
        }
    }

    private fun applyPercent() { /* unchanged */ }
    private fun toggleSign() { /* unchanged */ }
    private fun applySqrt() { /* unchanged */ }
    private fun applySquare() { /* unchanged */ }
    private fun applyReciprocal() { /* unchanged */ }
    private fun enterPi() { /* unchanged */ }
    private fun applyTrig(type: String) { /* unchanged */ }
    private fun applyLog() { /* unchanged */ }
    private fun applyLn() { /* unchanged */ }
    private fun applyExp() { /* unchanged */ }
    private fun applyFactorial() { /* unchanged */ }
    private fun applyPower() { /* unchanged */ }
    private fun enterE() { /* unchanged */ }

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
        val operators = setOf("+", "-", "×", "x", "/", "÷")
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
