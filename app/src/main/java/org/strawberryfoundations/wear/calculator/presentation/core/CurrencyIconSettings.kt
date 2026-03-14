package org.strawberryfoundations.wear.calculator.presentation.core

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material.icons.rounded.CurrencyFranc
import androidx.compose.material.icons.rounded.CurrencyLira
import androidx.compose.material.icons.rounded.CurrencyPound
import androidx.compose.material.icons.rounded.CurrencyRuble
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.material.icons.rounded.CurrencyYen
import androidx.compose.material.icons.rounded.CurrencyYuan
import androidx.compose.material.icons.rounded.Euro
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.strawberryfoundations.wear.calculator.R

private val Context.settingsDataStore by preferencesDataStore(name = "settings")
private val currencyIconKey = stringPreferencesKey("currency_icon")

enum class CurrencyIconOption(
    val key: String,
    val labelResId: Int,
    val icon: ImageVector,
) {
    Payments(
        key = "payments",
        labelResId = R.string.currency_icon_payments,
        icon = Icons.Rounded.Payments,
    ),
    Dollar(
        key = "dollar",
        labelResId = R.string.currency_icon_dollar,
        icon = Icons.Rounded.AttachMoney,
    ),
    Euro(
        key = "euro",
        labelResId = R.string.currency_icon_euro,
        icon = Icons.Rounded.Euro,
    ),
    Pound(
        key = "pound",
        labelResId = R.string.currency_icon_pound,
        icon = Icons.Rounded.CurrencyPound,
    ),
    Yen(
        key = "yen",
        labelResId = R.string.currency_icon_yen,
        icon = Icons.Rounded.CurrencyYen,
    ),
    Yuan(
        key = "yuan",
        labelResId = R.string.currency_icon_yuan,
        icon = Icons.Rounded.CurrencyYuan,
    ),
    Ruble(
        key = "ruble",
        labelResId = R.string.currency_icon_ruble,
        icon = Icons.Rounded.CurrencyRuble,
    ),
    Lira(
        key = "lira",
        labelResId = R.string.currency_icon_lira,
        icon = Icons.Rounded.CurrencyLira,
    ),
    Franc(
        key = "franc",
        labelResId = R.string.currency_icon_franc,
        icon = Icons.Rounded.CurrencyFranc,
    ),
    Rupee(
        key = "rupee",
        labelResId = R.string.currency_icon_rupee,
        icon = Icons.Rounded.CurrencyRupee,
    ),
    Bitcoin(
        key = "bitcoin",
        labelResId = R.string.currency_icon_bitcoin,
        icon = Icons.Rounded.CurrencyBitcoin,
    ),
    ;

    companion object {
        val default = Payments

        fun fromKey(key: String?): CurrencyIconOption {
            return entries.firstOrNull { it.key == key } ?: default
        }
    }
}

suspend fun loadCurrencyIcon(context: Context): CurrencyIconOption {
    val storedKey = context.settingsDataStore.data.first()[currencyIconKey]
    return CurrencyIconOption.fromKey(storedKey)
}

suspend fun saveCurrencyIcon(
    context: Context,
    option: CurrencyIconOption,
) {
    context.settingsDataStore.edit { prefs ->
        prefs[currencyIconKey] = option.key
    }
}
