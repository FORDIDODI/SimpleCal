package com.example.simplecal

sealed class CalculatorAction {
    data class Number(val number: Int): CalculatorAction()
    object Clear: CalculatorAction()
    object Delete: CalculatorAction()
    object  Decimal: CalculatorAction()
    object Calculate: CalculatorAction()
    object Percent: CalculatorAction()
    object ToggleSign: CalculatorAction()
    object Sqrt: CalculatorAction()
    object Square: CalculatorAction()
    object Reciprocal: CalculatorAction()
    object NumberPi: CalculatorAction()
    data class Operation(val operation: CalculatorOperation): CalculatorAction()
}