package org.strawberryfoundations.wear.calculator.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.material3.Typography
import org.strawberryfoundations.wear.calculator.presentation.theme.font.CustomFont
import org.strawberryfoundations.wear.calculator.presentation.theme.font.GoogleSansCode
import org.strawberryfoundations.wear.calculator.presentation.theme.font.GoogleSansFlex

val googleSansFlex = GoogleSansFlex()
val googleSansCode = GoogleSansCode()
val customFont = CustomFont()

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = googleSansFlex.displayFontFamily,
        fontSize = 17.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
    ),

    displayMedium = TextStyle(
        fontFamily = googleSansFlex.titleMediumFontFamily,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
    ),

    displaySmall = TextStyle(
        fontFamily = googleSansFlex.titleMediumFontFamily,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp,
    ),

    titleMedium = TextStyle(
        fontFamily = googleSansFlex.titleMediumFontFamily,
        fontSize = 15.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),

    titleSmall = TextStyle(
        fontFamily = googleSansFlex.titleMediumFontFamily,
        fontSize = 13.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),

    bodyLarge = TextStyle(
        fontFamily = googleSansFlex.labelLargeFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),

    bodyMedium = TextStyle(
        fontFamily = googleSansFlex.labelLargeFontFamily,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp,
    ),

    bodySmall = TextStyle(
        fontFamily = googleSansFlex.labelLargeFontFamily,
        fontSize = 11.sp,
        lineHeight = 11.sp,
        letterSpacing = 0.sp,
    ),

    labelLarge = TextStyle(
        fontFamily = googleSansFlex.displayLargeFontFamily,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
    ),

    labelMedium = TextStyle(
        fontFamily = googleSansFlex.labelFontFamily,
        fontSize = 13.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.sp,
    ),

    labelSmall = TextStyle(
        fontFamily = googleSansFlex.labelFontFamily,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp,
    ),

    numeralMedium = TextStyle(
        fontFamily = googleSansFlex.numeralMedium,
        fontSize = 13.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.sp,
    ),

    arcMedium = CurvedTextStyle(
        fontFamily = googleSansFlex.numeralMedium,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
    )
)