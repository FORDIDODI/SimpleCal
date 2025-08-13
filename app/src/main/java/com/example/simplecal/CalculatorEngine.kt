package com.example.simplecal

import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import com.example.simplecal.CalculatorOperation.*

object CalculatorEngine {

    fun calculateBasic(a: Double, b: Double, op: CalculatorOperation): Double {
        return when (op) {
            CalculatorOperation.Add -> a + b
            CalculatorOperation.Subtract -> a - b
            CalculatorOperation.Multiply -> a * b
            CalculatorOperation.Divide -> if (b != 0.0) a / b else Double.NaN
            else -> Double.NaN
        }
    }

    fun calculateScientific(value: Double, op: CalculatorOperation): Double {
        return when (op) {
            CalculatorOperation.Square -> value * value
            CalculatorOperation.SquareRoot -> if (value >= 0) sqrt(value) else Double.NaN
            CalculatorOperation.Sin -> sin(Math.toRadians(value))
            CalculatorOperation.Cos -> cos(Math.toRadians(value))
            CalculatorOperation.Tan -> tan(Math.toRadians(value))
            CalculatorOperation.Log -> if (value > 0) log10(value) else Double.NaN
            CalculatorOperation.Ln -> if (value > 0) ln(value) else Double.NaN
            CalculatorOperation.Factorial -> factorial(value.toInt())
            CalculatorOperation.Reciprocal -> if (value != 0.0) 1 / value else Double.NaN
            CalculatorOperation.Exp -> exp(value)
            CalculatorOperation.PI -> Math.PI
            CalculatorOperation.Percent -> value / 100
            else -> Double.NaN
        }
    }

    private fun factorial(n: Int): Double {
        return if (n >= 0) (1..n).fold(1L) { acc, i -> acc * i }.toDouble() else Double.NaN
    }
}
