---
status: pending
---

# Instruction: ViewModels from Koin

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
gradle/libs.versions.toml                          ✏️ lifecycle-viewmodel-compose 2.11.0
composeApp/build.gradle.kts                        ✏️ koin-compose-viewmodel, lifecycle-viewmodel-compose
composeApp/src/commonMain/kotlin/app/homeout/
├── di/PresentationModule.kt    ✅ viewModelOf(::HomeViewModel), viewModelOf(::EventViewModel)
├── di/InitKoin.kt              (from phase 1) extended: appModules + presentationModule
├── home/HomeViewModel.kt       ✅ was HomeStateHolder; viewModelScope
├── home/HomeStateHolder.kt     ❌
├── home/HomeScreen.kt          ✏️ HomeRoute(viewModel = koinViewModel(), …)
├── event/EventViewModel.kt     ✅ SavedStateHandle → EventDestination.id → GetEvent; Loading/Missing/Found state
├── event/EventRoute.kt         ✏️ reads EventViewModel state; no produceState
└── App.kt                      ✏️ no koinInject of use cases; destinations call koinViewModel()
composeApp/src/commonTest/kotlin/app/homeout/
├── home/HomeViewModelTest.kt   ✅ was HomeStateHolderTest
├── home/HomeScreenTest.kt      ✏️ HomeRoute takes the ViewModel from Koin
├── home/HomeStateHolderTest.kt ❌
├── event/EventViewModelTest.kt ✅
└── di/KoinGraphTest.kt         (from phase 1) extended: both ViewModels resolve
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | `Dispatchers.setMain(UnconfinedTestDispatcher)` in ViewModel unit tests, `withKoin` in UI tests | ViewModels run deterministically | system |
| Happy path   | `HomeViewModelTest`: the five former holder cases | Same results: every event and the weekly count, category filter, artist search, combined filter, count ignoring filters | system |
| Happy path   | `EventViewModelTest` with `SavedStateHandle` holding `EventDestination("fado-old-cistern")` | State moves from Loading to Found(Fado, isToday = true) | system |
| Edge case    | `EventViewModel` with an unknown id | State becomes Missing | system |
| Happy path   | `KoinGraphTest` | `HomeViewModel` and `EventViewModel` resolve (the event one with a `SavedStateHandle`) | system |
| Happy path   | `AppTest`: pick Food, type "Petiscos", open it, go back | Food selected, "Petiscos" in search, only Petiscos listed. The Home ViewModel is kept on the back stack. | system |
| Happy path   | `AppTest`: unknown event route | Returns Home | system |
| Happy path   | All other existing tests, wasmJs and Android, plus `BrowserBackTest` and `SystemBackTest` | All pass unchanged in behavior | system |
| Happy path   | Check that `App.kt`, the screens and the entry points hold no `SampleEventRepository(`, `GetUpcomingEvents(`, `GetEvent(`, `CountEventsThisWeek(` or `HomeViewModel(` construction (grep) | No hand-built dependencies outside the `di/` and `data` modules | cli |

## Tasks to do

### `1)` HomeViewModel

> Home's state comes from a Koin-provided ViewModel.

1. Add `lifecycle-viewmodel-compose` and `koin-compose-viewmodel`.
2. Rename `HomeStateHolder` to `HomeViewModel : ViewModel`, using `viewModelScope` in place of the passed scope. Its API doesn't change.
3. `presentationModule` with `viewModelOf(::HomeViewModel)`. The Home destination calls `koinViewModel<HomeViewModel>()`.
4. Port `HomeStateHolderTest` to `HomeViewModelTest`, and remove the holder from `App`.

### `2)` EventViewModel

> The event page's state comes from a ViewModel that knows its route.

1. `EventViewModel(savedStateHandle, getEvent)`: the id comes from `savedStateHandle.toRoute<EventDestination>()`, and the state is a `StateFlow` of Loading, Missing or Found.
2. `viewModelOf(::EventViewModel)`. `EventRoute` collects its state, shows `EventScreen` when Found, and calls `onBack` when Missing.
3. `EventViewModelTest`, and add both ViewModels to `KoinGraphTest`.
4. Grep the shell, screens and entry points for hand-built dependencies.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `HomeViewModelTest` and all Home and App tests pass. Filter and search survive back on Android and Web. |
| 2 | `EventViewModelTest`, `KoinGraphTest` and the full composeApp suite pass on wasmJs and Android, and the grep finds no hand-built dependency |
