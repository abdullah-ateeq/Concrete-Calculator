# Concrete Calculator

Concrete Calculator is a modern Android application built entirely with **Jetpack Compose**. It allows users to quickly calculate concrete volume and other related metrics required for construction and DIY projects.

## Features

- **Concrete Calculations**: Calculate the exact amount of concrete needed based on dimensions.
- **History Tracking**: Keep track of your past calculations.
- **Material Design 3 (M3)**: Beautiful, responsive UI following the latest Android design guidelines.
- **Dynamic Theming**: Support for light and dark modes, and dynamic color (on supported devices).
- **Settings & Preferences**: Customize your experience; app preferences are persisted using Jetpack DataStore.

## Tech Stack & Architecture

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Jetpack Compose Navigation
- **Local Storage**: Jetpack DataStore (Preferences)
- **Language**: Kotlin

## Project Structure

The application's navigation is structured as follows:
- **Splash Screen**
- **Calculator Screen**: The core utility to perform calculations.
- **Result Screen**: Detailed view of the calculated concrete requirement.
- **History Screen**: A log of previous calculations.
- **Settings Screen**: Theme adjustments and configurations.
- **About Screen**: Information regarding the application.

## Prerequisites

- Android Studio Koala (or newer)
- Kotlin 1.9.0+
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 37

## Building and Running

1. Clone this repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/Concrete-Calculator.git
   ```
2. Open the project in **Android Studio**.
3. Sync the Gradle files.
4. Build and run the app on an emulator or physical Android device.

## License

This project is open-source and available to use.
