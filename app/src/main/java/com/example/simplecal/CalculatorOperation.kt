package com.example.simplecal

sealed class CalculatorOperation(val symbol: String) {
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("-")
    object Multiply : CalculatorOperation("×")
    object Divide : CalculatorOperation("÷")
    object Power : CalculatorOperation("^")
    object Percentage : CalculatorOperation("%")
}