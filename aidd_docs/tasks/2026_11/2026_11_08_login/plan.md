---
objective: "On Android and iOS people create an account or log in, checked against accounts kept on the phone, before they reach Home and stay signed in across restarts, while Web opens straight on Home with no account, no tab bar and a Get the app screen for buying, all matching design 1794131199-48a3 in both themes, with every behavior tested."
status: implemented
---

# Plan: Log in, Create account and the Web edition

Source: `aidd_docs/specs/login.md`, and the design https://claude.ai/artifact/1krt444a78B46kwb7jYipL (Home Screen and homeout UI Library pages, version 1794131199-48a3, updated 2026-11-08).

## Phases

| #   | Phase        | File                         | Depends on |
| --- | ------------ | ---------------------------- | ---------- |
| 1   | Accounts in domain and data | [`phase-1.md`](./phase-1.md) | none |
| 2   | Design-system additions | [`phase-2.md`](./phase-2.md) | none |
| 3   | Log in and Create account screens | [`phase-3.md`](./phase-3.md) | 1, 2 |
| 4   | Web edition, Get the app and docs | [`phase-4.md`](./phase-4.md) | 3 |

## External sources

| URL   | What it settled   |
| ----- | ----------------- |
| https://claude.ai/artifact/1krt444a78B46kwb7jYipL | Design version 1794131199-48a3, Home Screen page. It settled:<br>- **Log in:** logo 52 px, tagline at Title Large, Google button (surface fill, divider border, 52 px), "or log in with email" divider, labelled Email and Password fields (48 px, radius 8, `field` fill, `fieldBorder` border), "Forgot password?" in `accentText`, tinted "Log in" button, "New to homeout?" footer.<br>- **Create account:** back button "Back to log in", 36 px title, Google button, Name / Email / Password fields, the hint "At least 8 characters.", the terms checkbox, tinted "Create account" button, "Already have an account?" footer.<br>- **Errors:** after blur, an invalid email shows "Enter an email like you@example.com." and a short password shows "At least 8 characters." in `error`, with the field border in `error`. The alerts sit above the form with an `error` border and `errorTint` fill, a 20 px alert-circle glyph and the spec's copy. "Log in instead" is an underlined `accentText` button. Disabled buttons fade.<br>- **Tokens:** light `error #a12d3a`, `errorTint rgba(161,45,58,0.10)`, `field rgba(255,255,255,0.55)`, `fieldBorder #7d9894`, `surfaceHover #c4d6d3`; dark `error #ffb3b8`, `errorTint rgba(255,179,184,0.14)`, `field rgba(51,67,64,0.85)`, `fieldBorder #6f8a86`, `surfaceHover #3c4e4b`.<br>- **Transitions:** login depth −2, signup −1, root 0, so signing in slides both away left. The header fades in over 360 ms.<br>- **Web:** no status bar, header with the logo only, no tab bar. Get the app at push depth 2: back "Back to event", 34 px title, the Buy summary, "Tickets live in the homeout app", the body copy, and black App Store and Google Play badges (56 px, radius 10, `fieldBorder` border, Phosphor fill `apple-logo` and `google-play-logo` at 28 px). |
| https://repo1.maven.org/maven2/dev/whyoleg/cryptography/cryptography-core/maven-metadata.xml | cryptography-kotlin 0.6.0 is rejected. Observed on 2026-11-08: on Wasm it pulls in `kotlinx-io-core` 0.9.0, whose import object calls `import.meta`, so `:data:wasmJsBrowserTest` fails with `Cannot use 'import.meta' outside a module`. Without the library the same test passes. |
| https://repo1.maven.org/maven2/com/russhwolf/multiplatform-settings-no-arg/maven-metadata.xml | multiplatform-settings' latest release is 1.3.0, from November 2024. It's rejected: it predates Kotlin 2.4 by two years, and two small platform stores do the job without it. |

## Impact

