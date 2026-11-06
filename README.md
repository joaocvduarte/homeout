# homeout

Find upcoming music and food events. Kotlin Multiplatform with Compose Multiplatform, for Android, iOS and Web (Wasm).

## Modules

| Module | Holds | Depends on |
| --- | --- | --- |
| `domain` | Event model and use cases. No UI. | kotlinx-datetime |
| `data` | Local sample events | `domain` |
| `designsystem` | Colors, type, theme, buttons, icons | Compose |
| `composeApp` | Home screen, iOS and Web entry points | all of the above |
| `androidApp` | Android entry point | `composeApp` |

## Requirements

- JDK 17 or newer. Android Studio's bundled JBR works: `JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"`.
- Android SDK with platform 37. Point `local.properties` at it: `sdk.dir=...`.
- Chrome, for the Web tests.
- macOS with Xcode, for iOS. The iOS target can't be built on Windows.

## Commands

| Task | Command |
| --- | --- |
| Build everything | `./gradlew build` |
| Domain and data tests (JVM, Wasm) | `./gradlew :domain:allTests :data:allTests` |
| UI tests in headless Chrome | `./gradlew :designsystem:wasmJsBrowserTest :composeApp:wasmJsBrowserTest` |
| UI tests on a running emulator or device | `./gradlew :designsystem:connectedAndroidDeviceTest :composeApp:connectedAndroidDeviceTest` |
| Run on Android | `./gradlew :androidApp:installDebug` |
| Run on Web | `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` |
| Run on iOS (macOS only) | open `iosApp/iosApp.xcodeproj` in Xcode and run |
| iOS tests (macOS only) | `./gradlew :composeApp:iosSimulatorArm64Test` |
