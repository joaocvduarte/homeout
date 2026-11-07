---
status: done
---

# Instruction: Tabs, Home rows and the Tickets tab

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
composeApp/src/commonMain/kotlin/app/homeout/
├── Destinations.kt              ✏️ RootDestination replaces HomeDestination
├── App.kt                       ✏️ root route; back targets RootDestination
├── root/RootScreen.kt           ✅ the tab pane (fade-up on switch) and the tab bar
├── root/RootViewModel.kt        ✅ tab, confirmation (SavedStateHandle), ticket count
├── root/TabBar.kt               ✅ Home, Tickets, Profile; badge; glyph fill when active
├── home/HomeBottomBar.kt        ❌ replaced by TabBar
├── home/HomeScreen.kt           ✏️ content only; the bar moves to RootScreen
├── home/EventRow.kt             ✏️ poster-colored thumbnail with the event icon
├── tickets/TicketsScreen.kt     ✅ title, summary, banner, cards, empty state
├── tickets/TicketsViewModel.kt  ✅ GetTicketGroups → state
├── tickets/TicketsFormat.kt     ✅ summary, count and detail copy
└── di/PresentationModule.kt     ✏️ RootViewModel, TicketsViewModel
composeApp/src/commonTest/kotlin/app/homeout/
├── root/RootScreenTest.kt       ✅
├── tickets/TicketsScreenTest.kt ✅
├── tickets/TicketsFormatTest.kt ✅
├── home/HomeScreenTest.kt       ✏️ tab assertions move to RootScreenTest
└── AppTest.kt                   ✏️ Explore gone; root destination
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | `withKoin(friday)`, `App()` | Home tab selected, with Home, Tickets and Profile in the bar and no badge | system |
| Happy path   | Home rows | Each thumbnail shows its event's poster color and icon (test tag `thumb-<id>` with its icon content description hidden) | system |
| Happy path   | Tap Tickets | The Tickets tab is selected and shows "YOUR TICKETS", "Nothing booked yet", "No tickets yet" and Find events | system |
| Happy path   | Tap Find events | Back on the Home tab | system |
| Happy path   | Place 2 Fado (MB WAY) and 1 Market through Koin, then open Tickets | Badge "3". Summary "3 tickets · 2 events". Fado card "2 tickets" with "Ticket 1 of 2", "General admission · Paid with MB WAY" and `HO-FADO-001`. Market card with "General admission · Free". | system |
| Happy path   | Root confirmation set (`Payment confirmed. 2 tickets for Fado at the Old Cistern added.`) | The banner shows. Switching to Home and back hides it. | system |
| Happy path   | Tap Profile | Nothing changes | system |
| Happy path   | Tab switch timing with a paused clock | The pane fades up over 320 ms | system |
| Happy path   | Existing Home, event and back tests | Pass. "Explore" assertions removed. | system |

## Wireframe

```txt
┌──────────────────────────────────────┐
│ (1) [HOMEOUT]               🔔  (MA) │
├──────────────────────────────────────┤
│ (2) YOUR TICKETS                     │
│     3 tickets · 2 events             │
│ (3) ✓ Payment confirmed. 2 tickets…  │
│ (4) ┌──────────────────────────────┐ │
│     │ FADO AT THE OLD…  [2 tickets]│ │
│     │ FRI 13 NOV · 21:30 · ALFAMA  │ │
│     ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┤ │
│     │ ▣ Ticket 1 of 2   HO-FADO-001 ›│
│     │   General admission · MB WAY │ │
│     └──────────────────────────────┘ │
├──────────────────────────────────────┤
│ (5)  ⌂ Home     🎫 Tickets(3)   👤 Profile │
└──────────────────────────────────────┘
```

1. Header: unchanged and fixed.
2. Title and summary.
3. Confirmation banner after a purchase, on the tonal fill.
4. One card per event: a poster-colored head, then dashed-separated ticket rows. Each row opens the QR code (phase 5).
5. Tab bar: three tabs. The active one is tinted, and Tickets carries a badge in the highlight color.

## Tasks to do

### `1)` Root with tabs

> Home and Tickets are tabs of one root destination.

1. `RootDestination`, and `RootViewModel(savedStateHandle, getTicketGroups)`. It exposes:
   - the tab (Home or Tickets) and the confirmation, both kept in its own `SavedStateHandle`
   - the ticket count
   - `select(tab)`, which clears the confirmation
   - `showTickets(confirmation)`
   - `clearConfirmation()`
2. `RootScreen`: Home or Tickets content with the `ho-in` enter (fade and 10 dp rise, 320 ms), plus `TabBar`. Profile has no action.
3. `EventRow` thumbnail: the poster color with the event's icon in `posterFg`. Delete the striped placeholder.
4. Update `AppTest`, `HomeScreenTest` and the navigation targets.

### `2)` Tickets tab

> Bought tickets grouped by event.

1. `TicketsViewModel(getTicketGroups)` and `TicketsScreen`: title, summary, banner, cards and empty state. Each row passes `onTicketClick(eventId, index)` up, wired in phase 5.
2. `TicketsFormat`: "<n> ticket(s)", "<n> tickets · <m> events", and "General admission · Paid with <method>" or "· Free".
3. Tests as in the scope.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `RootScreenTest` and the updated `AppTest` and `HomeScreenTest` pass on wasmJs and Android |
| 2 | `TicketsScreenTest` and `TicketsFormatTest` pass, and the screenshots match the design's Tickets page, empty and filled |
