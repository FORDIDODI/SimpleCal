package com.example.simplecal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplecal.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.exp
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
        viewModelScope.launch { _themeMode.emit(newTheme) }
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
                    currentInput += if (currentNumber.isEmpty()) "0." else "."
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
                    if (lastChar in listOf('+', '-', '×', 'x', '/', '÷', '^')) {
                        currentInput = currentInput.dropLast(1) + action.operation.symbol
                    } else {
                        currentInput += action.operation.symbol
                    }
                    updateStateFromText(currentInput)
                }
            }
            is CalculatorAction.Calculate -> {
                performCalculation()
            }
            is CalculatorAction.Delete -> {
                if (currentInput.isNotEmpty()) {
                    currentInput = currentInput.dropLast(1)
                    updateStateFromText(currentInput)
                }
            }
            is CalculatorAction.Percent -> {
                if (currentInput.isEmpty()) return
                try {
                    if (state.operation != null && state.number2.isNotEmpty()) {
                        val num1 = evaluateExpression(state.number1)
                        val num2 = evaluateExpression(state.number2)
                        val percentValue = (num1 * num2) / 100
                        currentInput = "${state.number1}${state.operation?.symbol}$percentValue"
                        state = state.copy(number2 = percentValue.toString(), expression = currentInput)
                    } else {
                        val number = evaluateExpression(currentInput)
                        val decimalValue = number / 100
                        currentInput = trimResult(decimalValue)
                        updateStateFromText(currentInput)
                    }
                } catch (_: Exception) {
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
        }
    }

    private fun trimResult(value: Double): String {
        val asLong = value.toLong()
        return if (value.isNaN() || !value.isFinite()) "Error"
        else if (value == asLong.toDouble()) asLong.toString() else {
            val s = "%,.10f".format(value).replace(",", "")
            s.trimEnd('0').trimEnd('.')
        }
    }

    private fun performCalculation() {
        try {
            val result = evaluateExpression(currentInput)
            val shown = trimResult(result)
            val expressionText = "$currentInput = $shown"
            _history.value = listOf(expressionText) + _history.value.take(19)
            currentInput = shown
            state = state.copy(
                number1 = currentInput,
                number2 = "",
                operation = null,
                openParentheses = 0,
                expression = currentInput
            )
        } catch (_: Exception) {
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
                .replace(" ", "")
                .replace(Regex("π"), Math.PI.toString())
                .replace(Regex("e(?![a-zA-Z])"), Math.E.toString())
                .replace(Regex("φ"), "1.618033988749895")
                .replace(Regex("%"), "/100")
                .replace(Regex("\\u00B2"), "^2")
                .replace(Regex("1/([\\d.]+(?:[eE][-+]?\\d+)?)"), "(1/$1)")
                .replace(Regex("×|x"), "*")
                .replace("÷", "/")
                .replace(Regex("sin\\(([^)]+)\\)")) { match ->
                    val angle = Math.toRadians(evaluateExpression(match.groupValues[1]))
                    kotlin.math.sin(angle).toString()
                }
                .replace(Regex("cos\\(([^)]+)\\)")) { match ->
                    val angle = Math.toRadians(evaluateExpression(match.groupValues[1]))
                    kotlin.math.cos(angle).toString()
                }
                .replace(Regex("tan\\(([^)]+)\\)")) { match ->
                    val angle = Math.toRadians(evaluateExpression(match.groupValues[1]))
                    kotlin.math.tan(angle).toString()
                }
                .replace(Regex("log\\(([^)]+)\\)")) { match ->
                    kotlin.math.log10(evaluateExpression(match.groupValues[1])).toString()
                }
                .replace(Regex("ln\\(([^)]+)\\)")) { match ->
                    kotlin.math.ln(evaluateExpression(match.groupValues[1])).toString()
                }
                .replace(Regex("√\\(([^)]+)\\)")) { match ->
                    kotlin.math.sqrt(evaluateExpression(match.groupValues[1])).toString()
                }

            while (true) {
                val powerMatch = Regex("(-?[\\d.]+(?:[eE][-+]?\\d+)?)\\^(-?[\\d.]+(?:[eE][-+]?\\d+)?)").find(expr) ?: break
                val base = powerMatch.groupValues[1].toDouble()
                val exponent = powerMatch.groupValues[2].toDouble()
                val result = Math.pow(base, exponent)
                expr = expr.replaceRange(powerMatch.range, result.toString())
            }

            expr = expr.replace(Regex("(?<=[+\\-*/^(]|^)-"), "0-")
            return ExpressionBuilder(expr).build().evaluate()
        } catch (_: Exception) {
            throw IllegalArgumentException("Invalid expression")
        }
    }

    private fun appendFunction(functionName: String) {
        currentInput += functionName
        updateStateFromText(currentInput)
    }

    private fun applyExp() {
        if (state.operation == null) {
            val number = evaluateSafe(state.number1)
            val result = exp(number)
            currentInput = trimResult(result)
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = evaluateSafe(state.number2)
            val result = exp(number)
            currentInput = state.number1 + (state.operation?.symbol ?: "") + trimResult(result)
            state = state.copy(number2 = trimResult(result))
        }
    }

    private fun toggleSign() {
        if (state.operation == null) {
            val number = evaluateSafe(state.number1)
            val result = -number
            currentInput = trimResult(result)
            state = state.copy(number1 = currentInput)
        } else {
            val number = evaluateSafe(state.number2)
            val result = -number
            currentInput = state.number1 + (state.operation?.symbol ?: "") + trimResult(result)
            state = state.copy(number2 = trimResult(result))
        }
    }

    private fun applySquare() {
        if (currentInput.isEmpty()) currentInput = "0"
        if (currentInput.lastOrNull()?.isDigit() == true || currentInput.lastOrNull() == ')') {
            currentInput = "($currentInput)²"
            updateStateFromText(currentInput)
        } else if (state.operation != null && state.number2.isNotEmpty()) {
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
        if (currentInput.lastOrNull()?.isDigit() == true || currentInput.lastOrNull() == ')') {
            currentInput = "1/($currentInput)"
            updateStateFromText(currentInput)
        } else if (state.operation != null && state.number2.isNotEmpty()) {
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
            currentInput = state.number1 + (state.operation?.symbol ?: "") + piValue
            state = state.copy(number2 = piValue)
        }
    }

    private fun applyFactorial() {
        fun factorial(n: Int): Double {
            if (n < 0) return Double.NaN
            var res = 1.0
            for (i in 2..n) res *= i
            return res
        }
        if (state.operation == null) {
            val number = evaluateSafe(state.number1)
            val result = if (number >= 0 && number == number.toInt().toDouble()) factorial(number.toInt()) else Double.NaN
            currentInput = trimResult(result)
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = evaluateSafe(state.number2)
            val result = if (number >= 0 && number == number.toInt().toDouble()) factorial(number.toInt()) else Double.NaN
            currentInput = state.number1 + (state.operation?.symbol ?: "") + trimResult(result)
            state = state.copy(number2 = trimResult(result))
        }
    }

    private fun applyPower() {
        if (state.operation == null) {
            val number = state.number1.ifEmpty { "0" }
            currentInput = "$number^"
            // hanya mengubah teks, biar ekspresi exp4j yang hitung
            updateStateFromText(currentInput)
        } else {
            val base = evaluateSafe(state.number1)
            val exponent = evaluateSafe(state.number2)
            val result = base.pow(exponent)
            currentInput = trimResult(result)
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        }
    }

    private fun enterE() {
        val eValue = "2.718281828459045"
        if (state.operation == null) {
            currentInput = eValue
            state = state.copy(number1 = eValue, number2 = "", operation = null)
        } else {
            currentInput = state.number1 + (state.operation?.symbol ?: "") + eValue
            state = state.copy(number2 = eValue)
        }
    }

    private fun applyCube() {
        if (state.operation == null) {
            val number = evaluateSafe(state.number1)
            val result = number * number * number
            currentInput = trimResult(result)
            state = state.copy(number1 = currentInput, number2 = "", operation = null)
        } else {
            val number = evaluateSafe(state.number2)
            val result = number * number * number
            currentInput = state.number1 + (state.operation?.symbol ?: "") + trimResult(result)
            state = state.copy(number2 = trimResult(result))
        }
    }

    private fun evaluateSafe(s: String): Double =
        if (s.isBlank()) 0.0 else try { evaluateExpression(s) } catch (_: Exception) { 0.0 }

    private fun handleCloseParenthesis() {
        val lastNumber = state.number2.ifEmpty { state.number1 }
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

    private fun handleParentheses() {
        val lastChar = currentInput.lastOrNull()
        val lastNumber = state.number2.ifEmpty { state.number1 }
        val operators = setOf('+', '-', '×', 'x', '/', '÷', '^', '(')
        if (state.openParentheses == 0 || (lastChar != null && lastChar in operators)) {
            currentInput += "("
            state = state.copy(
                number1 = if (state.operation == null) state.number1 + "(" else state.number1,
                number2 = if (state.operation != null) state.number2 + "(" else state.number2,
                openParentheses = state.openParentheses + 1,
                expression = currentInput
            )
        } else if (state.openParentheses > 0 && lastNumber.isNotEmpty() && lastNumber.last().isDigit()) {
            currentInput += ")"
            state = state.copy(
                number1 = if (state.operation == null) state.number1 + ")" else state.number1,
                number2 = if (state.operation != null) state.number2 + ")" else state.number2,
                openParentheses = state.openParentheses - 1,
                expression = currentInput
            )
        }
    }

    fun updateStateFromText(text: String) {
        if (text.isEmpty()) {
            state = CalculatorState()
            return
        }
        state = state.copy(expression = text)

        var lastOpIndex = -1
        var parenLevel = 0
        for (i in text.indices) {
            when (text[i]) {
                '(' -> parenLevel++
                ')' -> parenLevel--
                '+', '-', '×', 'x', '/', '÷', '*' -> if (parenLevel == 0) lastOpIndex = i
            }
        }

        val currentOperation: Char? = if (lastOpIndex != -1) text[lastOpIndex] else null
        val currentNumber1 = if (lastOpIndex != -1) text.substring(0, lastOpIndex) else text
        val currentNumber2 = if (lastOpIndex != -1) text.substring(lastOpIndex + 1) else ""

        val operation = when (currentOperation) {
            '+' -> CalculatorOperation.Add
            '-' -> CalculatorOperation.Subtract
            '×', 'x', '*' -> CalculatorOperation.Multiply
            '/', '÷' -> CalculatorOperation.Divide
            else -> null
        }

        val parenthesesCount = text.count { it == '(' } - text.count { it == ')' }

        state = state.copy(
            number1 = currentNumber1,
            number2 = currentNumber2,
            operation = operation,
            openParentheses = maxOf(0, parenthesesCount)
        )
    }
}
