// CalculatorOperation.kt
package com.example.simplecal

sealed class CalculatorOperation(val symbol: String) {
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("-")
    object Multiply : CalculatorOperation("×")
    object Divide : CalculatorOperation("÷")
    object Square : CalculatorOperation("x²")
    object SquareRoot : CalculatorOperation("√")
    object Sin : CalculatorOperation("sin")
    object Cos : CalculatorOperation("cos")
    object Tan : CalculatorOperation("tan")
    object Log : CalculatorOperation("log")
    object Ln : CalculatorOperation("ln")
    object Factorial : CalculatorOperation("n!")
    object Reciprocal : CalculatorOperation("1/x")
    object Exp : CalculatorOperation("exp")
    object PI : CalculatorOperation("π")
    object Percent : CalculatorOperation("%")
}
