---
objective: "homeout's Home screen (design variant A) runs on Android and Web from one Kotlin Multiplatform codebase that also targets iOS (built and tested later on macOS, per the spec), with every spec behavior covered by passing tests."
status: implemented
---

# Plan: homeout Home screen

Source: `aidd_docs/tasks/2026_11/2026_11_06_homeout-event-app/spec.md` and the design https://claude.ai/artifact/1krt444a78B46kwb7jYipL (pages homeout UI Library, Button Set, Home Screen; version 1793993015-73d9).

Project root: `C:\Users\joao_\Documents\homeout` (new).

## Phases

| #   | Phase        | File                         | Depends on |
| --- | ------------ | ---------------------------- | ---------- |
| 1   | Project scaffold | [`phase-1.md`](./phase-1.md) | none |
| 2   | Domain | [`phase-2.md`](./phase-2.md) | 1 |
| 3   | Sample data | [`phase-3.md`](./phase-3.md) | 2 |
| 4   | Design system | [`phase-4.md`](./phase-4.md) | 1 |
| 5   | Home screen | [`phase-5.md`](./phase-5.md) | 2, 3, 4 |

## External sources

| URL   | What it settled   |
| ----- | ----------------- |
| https://kotlinlang.org/docs/multiplatform/compose-compatibility-and-versioning.html | Compose Multiplatform 1.12.2 is the latest release (top of the table, Jetpack Compose 1.12.1). The page says "the latest Compose Multiplatform is always compatible with the latest version of Kotlin" and the Compose Compiler plugin must match the Kotlin version. Its web target is Kotlin/Wasm. |
| https://kotlinlang.org/docs/gradle-configure-project.html | The compatibility table for Kotlin Gradle plugin 2.4.20–2.4.21 lists Gradle 7.6.3–9.7.0 and AGP 8.5.2–9.3.1 as fully supported. |
| https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html | With AGP 9, the Kotlin Multiplatform plugin no longer works with `com.android.application` or `com.android.library`. Shared modules use `com.android.kotlin.multiplatform.library`, and the Android app goes in its own module. |
| https://developer.android.com/kotlin/multiplatform/plugin | With the KMP library plugin, device tests are off by default. They are enabled with `withDeviceTest { }` in `kotlin { android { } }` and live in `src/androidDeviceTest`. |
| https://github.com/Kotlin/kotlinx.coroutines/releases | kotlinx-coroutines 1.11.0 is the latest stable release, built with Kotlin 2.2.20. |
| https://github.com/Kotlin/kotlinx-datetime/releases | kotlinx-datetime 0.8.0 is the latest stable release. The page doesn't state its Kotlin version. |
| https://kotlinlang.org/docs/multiplatform/supported-platforms.html | Compose on Android and iOS is Stable; Compose on Web (Wasm) is Beta. |
| https://kotlinlang.org/docs/releases.html | Kotlin 2.4.21 (October 8, 2026) is the latest stable release. |
| https://kotlinlang.org/docs/multiplatform/compose-test.html | Common UI tests use `runComposeUiTest` from `org.jetbrains.compose.ui:ui-test:1.12.2`. They run with `wasmJsTest`, `connectedAndroidTest` (instrumented only, not local) and `iosSimulatorArm64Test`. |
| https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources-usage.html | Fonts go in `composeResources/font` (TTF) and load with `Font(Res.font.x, weight)` inside composable typography. |
| https://developer.android.com/build/releases/gradle-plugin | AGP needs JDK 17. AGP 9.5.0 needs Gradle 9.6.0, which is above Kotlin's fully supported AGP range, so the plan uses 9.3.1. |

## Impact

| Category                                           | Verdict and evidence | Handled in         |
| -------------------------------------------------- | -------------------- | ------------------ |
| runtime | JDK: observed. No system JDK; Android Studio's bundled JBR is OpenJDK 21.0.9 at `C:\Program Files\Android\Android Studio\jbr`, which meets AGP's JDK 17 minimum. | 1 |
| runtime | Android SDK: observed at `%LOCALAPPDATA%\Android\Sdk`, with platforms 34–36.1, build-tools 36.1.0, emulator and the AVD `Pixel_8_Pro` (android-34 Play image). `ANDROID_HOME` is not set, so `local.properties` must point to the SDK. | 1 |
| runtime | Web tests: Chrome observed at `C:\Program Files\Google\Chrome\Application\chrome.exe`, for `wasmJsBrowserTest`. | 1 |
| runtime | iOS: cannot be built or tested on this Windows machine (Kotlin/Native iOS and Xcode need macOS). Per the user's decision, the iOS target and `iosApp` entry are set up but stay unbuilt and untested until a Mac or macOS CI is available. Verdict: unverified. | 1, 5 |
| runtime | Versions are all inside the documented compatibility ranges: Kotlin 2.4.21, Compose Multiplatform 1.12.2, AGP 9.3.1, Gradle 9.7.0, kotlinx-coroutines 1.11.0, kotlinx-datetime 0.8.0. Two points are inferred, not stated: AGP 9.3.1's minimum Gradle is at or below 9.7.0 (AGP 9.5 needs 9.6.0), and kotlinx-datetime 0.8.0 works with Kotlin 2.4.21 (it's built on an older Kotlin). The first sync in phase 1 confirms both. | 1 |
| architecture | Greenfield. Clean architecture is enforced by Gradle modules: `domain` (pure Kotlin, no UI) ← `data` ← `composeApp` (presentation, iOS and Web entries). `designsystem` holds UI only and depends on no app module. `androidApp` holds only the Android entry point, as AGP 9 requires. | 1–5 |
| documentation | The spec and plan move into the project's `aidd_docs`. A short `README.md` gives the build, run and test commands. | 1 |

## Decisions

| Decision   | Why   |
| ---------- | ----- |
| One Gradle module per layer: `domain`, `data`, `designsystem`, `composeApp`, plus `androidApp` | The compiler blocks a wrong-way dependency, such as domain importing UI. A package-only split would rely on discipline alone. AGP 9 requires the Android app to be a separate module. |
| Pin AGP 9.3.1 and Gradle 9.7.0, not the newest 9.5.0 and 9.9.0 | They are the highest versions Kotlin 2.4.21 fully supports. Anything newer is only "may work, with warnings". |
| Web uses the Kotlin/Wasm target only | It is the only web target Compose Multiplatform supports. |
| No dependency-injection or navigation library; wiring is manual in `composeApp` | There is one screen and one repository, so a library would add code the spec doesn't call for. |
| Domain logic takes an injected `Clock` and `TimeZone` | "Upcoming", "Tonight" and day grouping depend on the current time, and tests need to fix it. |
| Sample events are dated relative to today | Fixed dates would stop being upcoming after they pass. |
