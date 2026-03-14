package org.strawberryfoundations.wear.calculator.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.dynamicColorScheme

private val colorPalette = ColorScheme(
    primary = Color(0xFFE57373),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFB75D5D),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFF8A65),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB26A5E),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFF06292),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFB2557A),
    onTertiaryContainer = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color.White,
    outline = Color(0xFFBCAAA4),
    outlineVariant = Color(0xFF8D6E63),
    surfaceContainerLow = Color(0xFF2D2323),
    surfaceContainer = Color(0xFF2D2323),
    surfaceContainerHigh = Color(0xFF2D2323),
)


@Composable
fun WearCalculatorTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            dynamicColorScheme(context) ?: colorPalette
        }
        else -> colorPalette
    }

    androidx.wear.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
