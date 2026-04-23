package org.strawberryfoundations.wear.calculator.presentation.core

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

object Changelog {
    val entries = listOf(
        ChangelogEntry(
            version = "1.0.4",
            date = "Apr 23, 2026",
            changes = listOf(
                "Updated AGP to v9.2.0",
                "Dependency updates",
                "Improve visibility handling in PagerChangePopup",
            )
        ),
        ChangelogEntry(
            version = "1.0.3",
            date = "Apr 7, 2026",
            changes = listOf(
                "New app name",
                "Added small pop-up when changing the view",
                "Previous history entries are now reusable",
                "Gradle build config update to match Google's AGP 9.x version",
            )
        ),
        ChangelogEntry(
            version = "1.0.2",
            date = "Mar 29, 2026",
            changes = listOf(
                "Update libraries",
                "Internal code optimizations & fixes",
            )
        ),
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
}