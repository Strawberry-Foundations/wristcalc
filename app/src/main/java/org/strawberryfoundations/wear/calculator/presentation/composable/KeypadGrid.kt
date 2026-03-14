package org.strawberryfoundations.wear.calculator.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.strawberryfoundations.wear.calculator.presentation.core.Keypad

@Composable
fun KeypadGrid(
    decimalSeparator: Char,
    onKeyPress: (Keypad) -> Unit
) {
    val rows = listOf(
        listOf(Keypad.KEY_7, Keypad.KEY_8, Keypad.KEY_9, Keypad.KEY_DIVIDE),
        listOf(Keypad.KEY_4, Keypad.KEY_5, Keypad.KEY_6, Keypad.KEY_MULTIPLY),
        listOf(Keypad.KEY_1, Keypad.KEY_2, Keypad.KEY_3, Keypad.KEY_MINUS),
        listOf(Keypad.KEY_DECIMAL, Keypad.KEY_0, Keypad.KEY_EQUALS, Keypad.KEY_PLUS)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                rowKeys.forEach { key ->
                    val label = if (key == Keypad.KEY_DECIMAL) decimalSeparator.toString() else key.label
                    CalculatorButton(
                        key = key,
                        displayLabel = label,
                        onClick = { onKeyPress(key) }
                    )
                }
            }
        }
    }
}