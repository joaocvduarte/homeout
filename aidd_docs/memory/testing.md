# Testing

## Strategy

- Domain and data: plain unit tests with a fixed `Clock` and `TimeZone`.
- UI: Compose UI tests in `commonTest`, run on Wasm (headless Chrome) and on Android devices from the same source.
- Android-only checks live in `androidDeviceTest`: pixel colors of pressed and tinted buttons, and system back.
- Activity recreation lives in `androidApp/src/androidTest` (`RecreationTest`). It uses the real `MainActivity` and `scenario.recreate()`. The manifest's `configChanges` stops a plain rotation from recreating the activity, so rotating alone proves nothing.
- DI: `KoinGraphTest` resolves every binding and ViewModel.
- Accounts: real PBKDF2 runs only in `data`'s Android host tests (with the RFC 7914 vector), since Web throws and 600,000 rounds would slow every UI test.
- Web-only checks live in `wasmJsTest`: browser back through history on the phone `App`, and that a restored old URL still shows the website.
- Every behavior a spec requires has a test. Visual match with the design is judged from screenshots.

## Tools

- `kotlin.test`, `kotlinx-coroutines-test`.
- Compose `ui-test` v2 (`runComposeUiTest`, `runAndroidComposeUiTest`).

## Conventions

- Tests sit beside their module (`<module>/src/commonTest/...`). Fixed instants live in each test, usually Friday 2026-11-13 19:00 UTC.
- Koin in tests: call `withKoin(clock, timeZone, overrides, signedIn = true)` (`composeApp/src/commonTest/.../KoinTestHarness.kt`) inside `runComposeUiTest` or `runTest`, never around it, because Web tests are async. It defaults the time zone to UTC and stops Koin afterwards. It binds an in-memory store and `TestPasswordHasher`, and signs in `maya@example.com` unless `signedIn = false`, which shows Log in. ViewModel unit tests set `Dispatchers.setMain`.
- Rows and key nodes carry test tags: `event-<id>`, `thumb-<id>`, `search`, `event-page`, `ticket-bar`, `tab-<Label>`, `tickets-badge`, `tickets-<eventId>`, `ticket-<code>`, `confirmation`, `method-<METHOD>`, `quantity`, `qr-position` and `qr-pager`, plus the account screens: `login-page`, `login-email`, `login-password`, `login-submit`, `login-google`, `login-mismatch`, `login-create`, `login-forgot`, `signup-page`, `signup-name`, `signup-email`, `signup-password`, `signup-terms`, `signup-submit`, `signup-google`, `signup-taken`, `signup-login-instead`, `signup-login`, and the website's `website`, `website-nav`, `nav-get-app`, `section-top`, `section-events`, `section-how`, `section-app`, `website-event-<id>`, `website-tickets-<id>`, `website-see-all` and `website-footer`.
- Place orders in a test through Koin: `KoinPlatform.getKoin().get<PlaceOrder>()(…)` inside `withKoin`.
- To test Log in with an account, add it through `AccountRepository.add(…)` (it doesn't sign in); `CreateAccount` signs in.
- Android: `RecreationTest` and `SignupRecreationTest` clear the `homeout.accounts` preferences before the activity starts (`getSharedPreferences(…).edit().clear().commit()`), and `SessionTest` relaunches the real activity.
- Gradle needs a full JDK: if Android Studio's `jbr` is missing `lib/jvm.cfg`, use Gradle's own JDK in `~/.gradle/jdks`.
- Website tests (`WebsiteTest`, `WebsiteLayoutTest`, `WebsiteMotionTest`, `WebsiteColorsTest`, `WebsiteFormatTest`, `WebsiteViewModelTest`) run on Wasm and Android. The Wasm test window is 1024×768 and a phone is about 411 dp wide, so taps and pixel reads use windows that fit (1000×700, 400×700); widths beyond that only check layout through `positionInRoot`, since off-screen nodes report empty `boundsInRoot`. Off-screen buttons are activated with `SemanticsActions.OnClick`. `Modifier.reveal` exposes a `hidden`/`revealed` state description for timing tests with `mainClock.autoAdvance = false`.
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

- Entry: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` serves the website at http://localhost:8080/. Reduced motion can be emulated in Chrome DevTools (Rendering, `prefers-reduced-motion`).
- The instrumented `RecreationTest` shares one process and its in-memory orders across its tests. Only one of them buys.
- Auth: none.
- State: `SampleEventRepository`, dated from the current clock. A reload resets everything.
