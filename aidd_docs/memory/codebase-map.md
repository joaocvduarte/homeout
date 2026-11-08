# Codebase Map

```mermaid
flowchart TD
  androidApp --> composeApp
  iosApp --> composeApp
  composeApp --> designsystem
  composeApp --> data
  data --> domain
```

## Areas

- `domain`: the event model and use cases, orders, and accounts (`CreateAccount`, `LogIn`, `ContinueWithGoogle`, `GetSession`). Pure Kotlin.
- `data`: the local sample events (`SampleEventRepository`), the in-memory orders, and the accounts: `StoredAccountRepository`, `Pbkdf2PasswordHasher`, and a `KeyValueStore` per platform (`SharedPreferencesStore`, `UserDefaultsStore`, `InMemoryKeyValueStore`).
- `designsystem`: colors, type scale, theme, buttons, icons, the flower border, fonts and licenses. Its `Res` is internal, so composeApp reaches images only through designsystem components.
- `composeApp`: the app shell (`App`, `AppHeader`, `Destinations`), `di/` (Koin modules, `initKoin`), the `auth/` (Log in, Create account), `home/`, `event/` and `getapp/` (Web's store hand-off) screens with their ViewModels, `AppEdition`, plus the iOS and Web entry points.
- `androidApp`: the Android entry point (`HomeoutApplication` starts Koin, `MainActivity` shows the app), and its instrumented `RecreationTest`.
- `iosApp`: the Xcode project that hosts `MainViewController`.
- `aidd_docs/specs/`: one spec per feature. `aidd_docs/tasks/<yyyy_mm>/`: plans and phases.

## Entry points

- Android: `androidApp/.../MainActivity.kt`
- Web: `composeApp/src/wasmJsMain/kotlin/app/homeout/main.kt`
- iOS: `composeApp/src/iosMain/kotlin/app/homeout/MainViewController.kt`
