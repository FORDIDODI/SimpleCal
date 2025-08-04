package com.example.simplecal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel: ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    val history = mutableStateOf<List<String>>(emptyList())

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

    private fun performCalculation() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()
        if (number1 != null && number2 != null) {
        val result = when(state.operation) {
            is CalculatorOperation.Add -> number1 + number2
            is CalculatorOperation.Subtract -> number1 - number2
            is CalculatorOperation.Multiply -> number1 * number2
            is CalculatorOperation.Divide -> number1 / number2
            is CalculatorAction.Sqrt -> applySqrt()
            is CalculatorAction.Square -> applySquare()
            is CalculatorAction.Reciprocal -> applyReciprocal()
            is CalculatorAction.NumberPi -> enterNumberPi()

            null -> return
        }
            val expression = "${state.number1} ${state.operation?.symbol} ${state.number2} = ${result.toString().take(15)}"
            history.value = listOf(expression) + history.value.take(9)
            state = state.copy(
                number1 = result.toString().take(15),
                number2 = "",
                operation = null
            )
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if(state.number1.isNotBlank()) {
            state = state.copy(operation = operation)
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
                state = state.copy(number1 = (num1 / 100).toString())
            }
        } else {
            val num2 = state.number2.toDoubleOrNull()
            if (num2 != null) {
                state =state.copy(number2 = (num2 / 100).toString())
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

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }

}