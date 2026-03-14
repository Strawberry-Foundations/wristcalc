package org.strawberryfoundations.wear.calculator.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import kotlin.math.pow

fun hexToColor(hex: String): Color {
    return runCatching { Color(hex.toColorInt()) }.getOrDefault(Color.LightGray)
}

fun darkenColor(color: Color, factor: Float = 0.85f): Color {
    return Color(
        red = (color.red * factor).coerceIn(0f, 1f),
        green = (color.green * factor).coerceIn(0f, 1f),
        blue = (color.blue * factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

fun brightenColor(color: Color, factor: Float = 0.85f): Color {
    return Color(
        red = (color.red + (1f - color.red) * (1f - factor)).coerceIn(0f, 1f),
        green = (color.green + (1f - color.green) * (1f - factor)).coerceIn(0f, 1f),
        blue = (color.blue + (1f - color.blue) * (1f - factor)).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

fun contrastColor(bg: Color): Color {
    return bestTextColorFor(bg)
}

private fun relativeLuminance(c: Color): Double {
    fun channel(v: Double): Double {
        return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }

    val r = channel(c.red.toDouble())
    val g = channel(c.green.toDouble())
    val b = channel(c.blue.toDouble())
    return 0.2126 * r + 0.7152 * g + 0.0722 * b
}

private fun contrastRatio(a: Color, b: Color): Double {
    val l1 = relativeLuminance(a)
    val l2 = relativeLuminance(b)
    val max = l1.coerceAtLeast(l2)
    val min = l1.coerceAtMost(l2)
    return (max + 0.05) / (min + 0.05)
}

private fun invertColor(c: Color): Color {
    return Color(
        red = (1f - c.red).coerceIn(0f, 1f),
        green = (1f - c.green).coerceIn(0f, 1f),
        blue = (1f - c.blue).coerceIn(0f, 1f),
        alpha = c.alpha
    )
}

private fun smoothBlend(base: Color, other: Color, blendToBase: Float = 0.6f): Color {
    val r = (other.red * (1f - blendToBase) + base.red * blendToBase).coerceIn(0f, 1f)
    val g = (other.green * (1f - blendToBase) + base.green * blendToBase).coerceIn(0f, 1f)
    val b = (other.blue * (1f - blendToBase) + base.blue * blendToBase).coerceIn(0f, 1f)
    return Color(r, g, b, base.alpha)
}

fun bestTextColorFor(bg: Color, minContrast: Double = 3.0): Color {
    val inverted = invertColor(bg)
    val smooth = smoothBlend(bg, inverted, 0.6f)
    val candidates = listOf(
        smooth,
        brightenColor(smooth, 0.5f),
        darkenColor(smooth, 0.5f),
        brightenColor(inverted, 0.5f),
        darkenColor(inverted, 0.5f),
        Color.White,
        Color.Black
    )

    val best = candidates.maxByOrNull { contrastRatio(it, bg) } ?: Color.White

    if (contrastRatio(best, bg) >= minContrast) return best

    return if (contrastRatio(Color.White, bg) >= contrastRatio(Color.Black, bg)) Color.White else Color.Black
}