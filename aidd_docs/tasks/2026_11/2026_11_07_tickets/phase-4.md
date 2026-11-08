---
status: done
---

# Instruction: Buy tickets

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
composeApp/src/commonMain/kotlin/app/homeout/
├── Destinations.kt              ✏️ BuyDestination(id)
├── App.kt                       ✏️ event → buy; pay → root with tab + confirmation
├── buy/BuyScreen.kt             ✅ summary, counter, methods or free note, total bar
├── buy/BuyViewModel.kt          ✅ quantity 1..8, method, total; pay() → Confirmation
├── buy/BuyFormat.kt             ✅ "€18 each", "Pay €36", "Reserve 2 tickets", method names and notes
├── event/EventScreen.kt         ✏️ ticket button calls onBuy; the design's ticket glyph
├── event/EventRoute.kt          ✏️ onBuy
└── di/PresentationModule.kt     ✏️ BuyViewModel
composeApp/src/commonTest/kotlin/app/homeout/
├── buy/BuyViewModelTest.kt      ✅
├── buy/BuyScreenTest.kt         ✅
├── buy/BuyFormatTest.kt         ✅
├── event/EventScreenTest.kt     ✏️ the ticket button opens Buy; Share and Save still do nothing
└── AppTest.kt                   ✏️ the full purchase flow
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | `withKoin(friday)`, `App()`, open Fado | Event page | system |
| Happy path   | Tap Buy tickets | Buy slides in: "BUY TICKETS", Fado summary ("Fri 13 Nov · 21:30", "Cisterna do Largo, Alfama"), "General admission", "€18 each", quantity 1, MB WAY selected, "1 ticket · Total", "€18", "Pay €18" | system |
| Happy path   | Tap plus, choose Google Wallet, Pay €36 | The Tickets tab is selected with "Payment confirmed. 2 tickets for Fado at the Old Cistern added." and the Fado card with 2 tickets paid with Google Wallet. Badge "2". Back from here doesn't return to Buy. | system |
| Happy path   | Free event (Rooftop Vinyl) → Get free tickets | "Free entry", the free note, no payment methods, "Free", "Reserve 1 ticket". Reserving shows "Reserved. 1 ticket for Rooftop Vinyl Session added." | system |
| Edge case    | Minus at 1, or plus at 8 | That button is disabled, and the count stays between 1 and 8 | system |
| Edge case    | Buy 1 more Fado | The same Fado card now shows 3 tickets, and the badge reads 3 | system |
| Happy path   | Back on Buy (button) | Returns to the event page with the reverse push | system |
| Happy path   | Event → Buy with a paused clock: advance 300 ms, then 150 ms more | At 300 ms both the event page and Buy are present, and at 450 ms only Buy is. Back mirrors it. | system |
| Happy path   | Payment options semantics | Each is a radio button and exactly one is selected | system |
| Happy path   | `BuyViewModelTest` | `inc`/`dec` clamp, the total is the price × the count, and `pay()` places the order with the chosen method, or none when free | system |

## Wireframe

```txt
┌──────────────────────────────────────┐
│ (1) [HOMEOUT]               🔔  (MA) │
├──────────────────────────────────────┤
│ (2) (←)  BUY TICKETS                 │
│ (3) ┌──┐ Fado at the Old Cistern     │
│     └──┘ Fri 13 Nov · 21:30 · venue  │
│ (4) TICKETS                          │
│     General admission    (−)  2  (+) │
│     €18 each                         │
│     Up to 8 tickets per order.       │
│ (5) PAYMENT METHOD                   │
│     [📱 MB WAY  ……………………………… (•)]   │
│     [💳 Credit or debit card …  ( )] │
│     [👛 Apple Pay …………………………  ( )] │
│     [👛 Google Wallet ……………… ( )]   │
├──────────────────────────────────────┤
│ (6) 2 tickets · Total   [ Pay €36 ]  │
│     €36                              │
└──────────────────────────────────────┘
```

1. Header: fixed, as on every screen.
2. Back to the event (glass circle) and the stencil title.
3. Event summary on the surface fill, with a poster-colored square and the flower.
4. Quantity counter: minus on an outline, plus on the tonal fill. It's disabled at the limits.
5. Payment methods as radio cards: the chosen one gets the accent ring and tint. For a free event, the free note shows instead.
6. Fixed bar: the count and total, and the tinted primary button.

## Tasks to do

### `1)` Buy screen and ViewModel

> Choose a count and a method.

1. `BuyDestination(id)`, and `BuyViewModel(savedStateHandle, getEvent, placeOrder)`. It holds the event, `quantity` (1..8, saved), `method` (default MB WAY, saved), the total, `inc`, `dec`, `select` and `pay()`. `pay()` places the order and returns the confirmation text.
2. `BuyScreen` and `BuyFormat`, built to the wireframe and the design's copy.
3. The event page's ticket button calls `onBuy`. Its icon becomes `HoGlyphs.Ticket`.

### `2)` Pay or reserve lands on Tickets

> The order shows on the Tickets tab.

1. In `App`, `onPaid(confirmation)` gets the root entry's `RootViewModel` with `koinViewModel(viewModelStoreOwner = navController.getBackStackEntry<RootDestination>())`, calls `showTickets(confirmation)`, and pops back to `RootDestination`.
2. Update `EventScreenTest`, and add the flows to `AppTest`.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `BuyViewModelTest`, `BuyFormatTest` and `BuyScreenTest` pass on wasmJs and Android |
| 2 | The paid and free purchase flows in `AppTest` pass, and the Buy screenshots match the design in both themes |
