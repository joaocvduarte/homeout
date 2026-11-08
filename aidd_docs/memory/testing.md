# Testing

## Strategy

- Domain and data: plain unit tests with a fixed `Clock` and `TimeZone`.
- UI: Compose UI tests in `commonTest`, run on Wasm (headless Chrome) and on Android devices from the same source.
- Android-only checks live in `androidDeviceTest`: pixel colors of pressed and tinted buttons, and system back.
- Activity recreation lives in `androidApp/src/androidTest` (`RecreationTest`). It uses the real `MainActivity` and `scenario.recreate()`. The manifest's `configChanges` stops a plain rotation from recreating the activity, so rotating alone proves nothing.
- DI: `KoinGraphTest` resolves every binding and ViewModel.
- Accounts: real PBKDF2 runs only in `data`'s Android host tests (with the RFC 7914 vector), since Web throws and 600,000 rounds would slow every UI test.
- Web-only checks live in `wasmJsTest`: browser back through history.
- Every behavior a spec requires has a test. Visual match with the design is judged from screenshots.

## Tools

- `kotlin.test`, `kotlinx-coroutines-test`.
- Compose `ui-test` v2 (`runComposeUiTest`, `runAndroidComposeUiTest`).

## Conventions

- Tests sit beside their module (`<module>/src/commonTest/...`). Fixed instants live in each test, usually Friday 2026-11-13 19:00 UTC.
- Koin in tests: call `withKoin(clock, timeZone, overrides, signedIn = true)` (`composeApp/src/commonTest/.../KoinTestHarness.kt`) inside `runComposeUiTest` or `runTest`, never around it, because Web tests are async. It defaults the time zone to UTC and stops Koin afterwards. It binds an in-memory store and `TestPasswordHasher`, and signs in `maya@example.com` unless `signedIn = false`, which shows Log in. ViewModel unit tests set `Dispatchers.setMain`.
- Rows and key nodes carry test tags: `event-<id>`, `thumb-<id>`, `search`, `event-page`, `ticket-bar`, `tab-<Label>`, `tickets-badge`, `tickets-<eventId>`, `ticket-<code>`, `confirmation`, `method-<METHOD>`, `quantity`, `qr-position` and `qr-pager`, plus the account screens: `login-page`, `login-email`, `login-password`, `login-submit`, `login-google`, `login-mismatch`, `login-create`, `login-forgot`, `signup-page`, `signup-name`, `signup-email`, `signup-password`, `signup-terms`, `signup-submit`, `signup-google`, `signup-taken`, `signup-login-instead`, `signup-login`, and Web's `getapp-page`, `badge-app-store`, `badge-google-play`.
- Place orders in a test through Koin: `KoinPlatform.getKoin().get<PlaceOrder>()(…)` inside `withKoin`.
- To test Log in with an account, add it through `AccountRepository.add(…)` (it doesn't sign in); `CreateAccount` signs in.
- Android: `RecreationTest` and `SignupRecreationTest` clear the `homeout.accounts` preferences before the activity starts (`getSharedPreferences(…).edit().clear().commit()`), and `SessionTest` relaunches the real activity.
- Gradle needs a full JDK: if Android Studio's `jbr` is missing `lib/jvm.cfg`, use Gradle's own JDK in `~/.gradle/jdks`.
- Web test gotchas:
  - `runTest` is asynchronous on Web: load data inside `runComposeUiTest`'s own suspend body, not in a `runTest` beside it.
  - Mocha's timeout is raised to 10 s in `composeApp/karma.config.d`.
  - `waitUntil` starves the browser's event loop, so history tests yield with `window.setTimeout`.
  - Browser-history tests reset the URL first (`history.replaceState`), because the app reads its first route from the fragment a previous test left.
  - Fonts load asynchronously, so font-family equality isn't reliable.
- Test the transition timing with `mainClock.autoAdvance = false`, by checking which screens are present. Layout bounds don't show the slide.

## Run

- See `coding-assertions.md`.

## Browser QA

- Entry: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` serves http://localhost:8080/. An event opens directly at `#app.homeout.EventDestination/<id>`.
- The instrumented `RecreationTest` shares one process and its in-memory orders across its tests. Only one of them buys.
- Auth: none.
- State: `SampleEventRepository`, dated from the current clock. A reload resets everything.
