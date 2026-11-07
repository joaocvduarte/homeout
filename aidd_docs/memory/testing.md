# Testing

## Strategy

- Domain and data: plain unit tests with a fixed `Clock` and `TimeZone`.
- UI: Compose UI tests in `commonTest`, run on Wasm (headless Chrome) and on Android devices from the same source.
- Android-only checks live in `androidDeviceTest`: pixel colors of pressed and tinted buttons, and system back.
- Activity recreation lives in `androidApp/src/androidTest` (`RecreationTest`). It uses the real `MainActivity` and `scenario.recreate()`. The manifest's `configChanges` stops a plain rotation from recreating the activity, so rotating alone proves nothing.
- DI: `KoinGraphTest` resolves every binding and ViewModel.
- Web-only checks live in `wasmJsTest`: browser back through history.
- Every behavior a spec requires has a test. Visual match with the design is judged from screenshots.

## Tools

- `kotlin.test`, `kotlinx-coroutines-test`.
- Compose `ui-test` v2 (`runComposeUiTest`, `runAndroidComposeUiTest`).

## Conventions

- Tests sit beside their module (`<module>/src/commonTest/...`). Fixed instants live in each test, usually Friday 2026-11-13 19:00 UTC.
- Koin in tests: call `withKoin(clock, timeZone, overrides)` (`composeApp/src/commonTest/.../KoinTestHarness.kt`) inside `runComposeUiTest` or `runTest`, never around it, because Web tests are async. It defaults the time zone to UTC and stops Koin afterwards. ViewModel unit tests set `Dispatchers.setMain`.
- Rows and key nodes carry test tags (`event-<id>`, `search`, `event-page`, `ticket-bar`).
- Web test gotchas:
  - Mocha's timeout is raised to 10 s in `composeApp/karma.config.d`.
  - `waitUntil` starves the browser's event loop, so history tests yield with `window.setTimeout`.
  - Fonts load asynchronously, so font-family equality isn't reliable.
- Test the transition timing with `mainClock.autoAdvance = false`, by checking which screens are present. Layout bounds don't show the slide.

## Run

- See `coding-assertions.md`.

## Browser QA

- Entry: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` serves http://localhost:8080/. An event opens directly at `#app.homeout.EventDestination/<id>`.
- Auth: none.
- State: `SampleEventRepository`, dated from the current clock. A reload resets everything.