| Category                                           | Verdict and evidence | Handled in         |
| -------------------------------------------------- | -------------------- | ------------------ |
| runtime | Two new things run outside the repo:<br>- **Storage:** Android `SharedPreferences` and iOS `NSUserDefaults`, both platform APIs. Unverified on iOS: it can't be built on Windows.<br>- **Hashing:** the platform's own PBKDF2 and secure random, with no library: `javax.crypto` on Android, CommonCrypto on iOS. Web never hashes. Unverified on iOS: it can't be built on Windows.<br>Android's `PBKDF2WithHmacSHA256` exists only from API 26, while minSdk is 24 (observed in `libs.versions.toml`). Phase 1 raises minSdk to 26.<br>Android auto-backup is on (the manifest doesn't set `allowBackup`), so stored hashes would be backed up with the app's data. The spec doesn't ask to change that, so the plan leaves it as is and flags it. | 1 |
| regression | High, observed:<br>- Every UI test that calls `App()` expects Home first: `AppTest`, `PurchaseFlowTest`, `TicketQrFlowTest`, `RootScreenTest`, `SystemBackTest` and `BrowserBackTest`. They run through `withKoin`, which in phase 1 gains a default signed-in session, so they keep opening on Home.<br>- `RecreationTest` launches the real `MainActivity` with real storage, so it will open on Log in. Phase 3 clears storage before each test and signs in with the Google button.<br>- `AppTest.headerStaysAndBottomNavHidesOnTheEventPage` asserts the header shows "HOMEOUT". The app edition keeps it.<br>- `BuyScreen`'s `Summary` moves into a shared composable. `BuyScreenTest` covers it.<br>- The `initKoin` signature changes for the platform store. Its five callers change: `HomeoutApplication`, `main.kt`, `MainViewController`, `KoinTestHarness` and `KoinGraphTest`.
- `androidApp` only sees `composeApp`'s API, and `composeApp` imports `data` and Koin as `implementation`. So `androidApp/build.gradle.kts` gains both, to bind `SharedPreferencesStore`.<br>No separate safety-net phase: the 60+ existing composeApp tests run after every phase. | 1, 3, 4 |
| architecture | Today the `NavHost` starts on `RootDestination`, and Koin has data, domain and presentation modules. After this plan:<br>- **Accounts:** domain gains `AccountRepository`, `PasswordHasher` and the use cases `CreateAccount`, `LogIn`, `ContinueWithGoogle` and `GetSession`, plus `Credentials` rules. Data implements them over a `KeyValueStore`. Each platform supplies its store through a Koin module passed to `initKoin`: SharedPreferences, NSUserDefaults, or in-memory on Web and in tests.<br>- **Navigation:** `LoginDestination` and `SignupDestination` sit before Root. The start destination is Login unless a session exists. Signing in pops both, so back from Home leaves the app.<br>- **Editions:** `App(edition)` takes `AppEdition.App` or `AppEdition.Web`. Web hides the header's account and the tab bar, and sends Buy to `GetAppDestination(id)`. App-only routes still exist on Web but redirect, so a restored URL fragment can't crash navigation. | 1, 3, 4 |
| documentation | Changes:<br>- `project-brief.md`: accounts, Web for browsing only, terms (Account, Session)<br>- `architecture.md`: accounts, platform stores, editions, `initKoin(platform)`<br>- `codebase-map.md`: `auth/`, `getapp/`, the data platform stores<br>- `navigation.md`: Login, Signup, GetApp, the start destination, back from Home<br>- `mobile.md`: storage on the device, the flow diagram<br>- `forms.md`: the account forms and their checks<br>- `design.md`: the new tokens, `HoTextField`, `HoCheckbox`, glyphs and icons<br>- `testing.md`: `withKoin(signedIn)`, new tags, clearing storage in `RecreationTest`<br>- README: none | 4 |

## Rules

| Rule   | Why it applies |
| ------ | -------------- |
| Clean architecture: `domain` has no Compose, platform, data or DI code (`architecture.md`) | Accounts, credential rules and the use cases are domain. Stores and hashing are data. |
| Koin, one module per layer, started once per process through `initKoin()` (`architecture.md`) | Each platform's store joins through `initKoin`, never from a composable. |
| One ViewModel per screen, plain `MutableStateFlow` for screen state (`architecture.md` gotcha) | Login, Signup and GetApp get ViewModels. No `combine` plus `stateIn`, which left the Buy screen blank on Web. |
| Screens use `HoTheme`, `HoText` and `HoButton`, never raw Material (`design.md`) | The new field and checkbox live in `designsystem`. |
| WCAG AA in both themes (`design.md`, spec) | `error`, placeholders on `field`, and the hint join `HoContrastTest`. |
| Icon-only buttons carry a content description; decorative images have none (`design.md`) | Back to log in, Back to event, the badges, the alert glyph. |
| Koin in tests: `withKoin` inside `runComposeUiTest`, browser-history tests reset the URL (`testing.md`) | New UI tests follow it. |
| Commit after each validated task (`vcs.md`) | As before. |

## Decisions

| Decision   | Why   |
| ---------- | ----- |
| Passwords are stored as PBKDF2-HMAC-SHA256 hashes (600,000 iterations, a 16-byte random salt, a 32-byte key), as `<iterations>:<salt>:<hash>` in base64. The format, the base64 and the constant-time compare are shared code; `expect`/`actual` functions supply PBKDF2 and secure random bytes from `javax.crypto` (Android) and CommonCrypto (iOS). Wasm's actual throws, because Web has no accounts. Tests inject a 1-iteration hasher. | The spec says a password is never stored as typed. PBKDF2 at OWASP's iteration count is the standard choice for a local password check. User decision, 2026-11-08: cryptography-kotlin breaks the Wasm tests and bundle, so the platform crypto is used directly. Storing the iteration count lets it change later. |
| A `KeyValueStore` interface in data, with SharedPreferences (Android), NSUserDefaults (iOS) and in-memory (Web, tests) implementations, each bound by its platform's entry point. | The reads are synchronous, so the start destination is known on first composition and Login never flashes before Home. It needs no third-party dependency. |
| Accounts are keys in the store: `account.<email>.name`, `account.<email>.password`, with `session.email` for the signed-in person. Emails are trimmed and lowercased. | No serialization dependency is needed in data, and case differences can't create duplicate accounts. |
| Fake Google sign-in records a session for the fixed placeholder `google@homeout.local`, with no stored account. | The spec says Google goes straight to Home and whoever signs in stays signed in. A session is what makes the next launch open on Home. |
| Typed passwords stay in ViewModel memory, never in `SavedStateHandle`. Name, emails, the terms box and the touched and error flags go in `SavedStateHandle`. | Recreation keeps the ViewModel, so the password survives a rotation. Saved state is written to disk when the process dies, and a password shouldn't land there. |
| Raise Android minSdk from 24 to 26. | PBKDF2-HMAC-SHA256 isn't on Android 7 (API 24–25), so sign-in would crash there. Android 7 is a tiny share of devices, and a weaker hash would break the spec's password rule. |
| `App(edition: AppEdition)`: Web's `main` passes `AppEdition.Web`, Android and iOS default to `AppEdition.App`. | Both editions stay testable on every platform from `commonTest`, and only the edition decides what Web shows. |
