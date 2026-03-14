package org.strawberryfoundations.wear.calculator.presentation.views

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.focusable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.People
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
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
import org.strawberryfoundations.wear.calculator.presentation.core.evaluateExpression
import org.strawberryfoundations.wear.calculator.presentation.core.formatExpression
import org.strawberryfoundations.wear.calculator.presentation.core.formatResult
import java.util.Locale
import kotlin.math.abs

@Composable
fun BillView(
    displayText: String,
    currentExpression: String,
    isPageActive: Boolean
) {
    val locale = LocalConfiguration.current.locales.get(0) ?: Locale.getDefault()
    val displayTextFormatted = formatExpression(displayText, locale).ifEmpty { "0" }

    val focusRequester = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current

    var activeField by remember { mutableStateOf(ActiveField.Tip) }
    var tipPercent by remember { mutableIntStateOf(5) }
    var peopleCount by remember { mutableIntStateOf(1) }
    
    // Rotary accumulation logic
    var rotaryAccumulator by remember { mutableFloatStateOf(0f) }
    val rotaryThreshold = 5f // Adjust this for sensitivity (higher = slower)

    val baseAmount = remember(displayText, currentExpression, locale) {
        parseBaseAmount(
            displayText = displayText,
            currentExpression = currentExpression,
            locale = locale
        )
    }

    val totalAmount = baseAmount?.let { it + (it * tipPercent / 100.0) }
    val tipAmount = totalAmount?.let { it - baseAmount }
    val perPerson = totalAmount?.let { it / peopleCount }

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
            delay(100)
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
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
                        applyRotaryStep(-steps)
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
                    imageVector = Icons.Rounded.Payments,
                    contentDescription = stringResource(R.string.tip),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            PillRow(
                label = "${tipPercent}% ${stringResource(R.string.tip)}",
                value = tipAmount?.let { formatResult(it, locale) } ?: "-",
                icon = Icons.Rounded.Payments,
                isActive = activeField == ActiveField.Tip,
                onClick = {
                    activeField = ActiveField.Tip
                    focusRequester.requestFocus()
                }
            )

            Spacer(modifier = Modifier.size(8.dp))

            PillRow(
                label = stringResource(R.string.people),
                value = peopleCount.toString(),
                icon = Icons.Rounded.People,
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
                    .padding(start = 2.dp, end = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (peopleCount > 1) {
                         perPerson?.let { formatResult(it, locale) } ?: "-"
                    } else {
                         totalAmount?.let { formatResult(it, locale) } ?: "-"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 24.sp,
                    ),
                    textAlign = TextAlign.End
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (peopleCount > 1) "pro Person" else stringResource(R.string.total),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun PillRow(
    label: String,
    value: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isActive) MaterialTheme.colorScheme.primaryDim
        else MaterialTheme.colorScheme.onSecondary

    val textColor = if (isActive) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface

    val backgroundColor = if (isActive) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainerLow

    val borderRadius = if (isActive) 14.dp else 24.dp

    val animatedCornerRadius by animateDpAsState(
        targetValue = borderRadius,
        animationSpec = tween(durationMillis = 200),
        label = "ButtonCornerRadius"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(animatedCornerRadius)
            )
            .pointerInput(Unit) {
                detectTapGestures { onClick() }
            }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .background(borderColor, CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private enum class ActiveField {
    Tip,
    People
}

private fun parseBaseAmount(
    displayText: String,
    currentExpression: String,
    locale: Locale
): Double? {
    val symbols = java.text.DecimalFormatSymbols.getInstance(locale)

    val fromDisplay = displayText
        .replace(symbols.groupingSeparator.toString(), "")
        .replace(symbols.decimalSeparator, '.')
        .toDoubleOrNull()

    if (fromDisplay != null && fromDisplay > 0) return fromDisplay

    return runCatching {
        if (currentExpression.isBlank()) null else evaluateExpression(currentExpression)
    }.getOrNull()?.takeIf { it > 0 }
}
