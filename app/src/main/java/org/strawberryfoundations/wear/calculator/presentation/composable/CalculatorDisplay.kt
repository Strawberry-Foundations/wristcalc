package org.strawberryfoundations.wear.calculator.presentation.composable

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text

@SuppressLint("FrequentlyChangingValue")
@Composable
fun CalculatorDisplay(
    text: String,
    onClearClick: () -> Unit,
    onClearLongPress: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            val scrollState = rememberScrollState()
            var previousTextLength by remember { androidx.compose.runtime.mutableIntStateOf(0) }
            var slideOffset by remember { mutableFloatStateOf(0f) }

            val animatedSlideOffset by animateFloatAsState(
                targetValue = slideOffset,
                animationSpec = tween(durationMillis = 150, easing = androidx.compose.animation.core.LinearOutSlowInEasing),
                label = "SlideOffset",
                finishedListener = { slideOffset = 0f }
            )

            LaunchedEffect(text) {
                val isDeleting = text.length < previousTextLength
                previousTextLength = text.length

                slideOffset = if (isDeleting && scrollState.value > 0) {
                    15f
                } else {
                    0f
                }

                scrollState.animateScrollTo(
                    scrollState.maxValue,
                    animationSpec = tween(durationMillis = 150, easing = androidx.compose.animation.core.LinearOutSlowInEasing)
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .graphicsLayer { translationX = animatedSlideOffset },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp),
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onBackground
                )

                val shadowAlpha by animateFloatAsState(
                    targetValue = if (scrollState.value > 0) 1f else 0f,
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = androidx.compose.animation.core.LinearOutSlowInEasing
                    ),
                    label = "ShadowAlpha"
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(16.dp)
                        .height(24.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = shadowAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    onClearClick()
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClearLongPress()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Backspace,
                    contentDescription = "Clear",
                )
            }
        }
    }
}