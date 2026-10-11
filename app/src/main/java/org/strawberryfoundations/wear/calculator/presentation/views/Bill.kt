package org.strawberryfoundations.wear.calculator.presentation.views

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import kotlinx.coroutines.delay
import org.strawberryfoundations.wear.calculator.R
import org.strawberryfoundations.wear.calculator.presentation.composable.PillRow
import org.strawberryfoundations.wear.calculator.presentation.core.CurrencyIconOption
import org.strawberryfoundations.wear.calculator.presentation.core.formatExpression
import org.strawberryfoundations.wear.calculator.presentation.core.formatPrice
import org.strawberryfoundations.wear.calculator.presentation.core.parseBaseAmount
import kotlin.math.abs
import kotlin.math.round
import androidx.compose.ui.platform.LocalLocale
import kotlin.time.Duration.Companion.milliseconds

private enum class ActiveField {
    Tip,
    People
}

@Composable
fun BillView(
    displayText: String,
    currentExpression: String,
    isPageActive: Boolean,
    currencyIcon: CurrencyIconOption,
    tipPercentState: MutableIntState,
    peopleCountState: MutableIntState,
) {
    val locale = LocalConfiguration.current.locales.get(0) ?: LocalLocale.current.platformLocale
    val displayTextFormatted = formatExpression(displayText, locale).ifEmpty { "0" }

    val focusRequester = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current

    var activeField by remember { mutableStateOf(ActiveField.Tip) }
    var tipPercent by tipPercentState
    var peopleCount by peopleCountState
    
    // Rotary accumulation logic
    var rotaryAccumulator by remember { mutableFloatStateOf(0f) }
    val rotaryThreshold = 25f // Adjust this for sensitivity (higher = slower)

    val baseAmount = remember(displayText, currentExpression, locale) {
        parseBaseAmount(
            displayText = displayText,
            currentExpression = currentExpression,
            locale = locale
        )
    }

    val tipAmount = baseAmount?.let { round((it * tipPercent / 100.0) * 100.0) / 100.0 }
    val totalAmount = baseAmount?.let { it + (tipAmount ?: 0.0) }
    val perPerson = totalAmount?.let { round((it / peopleCount) * 100.0) / 100.0 }

    fun applyRotaryStep(step: Int): Boolean {
        if (step == 0) return false

        when (activeField) {
            ActiveField.Tip -> {
                val oldValue = tipPercent
                tipPercent = (tipPercent + step).coerceIn(0, 100)
                if (oldValue != tipPercent) {
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                }
            }
            ActiveField.People -> {
                val oldValue = peopleCount
                peopleCount = (peopleCount + step).coerceIn(1, 50)
                if (oldValue != peopleCount) {
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                }
            }
        }
        return true
    }

    LaunchedEffect(isPageActive) {
        if (isPageActive) {
            delay(100.milliseconds)
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {
                // Ignore if not yet attachable
            }
            view.requestFocus()
        }
    }

    ScreenScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .onRotaryScrollEvent { event ->
                    rotaryAccumulator += event.verticalScrollPixels

                    if (abs(rotaryAccumulator) >= rotaryThreshold) {
                        val steps = (rotaryAccumulator / rotaryThreshold).toInt()
                        applyRotaryStep(steps)
                        rotaryAccumulator %= rotaryThreshold
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 36.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayTextFormatted,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = currencyIcon.icon,
                    contentDescription = stringResource(R.string.tip),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            PillRow(
                label = "${tipPercent}% ${stringResource(R.string.tip)}",
                value = tipAmount?.let { formatPrice(it, locale) } ?: "-",
                icon = Icons.Rounded.VolunteerActivism,
                isActive = activeField == ActiveField.Tip,
                onClick = {
                    activeField = ActiveField.Tip
                    focusRequester.requestFocus()
                }
            )

            Spacer(modifier = Modifier.size(4.dp))

            PillRow(
                label = stringResource(R.string.people),
                value = peopleCount.toString(),
                icon = Icons.Rounded.Groups,
                isActive = activeField == ActiveField.People,
                onClick = {
                    activeField = ActiveField.People
                    focusRequester.requestFocus()
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 36.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = totalAmount?.let { formatPrice(it, locale) } ?: "-",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp),
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = currencyIcon.icon,
                    contentDescription = stringResource(R.string.total),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (peopleCount > 1) {
                        stringResource(
                            R.string.each,
                            perPerson?.let { formatPrice(it, locale) } ?: "-"
                        )
                    } else stringResource(R.string.total),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


