package org.strawberryfoundations.wear.calculator.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.RevealValue
import androidx.wear.compose.material.SwipeToRevealChip
import androidx.wear.compose.material.SwipeToRevealPrimaryAction
import androidx.wear.compose.material.SwipeToRevealUndoAction
import androidx.wear.compose.material.rememberRevealState
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.VerticalPageIndicator
import androidx.wear.compose.material.Icon
import org.strawberryfoundations.wear.calculator.presentation.composable.CalculatorDisplay
import org.strawberryfoundations.wear.calculator.presentation.composable.KeypadGrid
import org.strawberryfoundations.wear.calculator.presentation.core.HistoryEntry
import org.strawberryfoundations.wear.calculator.presentation.core.Keypad
import org.strawberryfoundations.wear.calculator.presentation.core.evaluateExpression
import org.strawberryfoundations.wear.calculator.presentation.core.formatExpression
import org.strawberryfoundations.wear.calculator.presentation.core.formatResult
import org.strawberryfoundations.wear.calculator.presentation.core.loadHistory
import org.strawberryfoundations.wear.calculator.presentation.core.saveHistory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import org.strawberryfoundations.wear.calculator.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import java.text.DecimalFormatSymbols
import java.util.Locale


@Composable
fun CalculatorMainView() {
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
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
                0 -> CalculatorView(history = history)
                1 -> HistoryView(history = history)
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
fun CalculatorView(history: SnapshotStateList<HistoryEntry>) {
    var displayText by remember { mutableStateOf("") }
    var currentExpression by remember { mutableStateOf("") }

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



@Composable
fun HistoryView(history: SnapshotStateList<HistoryEntry>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (history.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.no_history_yet)
            )
            return
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(history, key = { index, _ -> index }) { index, entry ->
                HistoryChip(
                    entry = entry,
                    onDelete = {
                        if (index in history.indices) {
                            history.removeAt(index)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun HistoryChip(
    entry: HistoryEntry,
    onDelete: () -> Unit
) {
    val revealState = rememberRevealState()
    val coroutineScope = rememberCoroutineScope()
    var pendingDeletion by remember { mutableStateOf(false) }

    fun scheduleDelete() {
        if (pendingDeletion) return
        pendingDeletion = true
        coroutineScope.launch {
            revealState.animateTo(RevealValue.RightRevealed)
            delay(3000L)
            if (pendingDeletion) {
                onDelete()
                pendingDeletion = false
            }
        }
    }

    SwipeToRevealChip(
        revealState = revealState,
        modifier = Modifier.fillMaxWidth(),
        primaryAction = {
            SwipeToRevealPrimaryAction(
                revealState = revealState,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                },
                label = { Text(stringResource(R.string.delete)) },
                onClick = { scheduleDelete() }
            )
        },
        undoPrimaryAction = {
            SwipeToRevealUndoAction(
                revealState = revealState,
                label = { Text(stringResource(R.string.undo)) },
                onClick = {
                    pendingDeletion = false
                    coroutineScope.launch {
                        revealState.animateTo(RevealValue.Covered)
                    }
                }
            )
        },
        onFullSwipe = { scheduleDelete() },
        content = {
            Chip(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.chipColors(),
                label = {
                    Text(
                        text = entry.result,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                secondaryLabel = {
                    Text(
                        text = entry.expression,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )
        }
    )
}