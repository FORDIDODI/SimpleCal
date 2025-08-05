package com.example.simplecal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt

class CalculatorViewModel: ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    val history = mutableStateOf<List<String>>(emptyList())

    var isScientificMode by mutableStateOf(false)
        private set

    fun toggleScientificMode() {
        isScientificMode = !isScientificMode
    }

    fun onAction(action: CalculatorAction) {
        when(action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> state = CalculatorState()
            is CalculatorAction.Operation -> enterOperation(action.operation)
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
        when {
            state.number2.isNotBlank() -> state = state.copy(
                number2 = state.number2.dropLast(1)
            )
            state.operation != null -> state = state.copy(
                operation = null
            )
            state.number1.isNotBlank() -> state = state.copy(
                number1 = state.number1.dropLast(1)
            )
            state.operation != null -> state = state.copy(
                operation = null
            )
            state.number1.isNotBlank() -> state = state.copy(
                number1 = state.number1.dropLast(1)
            )
        }
    }

    private fun handleParentheses() {
        if (state.operation == null) {
            // Jika belum ada operasi, tambah kurung buka
            state = state.copy(
                number1 = state.number1 + "(",
                openParentheses = state.openParentheses + 1
            )
        } else {
            // Jika sudah ada operasi, cek apakah bisa tutup kurung
            if (state.openParentheses > 0) {
                state = state.copy(
                    number2 = state.number2 + ")",
                    openParentheses = state.openParentheses - 1
                )
            } else {
                // Jika tidak ada kurung buka, tambah kurung buka
                state = state.copy(
                    number2 = state.number2 + "(",
                    openParentheses = state.openParentheses + 1
                )
            }
        }
    }

    private fun performCalculation() {
        val expression = state.number1 + (state.operation?.symbol ?: "") + state.number2
        if (expression.isNotBlank()) {
            try {
                val result = evaluateExpression(expression)
                val expressionText = "$expression = $result"
                history.value = listOf(expressionText) + history.value.take(9)
                state = state.copy(
                    number1 = result.toString().take(15),
                    number2 = "",
                    operation = null,
                    openParentheses = 0
                )
            } catch (e: Exception) {
                // Handle error
                state = state.copy(
                    number1 = "Error",
                    number2 = "",
                    operation = null,
                    openParentheses = 0
                )
            }
        }
    }

    private fun evaluateExpression(expression: String): Double {
        // Simple expression evaluator with parentheses and operator precedence
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
        if (current.isNotEmpty()) {
            tokens.add(current)
        }
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
                    if (operators.isNotEmpty()) {
                        operators.removeAt(operators.lastIndex) // Remove "("
                    }
                }
                "+", "-", "×", "x", "/", "÷" -> {
                    while (operators.isNotEmpty() && 
                           operators.last() != "(" && 
                           precedence(operators.last()) >= precedence(token)) {
                        applyOperator(stack, operators.removeAt(operators.lastIndex))
                    }
                    operators.add(token)
                }
                else -> {
                    val number = token.toDoubleOrNull()
                    if (number != null) {
                        stack.add(number)
                    }
                }
            }
        }
        
        while (operators.isNotEmpty()) {
            applyOperator(stack, operators.removeAt(operators.lastIndex))
        }
        
        return stack.lastOrNull() ?: 0.0
    }

    private fun precedence(operator: String): Int {
        return when (operator) {
            "×", "x", "/", "÷" -> 2
            "+", "-" -> 1
            else -> 0
        }
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


    private fun enterOperation(operation: CalculatorOperation) {
        if(state.number1.isNotBlank()) {
            // Jika sudah ada operasi dan number2 kosong, ganti operasi
            if (state.operation != null && state.number2.isBlank()) {
                state = state.copy(operation = operation)
            } else {
                // Jika belum ada operasi atau number2 sudah diisi, set operasi baru
                state = state.copy(operation = operation)
            }
        }
    }

    private fun enterDecimal() {
       if (state.operation == null && !state.number1.contains(".")
           && state.number1.isNotBlank()) {
           state = state.copy(
               number1 = state.number1 + "."
           )
           return
       }
        if (!state.number2.contains(".") && state.number2.isNotBlank()) {
            state = state.copy(
                number1 = state.number2 + "."
            )
        }
    }

    private fun enterNumber(number: Int) {
        if (state.operation == null) {
            if (state.number1.length >= MAX_NUM_LENGTH) {
                return
            }
            state = state.copy(
                number1 = state.number1 + number
            )
            return
        }
        if (state.number2.length >= MAX_NUM_LENGTH) {
            return
        }
        state = state.copy(
            number2 = state.number2 + number
        )
    }

    private fun applyPercent() {
        if (state.operation == null) {
            val num1 = state.number1.toDoubleOrNull()
            if (num1 != null) {
                state = state.copy(number1 = (num1 / 100).toString(), number2 = "")
            }
        } else {
            val num1 = state.number1.toDoubleOrNull()
            val num2 = state.number2.toDoubleOrNull()
            if (num1 != null && num2 != null) {
                // number2 menjadi persentase dari number1
                val percentValue = num1 * (num2 / 100)
                state = state.copy(number2 = percentValue.toString())
            }
        }
    }

    private fun toggleSign() {
        if (state.operation == null) {
            val num1 = state.number1.toDoubleOrNull()
            if (num1 != null) {
                val toggled = -num1
                state = state.copy(number1 = toggled.toString().removeSuffix(".0"))
            }
        } else {
            val num2 = state.number2.toDoubleOrNull()
            if (num2 != null) {
                val toggled = -num2
                state = state.copy(number2 = toggled.toString().removeSuffix(".0"))
            }
        }
    }

    private fun applySqrt() {
        val number = state.number1.toDoubleOrNull()
        if (number != null && number >= 0) {
            val result = sqrt(number)
            state = state.copy(number1 = result.toString().take(15), number2 = "", operation = null)
        }
    }

    private fun applySquare() {
        val number = state.number1.toDoubleOrNull()
        if (number != null) {
            val result = number * number
            state = state.copy(number1 = result.toString().take(15), number2 = "", operation = null)
        }
    }

    private fun applyReciprocal() {
        val number = state.number1.toDoubleOrNull()
        if (number != null && number != 0.0) {
            val result = 1 / number
            state = state.copy(number1 = result.toString().take(15), number2 = "", operation = null)
        }
    }

    private fun enterPi() {
        val piValue = Math.PI.toString().take(15)
        state = state.copy(number1 = piValue, number2 = "", operation = null)
    }

    // Scientific: hanya menambah fungsi ke input, bukan langsung menghitung
    private fun applyTrig(type: String) {
        // Tambahkan fungsi ke input, misal sin(, cos(, dst
        val current = if (state.operation == null) state.number1 else state.number2
        val newValue = "$type("
        if (state.operation == null) {
            state = state.copy(number1 = newValue)
        } else {
            state = state.copy(number2 = newValue)
        }
    }
    private fun applyLog() {
        val current = if (state.operation == null) state.number1 else state.number2
        val newValue = "log("
        if (state.operation == null) {
            state = state.copy(number1 = newValue)
        } else {
            state = state.copy(number2 = newValue)
        }
    }
    private fun applyLn() {
        val current = if (state.operation == null) state.number1 else state.number2
        val newValue = "ln("
        if (state.operation == null) {
            state = state.copy(number1 = newValue)
        } else {
            state = state.copy(number2 = newValue)
        }
    }
    private fun applyExp() {
        val number = state.number1.toDoubleOrNull()
        if (number != null && number > 0) {
            val result = kotlin.math.exp(number)
            state = state.copy(number1 = result.toString().take(15), number2 = "", operation = null)
        }
    }
    private fun applyFactorial() {
        val number = state.number1.toDoubleOrNull()
        if (number != null && number > 0 && number == number.toInt().toDouble()) {
            val intNumber = number.toInt()
            val result = (1..intNumber).fold(1L) { acc, i -> acc * i}
            state = state.copy(number1 = result.toString(), number2 = "", operation = null)
        }
    }

    private fun applyPower() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()
        if (number1 != null && number2 != null) {
            val result = Math.pow(number1, number2)
            state = state.copy(number1 = result.toString().take(15), number2 = "", operation = null)
        }
    }
    private fun enterE() {
        val eValue = Math.E.toString().take(15)
        state = state.copy(number1 = eValue, number2 = "", operation = null)
    }

    fun updateStateFromText(text: String) {
        // Parse text untuk mengupdate state kalkulator
        val operators = listOf("+", "-", "×", "x", "/", "÷")
        var currentNumber1 = ""
        var currentOperation: String? = null
        var currentNumber2 = ""
        var inParentheses = false
        var parenthesesCount = 0
        
        var i = 0
        while (i < text.length) {
            val char = text[i].toString()
            when {
                char == "(" -> {
                    parenthesesCount++
                    inParentheses = true
                    if (currentOperation == null) {
                        currentNumber1 += char
                    } else {
                        currentNumber2 += char
                    }
                    i++
                }
                char == ")" -> {
                    parenthesesCount--
                    if (parenthesesCount == 0) {
                        inParentheses = false
                    }
                    if (currentOperation == null) {
                        currentNumber1 += char
                    } else {
                        currentNumber2 += char
                    }
                    i++
                }
                operators.contains(char) && !inParentheses -> {
                    if (currentOperation == null) {
                        currentOperation = char
                        i++
                    } else {
                        // Jika sudah ada operasi, ganti operasi
                        currentOperation = char
                        i++
                    }
                }
                else -> {
                    if (currentOperation == null) {
                        currentNumber1 += char
                    } else {
                        currentNumber2 += char
                    }
                    i++
                }
            }
        }
        
        // Update state berdasarkan hasil parsing
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
            operation = operation
        )
    }


    companion object {
        private const val MAX_NUM_LENGTH = 8
    }

}