package org.strawberryfoundations.wear.calculator.presentation.core

import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntry(
    val expression: String,
    val result: String
)
