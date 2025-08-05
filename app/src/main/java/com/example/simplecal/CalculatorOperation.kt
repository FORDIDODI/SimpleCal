package com.example.simplecal

sealed class CalculatorOperation(val symbol: String) {
    object Add: CalculatorOperation("+")
    object Subtract: CalculatorOperation("-")
    object Multiply: CalculatorOperation("x")
    object Divide: CalculatorOperation("/")
    object Power: CalculatorOperation("xʸ")
    object NumberE: CalculatorOperation("e")
}