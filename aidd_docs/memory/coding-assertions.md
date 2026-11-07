# Coding Assertions

`JAVA_HOME` must point to a JDK 17+ (Android Studio's bundled `jbr` works). The UI tests on Android need a running emulator or device.

## Before commit

| Order | Command | Checks |
| ----- | ------- | ------ |
| 1 | `./gradlew build` | compiles every target, Android lint |
| 2 | `./gradlew :domain:allTests :data:allTests` | domain and data tests, JVM and Wasm |
| 3 | `./gradlew :designsystem:wasmJsBrowserTest :composeApp:wasmJsBrowserTest` | UI tests in headless Chrome |

## Before push

| Order | Command | Checks |
| ----- | ------- | ------ |
| 1 | `./gradlew :designsystem:connectedAndroidDeviceTest :composeApp:connectedAndroidDeviceTest` | UI and pixel tests on Android, including system back |

## Behavior

If a fix is needed, spawn one agent per failing assertion (for example compile, Wasm UI tests and device tests = 3 agents).
