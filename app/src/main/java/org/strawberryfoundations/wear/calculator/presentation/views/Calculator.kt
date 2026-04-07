package org.strawberryfoundations.wear.calculator.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.VerticalPageIndicator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.strawberryfoundations.wear.calculator.presentation.composable.CalculatorDisplay
import org.strawberryfoundations.wear.calculator.presentation.composable.KeypadGrid
import org.strawberryfoundations.wear.calculator.presentation.core.HistoryEntry
import org.strawberryfoundations.wear.calculator.presentation.core.Keypad
import org.strawberryfoundations.wear.calculator.presentation.core.evaluateExpression
import org.strawberryfoundations.wear.calculator.presentation.core.formatExpression
import org.strawberryfoundations.wear.calculator.presentation.core.formatResult
import org.strawberryfoundations.wear.calculator.presentation.core.loadHistory
import org.strawberryfoundations.wear.calculator.presentation.core.normalizeResultForExpression
import org.strawberryfoundations.wear.calculator.presentation.core.saveHistory
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun CalculatorMainView(
    displayTextState: MutableState<String>,
    currentExpressionState: MutableState<String>
) {
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val coroutineScope = rememberCoroutineScope()
    val history = remember { mutableStateListOf<HistoryEntry>() }
    val context = LocalContext.current
    val edgePadding = 12.dp

    LaunchedEffect(Unit) {
        history.clear()
        history.addAll(loadHistory(context))
    }

    LaunchedEffect(history) {
        snapshotFlow { history.toList() }
            .distinctUntilChanged()
            .collectLatest { entries ->
                saveHistory(context, entries)
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        VerticalPager(
            state = pagerState,
            key = { it },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = edgePadding, vertical = 8.dp)
        ) { page ->
            when (page) {
                0 -> CalculatorView(
                    history = history,
                    displayTextState = displayTextState,
                    currentExpressionState = currentExpressionState
                )
                1 -> HistoryView(
                    history = history,
                    onHistorySelected = { entry ->
                        displayTextState.value = entry.result
                        currentExpressionState.value = normalizeResultForExpression(
                            entry.result,
                            Locale.getDefault(),
                        )
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                )
            }
        }

        VerticalPageIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp),
            selectedColor = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun CalculatorView(
    history: SnapshotStateList<HistoryEntry>,
    displayTextState: MutableState<String>,
    currentExpressionState: MutableState<String>
) {
    var displayText by displayTextState
    var currentExpression by currentExpressionState
    val locale = LocalConfiguration.current.locales.get(0) ?: Locale.getDefault()
    val decimalSeparator: Char = DecimalFormatSymbols.getInstance(locale).decimalSeparator

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CalculatorDisplay(
            text = formatExpression(displayText, locale),
            onClearClick = {
                if (displayText.isNotEmpty()) {
                    displayText = displayText.dropLast(1)
                    currentExpression = currentExpression.dropLast(1)
                }
            }, onClearLongPress = {
                displayText = ""
                currentExpression = ""
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        KeypadGrid(
            decimalSeparator = decimalSeparator,
            onKeyPress = { key ->
                when (key) {
                    Keypad.KEY_EQUALS -> {
                        if (currentExpression.isNotEmpty()) {
                            try {
                                val expressionDisplay = formatExpression(displayText, locale)
                                val result = evaluateExpression(currentExpression)
                                val resultText = formatResult(result, locale)
                                history.add(0, HistoryEntry(expressionDisplay, resultText))
                                displayText = resultText
                                currentExpression = displayText.replace(
                                    DecimalFormatSymbols.getInstance(locale).groupingSeparator.toString(),
                                    ""
                                )
                            } catch (_: Exception) {
                                displayText = "Error"
                                currentExpression = ""
                            }
                        }
                    }
                    else -> {
                        val char = if (key == Keypad.KEY_DECIMAL) "." else when(key.label) {
                            "×" -> "*"
                            "÷" -> "/"
                            "−" -> "-"
                            else -> key.label
                        }
                        currentExpression += char
                        displayText = if (key == Keypad.KEY_DECIMAL) {
                            displayText + decimalSeparator.toString()
                        } else {
                            displayText + key.label
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}