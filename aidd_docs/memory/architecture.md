# Architecture

## Stack

- Kotlin Multiplatform with Compose Multiplatform: one shared codebase for Android, iOS and Web (Kotlin/Wasm).
- kotlinx-datetime for every date and time. Events are shown in `Europe/Lisbon`.
- Navigation Compose with `@Serializable` destinations (kotlinx-serialization).
- Koin for dependency injection, from shared code on every platform.
- Multiplatform `lifecycle` ViewModels, one per screen, exposing `StateFlow`.

## How it fits together

```mermaid
flowchart LR
  Platform["androidApp · iOS · Web entry"] --> App["composeApp: App shell + screens"]
  App --> DS["designsystem: tokens, type, components"]
  App --> Data["data: SampleEventRepository"]
  Data --> Domain["domain: Event, use cases"]
  App --> Domain
```

## Key decisions

- Clean architecture: `domain` has no Compose, platform or data dependency. Use cases (`GetUpcomingEvents`, `GetEvent`, `CountEventsThisWeek`) take an `EventRepository`, a `Clock` and a `TimeZone`.
- There's no backend yet. `SampleEventRepository` dates its events today and tomorrow from the clock, so they always look upcoming. Real data will come from a backend later.
- Poster colors are event data (ARGB `Long` in `PosterColors`), not design tokens, so `domain` stays free of Compose.
- Koin over Hilt: Hilt can't inject shared multiplatform code. There's one Koin module per layer:
  - `dataModule` in `data`: the sample events and the in-memory `OrderRepository`, both singletons
  - `domainModule` in `composeApp/di`, holding the use cases (including `PlaceOrder` and `GetTicketGroups`) plus the `Clock` and `Europe/Lisbon` `TimeZone`. It sits there so `domain` stays free of DI code.
  - `presentationModule` in `composeApp/di`, holding the ViewModels
- Koin starts once per process through `initKoin()`: `HomeoutApplication` on Android, `main()` on Web, and guarded in `MainViewController` on iOS. Never start it from a composable or an activity.
- `App` is a shell: background, flower border and header stay fixed while a `NavHost` swaps screens beneath. `RootDestination` holds the Home and Tickets tabs and their bar, and Event, Buy and QR push over it. Each destination takes its ViewModel with `koinViewModel()`, scoped to its back-stack entry, so state survives going back and activity recreation.
- Pay hands its confirmation to the root entry's own `RootViewModel` (`koinViewModel(viewModelStoreOwner = rootEntry)`), then pops back to the root. Don't write to `NavBackStackEntry.savedStateHandle` for this: in navigation 2.10 that handle belongs to a separate internal ViewModel, not the Koin one.
- Orders are domain rules: `PlaceOrder` checks 1–8 and payment-iff-paid, and `GetTicketGroups` numbers and codes tickets per event, in agenda order.
- `EventViewModel` reads its id from `SavedStateHandle.toRoute<EventDestination>()`.
- Web binds browser history through the `onNavHostReady` hook in `wasmJsMain/main.kt`. Android system back comes from the `NavHost` itself.

## Gotchas

- AGP 9: shared modules use `com.android.kotlin.multiplatform.library`, and the Android app is its own module (`androidApp`).
- iOS can't be built on Windows. The target and entry exist and are built and tested on macOS.
- Navigation pulls a newer `androidx.activity`, so the catalog pins it to the version navigation resolves.
- Prefer a plain `MutableStateFlow` that's updated directly for screen state. A `combine` plus `stateIn` left the Buy screen blank in Web UI tests: its value arrived on a browser tick the test's idle wait doesn't drive.
