---
status: done
---

# Instruction: Project scaffold

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
C:\Users\joao_\Documents\homeout
├── settings.gradle.kts                       ✅
├── build.gradle.kts                          ✅
├── gradle.properties                         ✅
├── local.properties                          ✅ sdk.dir (not committed)
├── .gitignore                                ✅
├── README.md                                 ✅
├── gradle/libs.versions.toml                 ✅
├── gradle/wrapper/…, gradlew, gradlew.bat    ✅
├── domain/build.gradle.kts                   ✅
├── data/build.gradle.kts                     ✅
├── designsystem/build.gradle.kts             ✅
├── androidApp/                               ✅ com.android.application; depends on composeApp
│   ├── build.gradle.kts                      ✅
│   └── src/main/
│       ├── kotlin/app/homeout/android/MainActivity.kt           ✅
│       └── AndroidManifest.xml                                  ✅
├── composeApp/                               ✅ com.android.kotlin.multiplatform.library
│   ├── build.gradle.kts                      ✅
│   └── src/
│       ├── commonMain/kotlin/app/homeout/App.kt                 ✅
│       ├── commonTest/kotlin/app/homeout/AppTest.kt             ✅
│       ├── iosMain/kotlin/app/homeout/MainViewController.kt     ✅
│       └── wasmJsMain/kotlin/app/homeout/main.kt + resources/index.html ✅
├── iosApp/ (Xcode project calling MainViewController)          ✅
└── aidd_docs/tasks/2026_11/2026_11_06_homeout-event-app/        ✅ spec.md, plan.md, phase-*.md moved here
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Set `JAVA_HOME` to Android Studio's JBR and write `local.properties` with the SDK path | Gradle runs with JDK 21 and finds the SDK | cli |
| Happy path   | Run `./gradlew build` | All five modules compile for Android and wasmJs, with no Kotlin/AGP/Gradle compatibility warnings, and the build succeeds | cli |
| Happy path   | Run `AppTest` with `wasmJsBrowserTest` in headless Chrome | `App()` renders with no error | cli |
| Happy path   | Install the debug APK on the `Pixel_8_Pro` emulator and run `wasmJsBrowserDevelopmentRun` | Both show the app's empty root surface | system |
| Edge case    | A `domain` source imports a Compose or `data` symbol | The build fails, so the layer boundary holds | cli |
| Teardown     | Remove the deliberate bad import | The build is green again | cli |

## Tasks to do

### `1)` Create the Gradle project

> A multiplatform project with Android, iOS and wasmJs targets that builds on this machine.

1. Create `C:\Users\joao_\Documents\homeout` with a Gradle 9.7.0 wrapper.
2. Pin these versions in `libs.versions.toml`: Kotlin 2.4.21 (plus the Compose Compiler plugin at the same version), Compose Multiplatform 1.12.2, AGP 9.3.1, kotlinx-coroutines 1.11.0, kotlinx-datetime 0.8.0, compose ui-test 1.12.2.
3. Write `local.properties` with `sdk.dir`, and keep it out of git with `.gitignore`.

### `2)` Create the layer modules

> Modules whose dependency direction enforces clean architecture.

1. Every shared module applies `org.jetbrains.kotlin.multiplatform` with `com.android.kotlin.multiplatform.library`, and targets android, iosArm64, iosSimulatorArm64 and wasmJs.
2. `domain`: no Compose; depends on kotlinx-datetime only.
3. `data`: depends on `domain`.
4. `designsystem`: Compose plus resources; depends on no app module. Enable `withDeviceTest { }`.
5. `composeApp`: depends on all three, and holds the iOS and wasmJs entry points. Enable `withDeviceTest { }`.
6. `androidApp`: applies `com.android.application` and depends on `composeApp`.

### `3)` Add the platform entry points

> Each platform shows the shared `App()`.

1. `MainActivity` in `androidApp`, `MainViewController` on iOS, and `ComposeViewport` in wasmJs `main.kt` with `index.html`.
2. Add the `iosApp` Xcode project that hosts `MainViewController`. It is unbuilt on Windows.

### `4)` Move the docs and write the README

> The project holds its own spec and plan.

1. Move this task folder into `homeout/aidd_docs/tasks/2026_11/`.
2. Write a `README.md` with the build, run and test commands for each target, and note that iOS needs macOS.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `./gradlew build` succeeds with the pinned versions and prints no compatibility warning |
| 2 | A domain-to-UI import fails the build |
| 3 | The app launches on the Android emulator and in Chrome; `AppTest` passes on wasmJs |
| 4 | The spec and plan sit under `homeout/aidd_docs`; the README commands run as written |
