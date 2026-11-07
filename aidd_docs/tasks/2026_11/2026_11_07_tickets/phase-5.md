---
status: pending
---

# Instruction: Ticket QR code, back paths and docs

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
composeApp/src/commonMain/kotlin/app/homeout/
├── Destinations.kt              ✏️ QrDestination(eventId, index)
├── App.kt                       ✏️ tickets → qr
├── qr/QrScreen.kt               ✅ title, position, pager of ticket cards, dots, arrows, hint
├── qr/QrViewModel.kt            ✅ the route's event group from GetTicketGroups
├── qr/QrPattern.kt              ✅ placeholder 25×25 modules from the ticket code (ported from the design)
├── qr/QrCode.kt                 ✅ draws the modules on white
└── di/PresentationModule.kt     ✏️ QrViewModel
composeApp/src/commonTest/kotlin/app/homeout/
├── qr/QrPatternTest.kt          ✅ against Node-generated fixtures from the design's JS
├── qr/QrScreenTest.kt           ✅
└── AppTest.kt                   ✏️ tickets → qr → back
composeApp/src/wasmJsTest/kotlin/app/homeout/BrowserBackTest.kt        ✏️ buy and qr
composeApp/src/androidDeviceTest/kotlin/app/homeout/SystemBackTest.kt  ✏️ buy and qr
androidApp/src/androidTest/kotlin/app/homeout/android/RecreationTest.kt ✏️ buy count and qr page survive
aidd_docs/memory/{project-brief,architecture,navigation,design,testing}.md ✏️
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Run the design's `qrPath` in Node for `HO-FADO-001`, `HO-FADO-002` and `HO-NIGH-001`, and save the dark-module sets as fixtures | Fixtures in the test | cli |
| Happy path   | `QrPatternTest` | The Kotlin pattern equals each fixture, the three finders and the alignment square are present, and it's deterministic | system |
| Happy path   | Buy 3 Fado, then tap "Ticket 2 of 3" | The QR screen opens on ticket 2: "YOUR TICKET", "Fado at the Old Cistern · Ticket 2 of 3", `HO-FADO-002`, and the 3-ticket hint | system |
| Happy path   | Next arrow, then swipe left | Moves to ticket 3, then back to 2. The position text and dots follow. Previous is disabled on 1 and Next on 3. | system |
| Happy path   | With the purchase confirmation showing, open a ticket and go back | The confirmation is gone | system |
| Happy path   | Tickets → QR with a paused clock: advance 300 ms, then 150 ms more | Both screens are present at 300 ms, and only QR at 450 ms. Back mirrors it. | system |
| Happy path   | A single ticket (Market) | No dots or arrows, and "Show this code at the entrance." | system |
| Happy path   | Back (button), system back, browser back | Each returns to the Tickets tab with its list | system |
| Happy path   | Back from Buy (system and browser) | Returns to the event page | system |
| Happy path   | `RecreationTest`: Buy with count 3 and Google Wallet, recreate; pay, recreate on the Tickets tab; then the QR screen on ticket 2, recreate | Count 3 and Google Wallet kept. Still on the Tickets tab with the confirmation and the new tickets. Ticket 2 still shown. | system |
| Happy path   | Contrast of the QR code text on the surface, and of the card heads | Covered by existing tokens and `PosterContrastTest` | system |
| Happy path   | Screenshots on Android (light and dark) and Web: Home, Event, Buy (paid and free), Tickets (empty and filled), QR | Match design 1794071534-ba69 | system |

## Wireframe

```txt
┌──────────────────────────────────────┐
│ (1) [HOMEOUT]               🔔  (MA) │
├──────────────────────────────────────┤
│ (2) (←)  YOUR TICKET                 │
│          Fado… · Ticket 2 of 3       │
│ (3) ┌──────────────────────────────┐ │
│     │ FADO AT THE OLD CISTERN      │ │
│     │ FRI 13 NOV · 21:30 · ALFAMA  │ │
│     │        ┌────────────┐        │ │
│     │        │  ▓▒▓ QR ▒▓ │        │ │
│     │        └────────────┘        │ │
│     │         HO-FADO-002          │ │
│     ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┤ │
│     │ Ticket 2 of 3                │ │
│     │ General admission · MB WAY   │ │
│     └──────────────────────────────┘ │
│ (4)      (‹)   • ━ •   (›)           │
│     Swipe sideways for your other…   │
└──────────────────────────────────────┘
```

1. Header: fixed.
2. Back to the Tickets tab, the title, and the live position line.
3. One full-width card per ticket, swiped sideways with snapping. The QR code sits on white.
4. Dots (the active one wide) and arrows, only with several tickets, then the hint.

## Tasks to do

### `1)` QR screen

> Each ticket's code, swipeable.

1. `QrPattern`: port the design's `qrPath` to Kotlin (FNV-1a seed, xorshift, finders, alignment square, timing rows), returning the dark modules. Fixtures come from Node.
2. `QrDestination(eventId, index)`, `QrViewModel` and `QrScreen`, using a `HorizontalPager` whose initial page is the index. The arrows call `animateScrollToPage`. Dots, position and hint as in the design.
3. Tickets rows call `RootViewModel.clearConfirmation()`, then navigate to `QrDestination`.

### `2)` Back paths, recreation and docs

> Every back path and state survives, and the memory is current.

1. Extend `BrowserBackTest`, `SystemBackTest` and `RecreationTest` as in the scope.
2. Update the memory files: features, orders, navigation graph, `HoGlyphs` and `onTertiary`, test tags.
3. Run the full gate (`coding-assertions.md`) and take the screenshots.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `QrPatternTest` and `QrScreenTest` pass, and the QR flow in `AppTest` passes |
| 2 | The back and recreation tests pass on the emulator and in headless Chrome, the full gate passes, the screenshots match the design, and the memory matches the code |
