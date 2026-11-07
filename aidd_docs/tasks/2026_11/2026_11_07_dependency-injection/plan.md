---
objective: "Koin provides every dependency from shared code on Android, iOS and Web. Home and the event page get their state from Kotlin ViewModels that keep it through Android activity recreation. `domain` stays free of DI code, tests swap bindings through Koin, and the app looks and behaves exactly as before."
status: implemented
---

# Plan: Dependency injection and ViewModels

Source: `aidd_docs/specs/dependency-injection.md`

## Phases

| #   | Phase        | File                         | Depends on |
| --- | ------------ | ---------------------------- | ---------- |
| 1   | Koin foundation | [`phase-1.md`](./phase-1.md) | none |
| 2   | ViewModels from Koin | [`phase-2.md`](./phase-2.md) | 1 |
| 3   | Recreation survival and docs | [`phase-3.md`](./phase-3.md) | 2 |

## External sources

| URL   | What it settled   |
| ----- | ----------------- |
| https://repo1.maven.org/maven2/io/insert-koin/koin-bom/maven-metadata.xml | Koin 4.2.2 is the latest stable release, published 2026-06-15. 4.3.0 is still a beta. |
| https://repo1.maven.org/maven2/io/insert-koin/koin-compose-viewmodel/4.2.2/koin-compose-viewmodel-4.2.2.module | `koin-compose-viewmodel` ships for Android, iOS and wasm-js. It depends on `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose` 2.9.6, which Gradle raises to the 2.11.0 that navigation already pulls in. |
| https://repo1.maven.org/maven2/io/insert-koin/koin-test/4.2.2/koin-test-4.2.2.module | `koin-test` publishes a `wasmJs` variant, so graph checks can live in `commonTest`. |
| https://repo1.maven.org/maven2/org/jetbrains/androidx/lifecycle/lifecycle-viewmodel-compose/maven-metadata.xml | Multiplatform `lifecycle-viewmodel-compose` 2.11.0 is stable. |
| https://dl.google.com/android/maven2/androidx/compose/ui/ui-test-junit4/maven-metadata.xml | `ui-test-junit4` 1.12.1 (matches the catalog's Jetpack Compose), `androidx.test.ext:junit` 1.3.0 and `androidx.test:core` 1.7.0 are stable. Together they give `createAndroidComposeRule<MainActivity>()` and `scenario.recreate()`. |

## Impact

| Category                                           | Verdict and evidence | Handled in         |
| -------------------------------------------------- | -------------------- | ------------------ |
| runtime | New dependencies: Koin 4.2.2 (core, compose, compose-viewmodel, test) and lifecycle-viewmodel-compose 2.11.0, which is already on the classpath through navigation (observed in phase 4 of the event page plan). Two things are unverified until phase 1 and 2 builds: whether Koin 4.2.2, built against older Compose, works with CMP 1.12.2, and whether `koinViewModel` gets a `SavedStateHandle` holding the navigation route on Wasm. | 1, 2 |
| regression | Medium. Observed today:<br>- `AppTest`, `HomeScreenTest` and `HomeStateHolderTest` build `SampleEventRepository`, the use cases and `HomeStateHolder` by hand.<br>- `BrowserBackTest` and `SystemBackTest` call `App(clock, timeZone)`.<br>All of them move to a Koin test harness.<br>No safety-net phase is needed: those 33 composeApp tests already lock the behavior and must pass unchanged in behavior after each phase. | 1, 2 |
| configuration change | `AndroidManifest.xml` declares `configChanges="orientation\|screenSize\|…\|uiMode"` (observed). Rotation doesn't recreate the activity today, so a rotation test alone would pass without ViewModels. Phase 3 forces a real recreation with `scenario.recreate()`, which proves ViewModel survival for any configuration change. | 3 |
| architecture | Today `App` builds the repository, use cases and `HomeStateHolder` with `remember`, and takes `clock` and `timeZone` as parameters. After this plan:<br>- Each platform entry starts Koin once through `initKoin()`.<br>- `dataModule` lives in `data`. `domainModule` (the use cases plus their clock and time zone) and `presentationModule` (ViewModels) live in `composeApp/di`.<br>- `App()` takes no dependencies.<br>- Each `NavHost` destination gets its own ViewModel through `koinViewModel()`, scoped to its back-stack entry. | 1, 2 |
| documentation | Changes:<br>- `aidd_docs/memory/architecture.md`: wiring, Koin modules, ViewModels<br>- `testing.md`: the Koin test harness, the recreation test<br>- `codebase-map.md`: `di/`, the Application class<br>- `README.md`: the modules table | 3 |

## Rules

| Rule   | Why it applies |
| ------ | -------------- |
| Clean architecture: `domain` free of DI code (spec, `architecture.md`) | The domain Koin module is declared in `composeApp`, not in `domain`. Use cases keep plain constructors. |
| No visible change (spec) | Every existing UI test keeps its assertions. Only its setup changes. |
| Every required behavior has a passing test (spec) | Graph check, binding override, back-stack survival and recreation survival each get a test. |
| Commit after each validated task (`vcs.md`) | One commit per task, as before. |

## Decisions

| Decision   | Why   |
| ---------- | ----- |
| Koin starts globally, once per process: from an `Application` subclass on Android, from `main()` on Web, and guarded in `MainViewController` on iOS. The composable `KoinApplication` and isolated contexts aren't used. | Activity recreation must not rebuild the graph, so ViewModels stay valid. Global start is Koin's standard multiplatform setup. |
| Tests start and stop Koin through a suspend `withKoin(overrides)` helper in `commonTest`, called inside `runComposeUiTest` (whose wasmJs result is async). | Global Koin can only start once at a time. The helper keeps tests isolated and lets them swap the clock or repository. |
| The data Koin module lives in `data`. The domain and presentation modules live in `composeApp/di`. The clock and time zone sit in the domain module, so there's one module per layer. | `domain` must hold no DI code, while `data` may depend on Koin. |
| `HomeStateHolder` becomes `HomeViewModel`. `EventViewModel` reads its event id from `SavedStateHandle` (`toRoute<EventDestination>()`). | ViewModels scoped to back-stack entries survive both navigation and activity recreation, which the spec requires. |
| Keep the manifest's `configChanges`, and prove recreation with `scenario.recreate()`. | Rotation stays smooth, and the test covers every recreation cause, not only rotation. |
