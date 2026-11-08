---
status: done
---

# Instruction: Navigation, back and app shell

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
gradle/libs.versions.toml                       ✏️ navigation-compose 2.10.0, serialization 1.12.0, plugin; androidx-activity raised if navigation needs it
composeApp/build.gradle.kts                     ✏️ serialization plugin and dependencies; activity-compose for the device test
composeApp/src/
├── commonMain/kotlin/app/homeout/
│   ├── App.kt                     ✏️ shell: background, flowers, header, NavHost
│   ├── AppHeader.kt               ✅ wordmark, bell, avatar (moved from HomeScreen)
│   ├── Routes.kt                  ✅ @Serializable HomeRoute and EventRoute(id)
│   ├── BrowserNavigation.kt       ✅ expect bindBrowserBack(navController)
│   ├── home/HomeScreen.kt         ✏️ no header or flowers; rows take onEventClick
│   ├── home/EventRow.kt           ✏️ clickable, Role.Button
│   └── event/EventRoute.kt        ✅ GetEvent → EventScreen; unknown id pops back
├── wasmJsMain/kotlin/app/homeout/BrowserNavigation.wasmJs.kt   ✅ bindToBrowserNavigation()
├── androidMain/kotlin/app/homeout/BrowserNavigation.android.kt ✅ no-op
├── iosMain/kotlin/app/homeout/BrowserNavigation.ios.kt         ✅ no-op
├── commonTest/kotlin/app/homeout/AppTest.kt                    ✏️ open, back, state kept, header stays, transition
├── wasmJsTest/kotlin/app/homeout/BrowserBackTest.kt            ✅ browser back returns Home
├── commonTest/kotlin/app/homeout/home/HomeScreenTest.kt        ✏️ rows open their event; header assertions move to AppTest
└── androidDeviceTest/kotlin/app/homeout/SystemBackTest.kt      ✅ Android system back returns Home
README.md                                       ✏️ modules table
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | `App` with a fixed clock in Lisbon | Home is shown | system |
| Happy path   | Tap each of the four event rows in turn, going back each time | Each tap opens that event's page with its own title | system |
| Happy path   | On an event page | "HOMEOUT" header shown, bottom navigation (Explore, Tickets…) not shown | system |
| Happy path   | Pick Food, type "Petiscos", open it, tap Back | Home again with Food selected and "Petiscos" in the search. Only Petiscos is listed. | system |
| Happy path   | Android: `runAndroidComposeUiTest<ComponentActivity>`, open an event, call `activity.onBackPressedDispatcher.onBackPressed()` | Home is shown with the same filter and search | system |
| Happy path   | Web (`BrowserBackTest`, headless Chrome): open an event, call `window.history.back()`, wait for idle | Home is shown, and `window.location.hash` changed on open and on back | browser |
| Happy path   | `AppTest` with `mainClock.autoAdvance = false`: open an event, then advance 180 ms, then 360 ms | At 180 ms both screens are present, and the event page is offset right but less than its full width. At 360 ms only the event page is left. Back mirrors it. | system |
| Edge case    | Navigate to `EventRoute("unknown")` (a typed Web URL fragment) | It returns to Home, with no blank page and no crash | system |
| Edge case    | On Home, press system back or browser back | Default platform behavior (the app closes or the browser leaves). No loop. | browser |
| Happy path   | Screenshot Android (night on and off) and Web (dark and light), Home and an event page | Both match the design. Header and flowers stay fixed during the transition. | system |

## Tasks to do

### `1)` Dependencies

> Navigation and serialization are available.

1. Add `navigation-compose` 2.10.0, `kotlinx-serialization-core` 1.12.0 and the `org.jetbrains.kotlin.plugin.serialization` plugin (Kotlin version) to the catalog and to `composeApp`.
2. Add `androidx-activity-compose` to `composeApp`'s `androidDeviceTest` dependencies.
3. Run `./gradlew :androidApp:dependencies --configuration debugRuntimeClasspath`. If navigation pulls `androidx.activity` above 1.10.1, raise the catalog's `androidx-activity` to match, so `MainActivity` compiles against the version it runs with.
4. `./gradlew build`: confirm it resolves and compiles with CMP 1.12.2.

### `2)` App shell

> Header and flowers stay fixed; screens swap beneath.

1. Move `Header` from `HomeScreen` into `AppHeader.kt`. Move the background, `HoFlowerBorder`, the status-bar band and the header into `App`.
2. Below them, a `NavHost` with `HomeRoute` and `EventRoute(id)`. `HomeStateHolder` stays remembered in `App`, outside the `NavHost`.
3. `HomeScreen` keeps its scrolling content and bottom bar. Its search field keeps its text across navigation (saveable state).

### `3)` Open and close

> Rows open their event; every back returns Home.

1. `EventRow` takes `onClick`, with `Role.Button` and an accessible label, and navigates to `EventRoute(event.id)`.
2. `EventRoute` runs `GetEvent`. If the event exists it shows `EventScreen` with `onBack = navController::popBackStack`. If not, it pops back.
3. Transitions on `NavHost`, all 360 ms with easing `CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)`:
   - event enters by sliding in from the full width
   - Home exits by sliding to −28% and fading out
   - on back, the reverse (pop transitions)
4. `bindBrowserBack`: on wasmJs, call `navController.bindToBrowserNavigation()` once the NavHost is ready. On Android and iOS it does nothing, because the NavHost already handles system back.

### `4)` Tests and docs

> Lock the behavior and update the README.

1. Update `HomeScreenTest`: the rows-not-clickable test becomes "each row has a click action". Move the "HOMEOUT" checks to `AppTest`.
2. `AppTest`: opening each event, the header staying while the bottom nav hides, filter and search surviving Back, and an unknown id returning Home.
3. `SystemBackTest` (Android device): system back returns Home with its state.
4. `BrowserBackTest` (wasmJs): `window.history.back()` returns Home. If the test browser can't drive history this way, record that and check it by hand in the dev server instead.
5. `AppTest`: the transition test with a paused clock.
6. Update the `README.md` modules row for `composeApp`.
7. Screenshot both platforms and themes, and compare with the design. Visual match stays a screenshot check, as in the earlier plans.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `./gradlew build` passes with the new dependencies |
| 2 | Home looks unchanged, apart from the fixed header, and all Home tests pass |
| 3 | Tapping any row opens its event page, and Back returns Home with the push transition |
| 4 | `AppTest`, `HomeScreenTest`, `SystemBackTest` and `BrowserBackTest` pass. The screenshots match the design. |
