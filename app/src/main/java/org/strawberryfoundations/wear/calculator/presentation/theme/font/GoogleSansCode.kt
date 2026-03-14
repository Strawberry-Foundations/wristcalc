package org.strawberryfoundations.wear.calculator.presentation.theme.font

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.strawberryfoundations.wear.calculator.R


class GoogleSansCode {
    @OptIn(ExperimentalTextApi::class)
    val numeralMedium =
        FontFamily(
            Font(
                R.font.google_sans_code,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(GoogleSansFlex.DisplayLargeVFConfig.WEIGHT),
                    FontVariation.width(GoogleSansFlex.DisplayLargeVFConfig.WIDTH),
                )
            )
        )
}