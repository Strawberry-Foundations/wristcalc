<div align="center">
    <h1>⌚ Wear Calculator (Wear OS)</h1>
</div>

<p align="center">
	<img src="assets/screenshot1.png" alt="Calculator screen" width="220" style="border-radius: 100%"/>
	<img src="assets/screenshot2.png" alt="Bill split screen" width="220" style="border-radius: 100%" />
</p>
<p align="center">
	<sub>Calculator</sub>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<sub>Bill Split</sub>
</p>

A lightweight standalone calculator for Wear OS, built with Kotlin and Jetpack Compose for Wear.

The app is optimized for quick wrist interactions and includes a bill split mode, persistent history, and configurable currency icons.

## Features

- Basic calculator operations with expression evaluation.
- Calculation history with persistent local storage.
- Bill split view with tip percentage and people count.
- Rotary crown input support in bill split mode.
- Customizable currency icon for totals and tip display.
- Localized number formatting (decimal/grouping based on device locale).
- Swipe/page-based navigation tuned for wearable screens.

## Requirements

- Android Studio (latest stable recommended)
- JDK 11
- Wear OS emulator or physical Wear OS device
- Android SDK with:
	- `compileSdk = 36`
	- `minSdk = 33`
	- `targetSdk = 36`

## Getting Started

1. Clone the repository.
2. Open it in Android Studio.
3. Let Gradle sync finish.
4. Select a Wear OS emulator/device.
5. Run the `app` configuration.

## Build

```bash
./gradlew assembleDebug
```

Release build:

```bash
./gradlew assembleRelease
```

## Test

Unit tests:

```bash
./gradlew test
```

Instrumented tests (connected Wear device/emulator required):

```bash
./gradlew connectedAndroidTest
```


## License

This project is licensed under the terms of the [LICENSE](LICENSE) file in this repository.
