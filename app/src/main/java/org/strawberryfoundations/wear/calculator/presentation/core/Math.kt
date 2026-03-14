package org.strawberryfoundations.wear.calculator.presentation.core

import java.text.DecimalFormatSymbols
import java.util.Locale


fun evaluateExpression(expression: String): Double {
    if (expression.isEmpty()) throw IllegalArgumentException("Empty expression")

    val tokens = tokenize(expression)
    if (tokens.isEmpty()) throw IllegalArgumentException("Invalid expression")

    return evaluateTokens(tokens)
}

private fun tokenize(expression: String): List<String> {
    val tokens = mutableListOf<String>()
    val currentNumber = StringBuilder()

    for (i in expression.indices) {
        val char = expression[i]
        when {
            char.isDigit() || char == '.' -> {
                currentNumber.append(char)
            }
            char in listOf('+', '-', '*', '/') -> {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber.toString())
                    currentNumber.clear()
                }

                if (char == '-' && (i == 0 || expression[i - 1] in listOf('+', '-', '*', '/'))) {
                    currentNumber.append(char)
                } else {
                    tokens.add(char.toString())
                }
            }
        }
    }

    if (currentNumber.isNotEmpty()) {
        tokens.add(currentNumber.toString())
    }

    return tokens
}

private fun evaluateTokens(tokens: List<String>): Double {
    val values = mutableListOf<Double>()
    val operators = mutableListOf<String>()

    var i = 0
    while (i < tokens.size) {
        val token = tokens[i]

        when {
            token.toDoubleOrNull() != null -> {
                values.add(token.toDouble())
            }
            token in listOf("+", "-", "*", "/") -> {
                while (operators.isNotEmpty() && shouldEvaluate(operators.last(), token)) {
                    val op = operators.removeAt(operators.lastIndex)
                    val b = values.removeAt(values.lastIndex)
                    val a = values.removeAt(values.lastIndex)
                    values.add(applyOperator(a, b, op))
                }
                operators.add(token)
            }
        }
        i++
    }

    while (operators.isNotEmpty()) {
        val op = operators.removeAt(operators.lastIndex)
        val b = values.removeAt(values.lastIndex)
        val a = values.removeAt(values.lastIndex)
        values.add(applyOperator(a, b, op))
    }

    return values.firstOrNull() ?: throw IllegalArgumentException("Invalid expression")
}

private fun shouldEvaluate(stackOp: String, currentOp: String): Boolean {
    val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)
    return (precedence[stackOp] ?: 0) >= (precedence[currentOp] ?: 0)
}

private fun applyOperator(a: Double, b: Double, operator: String): Double {
    return when (operator) {
        "+" -> a + b
        "-" -> a - b
        "*" -> a * b
        "/" -> {
            if (b == 0.0) throw ArithmeticException("Division by zero")
            a / b
        }
        else -> throw IllegalArgumentException("Unknown operator: $operator")
    }
}