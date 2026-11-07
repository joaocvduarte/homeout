---
status: in-progress
---

# Instruction: Koin foundation

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
gradle/libs.versions.toml                          ✏️ koin 4.2.2 (bom, core, compose, compose-viewmodel, test)
data/build.gradle.kts                              ✏️ koin-core
data/src/commonMain/kotlin/app/homeout/data/DataModule.kt          ✅ single<EventRepository>
composeApp/build.gradle.kts                        ✏️ koin-core, koin-compose; koin-test in commonTest
composeApp/src/commonMain/kotlin/app/homeout/
├── di/DomainModule.kt          ✅ the use cases' inputs (Clock.System, Europe/Lisbon) and a factory per use case
├── di/InitKoin.kt              ✅ appModules; initKoin(extra: List<Module> = emptyList())
└── App.kt                      ✏️ no clock/timeZone params; repository-free: koinInject the use cases and TimeZone
composeApp/src/wasmJsMain/kotlin/app/homeout/main.kt              ✏️ initKoin() before ComposeViewport
composeApp/src/iosMain/kotlin/app/homeout/MainViewController.kt   ✏️ initKoin() once (guarded)
androidApp/src/main/kotlin/app/homeout/android/HomeoutApplication.kt  ✅ initKoin() in onCreate
androidApp/src/main/AndroidManifest.xml            ✏️ android:name=".HomeoutApplication"
composeApp/src/commonTest/kotlin/app/homeout/
├── KoinTestHarness.kt          ✅ suspend withKoin(clock, timeZone, overrides) { }: startKoin, run, stopKoin in finally
├── di/KoinGraphTest.kt         ✅ every binding resolves; overrides win
├── AppTest.kt                  ✏️ setup through withKoin
└── home/HomeScreenTest.kt      ✏️ setup through withKoin (time zone overridden to UTC, as today)
composeApp/src/wasmJsTest/kotlin/app/homeout/BrowserBackTest.kt    ✏️ setup through withKoin
composeApp/src/androidDeviceTest/kotlin/app/homeout/SystemBackTest.kt ✏️ setup through withKoin
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Inside `runComposeUiTest { withKoin(fixedClock, zone) { … } }`, `withKoin` starts Koin with `appModules` plus the overrides | A clean Koin per test, stopped in `finally` after the suspending body. On wasmJs it runs inside the async test, never around it. | system |
| Happy path   | `KoinGraphTest`: resolve `EventRepository`, the three use cases, `Clock` and `TimeZone` | Each resolves. `TimeZone` is `Europe/Lisbon` and `Clock` is `Clock.System` without overrides. | system |
| Happy path   | Override `Clock` with a fixed instant and `EventRepository` with a fake | The resolved use cases use the override (the fake's single event is returned by `GetEvent`) | system |
| Happy path   | Run every existing composeApp test on wasmJs and Android with the harness | Same assertions, all pass | system |
| Happy path   | Launch the app on Android and in the Web dev server | Home shows the sample events as before. Opening an event and going back still work. | system |
| Edge case    | Two tests in a row | No "Koin already started" error. Each test stops Koin. | system |
| Edge case    | iOS `MainViewController()` called twice | Koin starts once (guarded). The iOS compile is checked on macOS later. | system |

## Tasks to do

### `1)` Dependencies and modules

> Koin can build the whole non-UI graph.

1. Add Koin 4.2.2 to the catalog through `koin-bom`: `koin-core`, `koin-compose`, `koin-compose-viewmodel` and `koin-test`.
2. `data`: add `koin-core`, and `dataModule` with `single<EventRepository> { SampleEventRepository(get(), get()) }`.
3. `composeApp/di`:
   - `domainModule`: the use cases' inputs (`Clock.System`, `TimeZone.of("Europe/Lisbon")`) and a `factory` per use case. There's no extra platform module, so there's one module per layer as the spec says.
   - `appModules`
   - `initKoin(extra)`: calls `startKoin { modules(appModules + extra) }`
4. `KoinGraphTest` and the `withKoin` harness. The harness is a `suspend` function called inside `runComposeUiTest` (or `runTest`). It overrides `Clock` and `TimeZone`, defaulting to UTC like today's tests.

### `2)` Start Koin on every platform

> Each entry starts Koin once; `App` takes no dependencies.

1. Android: `HomeoutApplication.onCreate` calls `initKoin()`, registered in the manifest.
2. Web: `main()` calls `initKoin()` before `ComposeViewport`.
3. iOS: `MainViewController()` calls `initKoin()` only when `KoinPlatform.getKoinOrNull()` is null.
4. `App()`: remove the `clock` and `timeZone` parameters. Get the use cases and `TimeZone` with `koinInject()`. Keep `HomeStateHolder` for now, built from the injected use cases.
5. Move every test that builds `App` or the holder onto `withKoin`. Assertions don't change. `EventFixtures`, `EventFormatTest` and `EventScreenTest` keep building `GetEvent` by hand, because they test screens, not wiring.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `KoinGraphTest` passes on wasmJs and Android, and `domain` has no Koin dependency (`domain/build.gradle.kts` unchanged) |
| 2 | Every existing composeApp test passes through the harness, and the app runs on Android and Web exactly as before |
