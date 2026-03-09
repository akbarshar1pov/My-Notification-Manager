# Repository Guidelines

## Project Structure & Module Organization
This repository is a single-module Android app. Core code lives in `app/src/main/java/com/sharipov/mynotificationmanager`, split by responsibility: `ui/` for Compose screens and components, `viewmodel/` for state holders, `data/` and `data/repository/` for Room and preferences, `services/` for notification listeners, `di/` for Hilt wiring, and `utils/` for shared helpers. Resources are under `app/src/main/res`; localized strings already exist in `values-ru`, `values-tg`, and `values-uk`. Room schema snapshots are checked in under `app/schemas/` and should be updated with any database change.

## Build, Test, and Development Commands
Use the Gradle wrapper through `bash` in this checkout:

- `bash ./gradlew assembleDebug` builds a debug APK.
- `bash ./gradlew installDebug` installs the debug build on a connected device or emulator.
- `bash ./gradlew testDebugUnitTest` runs JVM unit tests from `app/src/test`.
- `bash ./gradlew connectedDebugAndroidTest` runs instrumentation and Compose UI tests from `app/src/androidTest`.
- `bash ./gradlew lintDebug` runs Android lint for the debug variant.

## Coding Style & Naming Conventions
Write Kotlin with 4-space indentation and keep imports and nullability explicit. Follow the existing package layout and Android naming already used in the app: `MainActivity`, `HomeViewModel`, `NotificationRepository`, `NotificationDao`, `NotificationEntity`, `SettingsScreen`. Compose screens and reusable UI pieces belong in `ui/`; keep composable filenames aligned with the main type they expose. Resource names should stay lowercase with underscores, for example `ic_notifications_off.xml`.

## Testing Guidelines
JUnit 4 is configured for local unit tests, and AndroidX test plus Compose test libraries are configured for device tests. Name new tests `*Test.kt` and place them in the matching source set. Prefer unit tests for repositories, converters, and utility logic; use `androidTest` for Room integration, notification listener behavior that needs Android framework support, and Compose UI flows. If a Room schema changes, verify the exported JSON in `app/schemas/` is updated in the same commit.

## Commit & Pull Request Guidelines
Recent history favors short, imperative commit subjects such as `Update app icon` or `Update libs`. Keep the first line brief and descriptive; add details in the body when a change spans UI, data, and dependency updates. Pull requests should include a summary, test steps or commands run, linked issues when applicable, and screenshots or recordings for visible UI changes.
