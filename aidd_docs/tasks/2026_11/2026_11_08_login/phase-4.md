---
status: done
---

# Instruction: Web edition, Get the app and docs

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
.
├── composeApp/src/commonMain/kotlin/app/homeout/
│   ├── AppEdition.kt ✅ App, Web
│   ├── App.kt ✏️ edition: start, destinations, Buy target, header and tab bar
│   ├── AppHeader.kt ✏️ showAccount
│   ├── Destinations.kt ✏️ GetAppDestination(id)
│   ├── root/RootScreen.kt ✏️ showTabs
│   ├── event/EventSummary.kt ✅ the summary card, moved from BuyScreen
│   ├── buy/BuyScreen.kt ✏️ uses EventSummary
│   ├── getapp/
│   │   ├── GetAppScreen.kt ✅ GetAppRoute + GetAppScreen, store badges
│   │   └── GetAppViewModel.kt ✅ reads the id, GetEvent
│   └── di/PresentationModule.kt ✏️ GetAppViewModel
├── composeApp/src/wasmJsMain/kotlin/app/homeout/main.kt ✏️ AppEdition.Web
├── composeApp/src/commonTest/kotlin/app/homeout/
│   ├── getapp/GetAppScreenTest.kt ✅
│   ├── getapp/WebEditionTest.kt ✅ App(edition = Web)
│   └── di/KoinGraphTest.kt ✏️ GetAppViewModel
├── composeApp/src/wasmJsTest/kotlin/app/homeout/BrowserBackTest.kt ✏️ Web edition: back from Get the app; a Buy URL doesn't open Buy
└── aidd_docs/memory/
    ├── project-brief.md ✏️
    ├── architecture.md ✏️
    ├── codebase-map.md ✏️
    ├── navigation.md ✏️
    ├── mobile.md ✏️
    ├── forms.md ✏️
    ├── design.md ✏️
    └── testing.md ✏️
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | `withKoin(signedIn = false)` and `App(edition = AppEdition.Web)` | Home shows, not Log in | browser |
| Happy path   | Open Fado, tap Buy tickets | Get the app pushes in at 360 ms, with Fado's summary, the headline, the body copy and both badges | browser |
| Edge case    | Web header | "HOMEOUT" shows; no "Notifications" and no "MA" | browser |
| Edge case    | Web Home | No `tab-Home`, `tab-Tickets` or `tab-Profile` node | browser |
| Edge case    | A free event's Get free tickets on Web | Get the app opens | browser |
| Edge case    | Back to event, and browser back, from Get the app | The event page returns | browser |
| Edge case    | Tap either badge | Nothing navigates (placeholder) | browser |
| Edge case    | Load the page with a Buy fragment (`#…BuyDestination/fado-old-cistern`) in the Web edition | Get the app for Fado, no crash | browser |
| Edge case    | Load the page with a QR or Login fragment in the Web edition | Home, no crash | browser |
| Edge case    | App edition: Buy tickets | Buy opens, as before | browser |
| Teardown     | Reset the URL with `history.replaceState` | The bare page URL | browser |

## Wireframe

```txt
┌───────────────────────────────┐
│ (1) [HOMEOUT]                 │
│ (2) [←] GET THE APP           │
│ (3) ┌─────────────────────────┐│
│     │ ▓ Fado at the Old Cist… ││
│     │   Fri 14 Nov · 21:30    ││
│     │   Cisterna do Largo, …  ││
│     └─────────────────────────┘│
│ (4) Tickets live in the       │
│     homeout app               │
│     Download the app to …     │
│ (5) [  Download on the        │
│        App Store            ] │
│     [  GET IT ON              │
│        Google Play          ] │
└───────────────────────────────┘
```

1. Web header: the logo only, no account.
2. Top bar: back to the event and the title.
3. The event summary, shared with Buy tickets.
4. The headline and body copy.
5. The App Store and Google Play badges: placeholders that go nowhere.

## Tasks to do

### `1)` Edition switch

> Web shows events only.

1. Add `AppEdition { App, Web }` and `App(edition = AppEdition.App, …)`. Web `main` passes `Web`.
2. Web:
   - start on `RootDestination`
   - a URL fragment the browser restores can name any destination, and an unregistered route would crash navigation. So Web registers every destination: Buy redirects to `GetAppDestination(id)`, and Login, Signup and QR redirect to `RootDestination`, each popping itself
   - `AppHeader(showAccount = false)`
   - `RootScreen(showTabs = false)`, which always shows Home
3. The event page's Buy goes to `GetAppDestination(id)` on Web and `BuyDestination(id)` in the app.
4. `WebEditionTest` and the `BrowserBackTest` additions, including a Buy, a QR and a Login fragment.

### `2)` Get the app

> The design's store hand-off.

1. Move `BuyScreen`'s `Summary` into `event/EventSummary.kt`, taking the event detail. Buy uses it, unchanged.
2. `GetAppViewModel`: reads `GetAppDestination` from `SavedStateHandle.toRoute()` and resolves it through `GetEvent`, into a plain `MutableStateFlow`. An unknown id goes back, as the event page does.
3. `GetAppScreen`:
   - the back button ("Back to event") and the 34 px title
   - `EventSummary`
   - "Tickets live in the homeout app" at Title Large and the body copy in `onSurfaceVariant`
   - two badges: 56 px, radius 10, black, `fieldBorder` border, white text, the Phosphor fill logos
   - each badge clickable with the link role and a description ("Download on the App Store", "Get it on Google Play"), doing nothing for now
4. Tags: `getapp-page`, `badge-app-store`, `badge-google-play`.
5. Register `GetAppViewModel`, and add it to `KoinGraphTest`.
6. Design check:
   - screenshot Web Home and Get the app in the browser (`wasmJsBrowserDevelopmentRun`) in light and dark
   - compare each with the design's Web frames (`platform="web"`, `home` and `getapp`)
   - fix each difference, or record why it stays

### `3)` Docs

> Memory matches the code.

1. Update the memory files listed in the projection, as the plan's documentation impact says.
2. Set `plan.md` and the phase files to `implemented` once every assertion passes.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | Web opens on Home with no Log in, no account in the header and no tab bar. A Buy URL opens Get the app, and a QR or Login URL opens Home, without crashing. The app edition is unchanged |
| 2 | Buy tickets on Web opens Get the app with the event's summary and both badges, in light and dark. Back and browser back return to the event. The badges don't navigate. The browser screenshots match the design's Web frames, or each difference is recorded |
| 3 | The memory files describe accounts, editions and the new screens |
