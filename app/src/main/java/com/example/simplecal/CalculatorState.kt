package com.example.simplecal

data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: CalculatorOperation? = null,
    val openParentheses: Int = 0,
    val expression: String = ""
)
