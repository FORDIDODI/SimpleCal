package com.example.simplecal

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
    object Calculate : CalculatorAction()
    object ToggleSign : CalculatorAction()
    object Ln : CalculatorAction()
    object Log : CalculatorAction()
    object Sin : CalculatorAction()
    object Cos : CalculatorAction()
    object Tan : CalculatorAction()
    object E : CalculatorAction()
    object Pi : CalculatorAction()
    object Sqrt : CalculatorAction()
    object Square : CalculatorAction()
    object Cube : CalculatorAction()
    object Factorial : CalculatorAction()
    object ClearHistory : CalculatorAction()
    object Percent: CalculatorAction()
    data class Parenthesis(val parenSymbol: String) : CalculatorAction()
    data class LoadFromHistory(val entry: String) : CalculatorAction()

    val symbol: String
        get() = when (this) {
            is Number -> number.toString()
            is Operation -> operation.symbol
            Clear -> "AC"
            Delete -> "Del"
            Decimal -> "."
            Calculate -> "="
            ToggleSign -> "±"
            Ln -> "ln"
            Log -> "log"
            Sin -> "sin"
            Cos -> "cos"
            Tan -> "tan"
            E -> "e"
            Pi -> "π"
            Sqrt -> "√"
            Square -> "x²"
            Cube -> "x³"
            Factorial -> "x!"
            ClearHistory -> ""
            Percent -> "%"
            is Parenthesis -> parenSymbol
            is LoadFromHistory -> entry
        }
}