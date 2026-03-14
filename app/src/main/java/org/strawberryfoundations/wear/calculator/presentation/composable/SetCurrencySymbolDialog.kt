package org.strawberryfoundations.wear.calculator.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import org.strawberryfoundations.wear.calculator.R
import org.strawberryfoundations.wear.calculator.presentation.core.CurrencyIconOption

@Composable
fun SetCurrencySymbolDialog(
    visible: Boolean,
    selectedCurrencyIcon: CurrencyIconOption,
    onCurrencyIconSelected: (CurrencyIconOption) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val dialogListState = rememberScalingLazyListState()
    val dialogFocusRequester = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current

    Dialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
    ) {
        ScreenScaffold(scrollState = dialogListState) { paddingValues ->
            ScalingLazyColumn(
                state = dialogListState,
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(dialogFocusRequester)
                    .requestFocusOnHierarchyActive()
                    .rotaryScrollable(
                        behavior = RotaryScrollableDefaults.behavior(dialogListState),
                        focusRequester = dialogFocusRequester,
                    ),
                autoCentering = null,
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.settings_currency_icon_title),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(R.string.settings_currency_icon_dialog_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                items(CurrencyIconOption.entries.size) { index ->
                    val option = CurrencyIconOption.entries[index]
                    val isSelected = option == selectedCurrencyIcon

                    Button(
                        onClick = {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                onCurrencyIconSelected(option)
                            }
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 3.dp),
                        colors = if (isSelected) {
                            ButtonDefaults.buttonColors()
                        } else {
                            ButtonDefaults.filledTonalButtonColors()
                        },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = stringResource(option.labelResId),
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = stringResource(option.labelResId),
                                    style = MaterialTheme.typography.displaySmall,
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = stringResource(R.string.selected),
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}