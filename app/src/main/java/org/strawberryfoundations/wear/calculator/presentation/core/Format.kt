package org.strawberryfoundations.wear.calculator.presentation.core

import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

fun formatExpression(expression: String, locale: Locale): String {
    if (expression.isEmpty()) return ""

    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator

    val formatted = StringBuilder()
    val currentNumber = StringBuilder()

    for (char in expression) {
        if (char.isDigit() || char == decimalSeparator) {
            currentNumber.append(char)
        } else {
            if (currentNumber.isNotEmpty()) {
                formatted.append(formatNumber(currentNumber.toString(), locale))
                currentNumber.clear()
            }
            formatted.append(char)
        }
    }
    // Append remaining number
    if (currentNumber.isNotEmpty()) {
        formatted.append(formatNumber(currentNumber.toString(), locale))
    }

    return formatted.toString()
}

fun formatNumber(numberStr: String, locale: Locale): String {
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator

    val index = numberStr.indexOf(decimalSeparator)
    if (index != -1) {
        val intPart = numberStr.substring(0, index)
        val fracPart = numberStr.substring(index + 1)
        return formatRawInteger(intPart, locale) + decimalSeparator + fracPart
    }

    return formatRawInteger(numberStr, locale)
}

fun formatRawInteger(intStr: String, locale: Locale): String {
    if (intStr.isEmpty()) return ""
    return try {
        val groupingSeparator = DecimalFormatSymbols.getInstance(locale).groupingSeparator
        if (intStr.length > 18) {
            val reversed = intStr.reversed()
            val chunked = reversed.chunked(3).joinToString(groupingSeparator.toString())
            chunked.reversed()
        } else {
            val number = intStr.toLong()
            NumberFormat.getIntegerInstance(locale).format(number)
        }
    } catch (_: NumberFormatException) {
        intStr
    }
}



fun formatResult(result: Double, locale: Locale): String {
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator

    return if (result % 1.0 == 0.0 && result.toString().length <= 15) {
        result.toLong().toString()
    } else {
        val formatted = String.format(locale, "%.10f", result)
            .trimEnd('0')
            .trimEnd(decimalSeparator)
        formatted
    }
}

fun formatPrice(amount: Double, locale: Locale): String {
    return String.format(locale, "%.2f", amount)
}

fun parseBaseAmount(
    displayText: String,
    currentExpression: String,
    locale: Locale
): Double? {
    val symbols = DecimalFormatSymbols.getInstance(locale)

    val fromDisplay = displayText
        .replace(symbols.groupingSeparator.toString(), "")
        .replace(symbols.decimalSeparator, '.')
        .toDoubleOrNull()

    if (fromDisplay != null && fromDisplay > 0) return fromDisplay

    return runCatching {
        if (currentExpression.isBlank()) null else evaluateExpression(currentExpression)
    }.getOrNull()?.takeIf { it > 0 }
}


fun normalizeResultForExpression(result: String, locale: Locale): String {
    val symbols = DecimalFormatSymbols.getInstance(locale)

    return result
        .replace(symbols.groupingSeparator.toString(), "")
        .replace(symbols.decimalSeparator, '.')
}
