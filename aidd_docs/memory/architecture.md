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
  - `dataModule` in `data`: the sample events, the in-memory `OrderRepository`, and the `AccountRepository` and `PasswordHasher`, all singletons. The `KeyValueStore` behind the accounts comes from each platform, through `initKoin(platform)`.
  - `domainModule` in `composeApp/di`, holding the use cases (including `PlaceOrder`, `GetTicketGroups`, `CreateAccount`, `LogIn`, `ContinueWithGoogle` and `GetSession`) plus the `Clock` and `Europe/Lisbon` `TimeZone`. It sits there so `domain` stays free of DI code.
  - `presentationModule` in `composeApp/di`, holding the ViewModels
- Koin starts once per process through `initKoin(platform)`: `HomeoutApplication` on Android (SharedPreferences), `main()` on Web (in-memory, never read), and guarded in `MainViewController` on iOS (NSUserDefaults). Never start it from a composable or an activity.
- Accounts: `CreateAccount` and `LogIn` are domain rules over an `AccountRepository` port, and the signed-in email is the session. `StoredAccountRepository` keeps accounts as `account.<email>.name` and `.password`, and the session as `session.email`, in a `KeyValueStore`. It keeps no copy, so it always reads the store. Emails are trimmed and lowercased.
- Passwords: `Pbkdf2PasswordHasher` stores PBKDF2-HMAC-SHA256 as `<iterations>:<salt>:<hash>` in base64 (600,000 rounds, a 16-byte salt, off the main thread). `expect`/`actual` functions supply PBKDF2 and secure random bytes from `javax.crypto` (Android) and CommonCrypto (iOS), and throw on Web, which never hashes.
- Web and phone are separate entries. `App()` is the phone app only. Web's `main()` shows `Website` (`composeApp/.../website/`): one scrolling page with no `NavHost`, fed by `WebsiteViewModel` (the first 4 upcoming events and the total). It always uses the fixed `WebsiteColors` look inside `HoTheme(darkTheme = true)`, whatever the visitor's setting. Its web-only pill, colors and motion stay out of `designsystem`, so the phone app can't change.
- `App` is a shell: background, flower border and header stay fixed while a `NavHost` swaps screens beneath. `RootDestination` holds the Home and Tickets tabs and their bar, and Event, Buy and QR push over it. Each destination takes its ViewModel with `koinViewModel()`, scoped to its back-stack entry, so state survives going back and activity recreation.
- Pay hands its confirmation to the root entry's own `RootViewModel` (`koinViewModel(viewModelStoreOwner = rootEntry)`), then pops back to the root. Don't write to `NavBackStackEntry.savedStateHandle` for this: in navigation 2.10 that handle belongs to a separate internal ViewModel, not the Koin one.
- Orders are domain rules: `PlaceOrder` checks 1–8 and payment-iff-paid, and `GetTicketGroups` numbers and codes tickets per event, in agenda order.
- `EventViewModel` reads its id from `SavedStateHandle.toRoute<EventDestination>()`.
- Website stage: `StageSection` is the hero and the swipe into the next events, two viewports tall with its content pinned. The page opens on the first event's poster full screen, with the promise and two actions over it. `stageOn` (direction and distance of the scroll) flips it on at the first swipe and back off when scrolling up to within 90% of a viewport from the top. `rememberStageFrame` eases each piece (poster clip, frame, flowers, words, side cards, heading) on its own duration and delay, like the design's CSS transitions. The pieces are read inside `graphicsLayer` lambdas, so nothing recomposes per frame. `StageSection` shows the words only until they have lifted away and the heading only once it arrives, so hidden controls take no taps. `Modifier.reveal` fades an item in once, and `SiteMotion` (a `CompositionLocal`) carries the scroll and `reduceMotion`. `main()` reads `prefers-reduced-motion`; with it every piece jumps to its final frame and anchors jump. The design's snap scrolling has no Compose equivalent and is not built.
- `App(onNavHostReady)` stays so tests can bind browser history to the phone's `NavHost`. Android system back comes from the `NavHost` itself.

## Gotchas

- AGP 9: shared modules use `com.android.kotlin.multiplatform.library`, and the Android app is its own module (`androidApp`).
- iOS can't be built on Windows. The target and entry exist and are built and tested on macOS.
- The Android `minSdk` is 26: `PBKDF2WithHmacSHA256` doesn't exist on API 24 and 25.
- Don't add cryptography-kotlin: on Wasm it pulls in `kotlinx-io`, whose import object calls `import.meta`, and every Karma test (and likely the browser bundle) fails with `Cannot use 'import.meta' outside a module`.
- Typed passwords stay in the ViewModel's memory, never in `SavedStateHandle` or `rememberTextFieldState`, which are saved to disk when the process dies.
- The iOS hashing and storage code can't be compiled on Windows. It is checked on macOS.
- Navigation pulls a newer `androidx.activity`, so the catalog pins it to the version navigation resolves.
- Prefer a plain `MutableStateFlow` that's updated directly for screen state. A `combine` plus `stateIn` left the Buy screen blank in Web UI tests: its value arrived on a browser tick the test's idle wait doesn't drive.
