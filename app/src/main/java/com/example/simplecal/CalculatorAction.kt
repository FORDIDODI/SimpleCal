package com.example.simplecal

sealed class CalculatorAction {
    data class Number(val number: Int): CalculatorAction()
    object Clear: CalculatorAction()
    object Delete: CalculatorAction()
    object Decimal: CalculatorAction()
    object Calculate: CalculatorAction()
    object Percent: CalculatorAction()
    object ToggleSign: CalculatorAction()
    data class Operation(val operation: CalculatorOperation): CalculatorAction()
    object Parentheses: CalculatorAction()
    object ClearHistory: CalculatorAction()
    data class LoadFromHistory(val entry: String) : CalculatorAction()

    // Scientific
    object Sqrt: CalculatorAction()
    object Square: CalculatorAction()
    object Cube: CalculatorAction()
    object Reciprocal: CalculatorAction()
    object NumberPi: CalculatorAction()
    object Sin: CalculatorAction()
    object Cos: CalculatorAction()
    object Tan: CalculatorAction()
    object Log: CalculatorAction()
    object Ln: CalculatorAction()
    object Exp: CalculatorAction()
    object Factorial: CalculatorAction()
    object Power: CalculatorAction()
    object NumberE: CalculatorAction()
    data class Parenthesis(val symbol: String) : CalculatorAction()
    object CloseParenthesis: CalculatorAction()
}
