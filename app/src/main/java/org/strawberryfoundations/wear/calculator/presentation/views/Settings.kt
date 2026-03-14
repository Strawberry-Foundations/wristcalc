package org.strawberryfoundations.wear.calculator.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import org.strawberryfoundations.wear.calculator.R
import org.strawberryfoundations.wear.calculator.presentation.composable.SetCurrencySymbolDialog
import org.strawberryfoundations.wear.calculator.presentation.core.CurrencyIconOption

@Composable
fun SettingsView(
    selectedCurrencyIcon: CurrencyIconOption,
    onCurrencyIconSelected: (CurrencyIconOption) -> Unit,
) {
    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }
    val showCurrencyDialog = remember { mutableStateOf(false) }

    ScreenScaffold(scrollState = listState) { paddingValues ->
        ScalingLazyColumn(
            state = listState,
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(rotaryFocusRequester)
                .requestFocusOnHierarchyActive()
                .rotaryScrollable(
                    behavior = RotaryScrollableDefaults.behavior(listState),
                    focusRequester = rotaryFocusRequester,
                ),
            autoCentering = null,
        ) {
            item {
                ListHeader {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(R.string.settings),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(R.string.settings),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Payments,
                            contentDescription = stringResource(R.string.settings_currency_icon_title),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(R.string.settings_currency_icon_title),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Text(
                        text = stringResource(R.string.settings_currency_icon_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Button(
                    onClick = { showCurrencyDialog.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = selectedCurrencyIcon.icon,
                                contentDescription = stringResource(selectedCurrencyIcon.labelResId),
                                modifier = Modifier.size(20.dp),
                            )
                            Column {
                                Text(
                                    text = stringResource(R.string.settings_currency_icon_pick_button),
                                    style = MaterialTheme.typography.displaySmall,
                                )
                                Text(
                                    text = stringResource(selectedCurrencyIcon.labelResId),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
    SetCurrencySymbolDialog(
        visible = showCurrencyDialog.value,
        selectedCurrencyIcon = selectedCurrencyIcon,
        onCurrencyIconSelected = onCurrencyIconSelected,
        onDismissRequest = { showCurrencyDialog.value = false },
    )

}
