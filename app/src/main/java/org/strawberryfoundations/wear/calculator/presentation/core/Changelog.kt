package org.strawberryfoundations.wear.calculator.presentation.core

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

object Changelog {
    val entries = listOf(
        ChangelogEntry(
            version = "1.0.1",
            date = "Mar 14, 2026",
            changes = listOf(
                "Fixed scroll logic (rotaryStep) in bill split view",
                "Fixed resetting bill split values when navigating away from the bill split view",
            )
        ),
        ChangelogEntry(
            version = "1.0.0",
            date = "Mar 14, 2026",
            changes = listOf(
                "Initial full release",
                "Basic calculator functionality",
                "Bill splitting feature",
                "Customizable currency icons",
            )
        ),
    )

    val latestVersion: String
        get() = entries.firstOrNull()?.version ?: "Unknown"

    val latestChanges: List<String>
        get() = entries.firstOrNull()?.changes ?: emptyList()
}