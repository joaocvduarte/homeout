---
objective: "From an event page people buy or reserve 1–8 tickets with a simulated payment method, land on a Tickets tab that groups their tickets by event with a count badge, and open each ticket's swipeable QR code, all matching design 1794071534-ba69 on Android and Web, in both themes, with every behavior tested."
status: implemented
---

# Plan: Buying tickets, Tickets tab and ticket QR codes

Source: `aidd_docs/specs/tickets.md`, and the design https://claude.ai/artifact/1krt444a78B46kwb7jYipL (Home Screen page, version 1794071534-ba69, read 2026-11-07).

## Phases

| #   | Phase        | File                         | Depends on |
| --- | ------------ | ---------------------------- | ---------- |
| 1   | Orders and tickets in domain and data | [`phase-1.md`](./phase-1.md) | none |
| 2   | Design-system additions | [`phase-2.md`](./phase-2.md) | none |
| 3   | Tabs, Home rows and the Tickets tab | [`phase-3.md`](./phase-3.md) | 1, 2 |
| 4   | Buy tickets | [`phase-4.md`](./phase-4.md) | 3 |
| 5   | Ticket QR code, back paths and docs | [`phase-5.md`](./phase-5.md) | 4 |

## External sources

| URL   | What it settled   |
| ----- | ----------------- |
| https://claude.ai/artifact/1krt444a78B46kwb7jYipL | Design version 1794071534-ba69, Home Screen page. It settled:<br>- **Screens:** root (Home or Tickets tab plus the tab bar), Event, Buy and QR, with push depths root 0, event 1, buy 2, qr 1, at 360 ms `cubic-bezier(0.2,0.8,0.2,1)`. A tab pane enters with `ho-in` (opacity 0 → 1, translateY 10 px → 0, 320 ms).<br>- **Tab bar:** Home, Tickets and Profile as 24-viewBox stroke icons (1.8). The active tab has fill `accentTint` and `accentText`. The badge is `hot` with `onHot` text (light `#ffffff`, dark `#283432`).<br>- **Home rows:** the thumbnail is `posterBg` with a Phosphor icon in `posterFg` (music-notes, vinyl-record, fork-knife, wine).<br>- **Buy:** the counter's limits, the four payment methods and their notes, the free note, and the bar copy.<br>- **Tickets:** the card layout, codes `HO-<id4>-<nnn>`, the banner copy and the empty state.<br>- **QR:** the 25-module placeholder (FNV-1a and xorshift seeded by the code, three finders, an alignment square at 16–20, timing rows), slides, dots (8 px, the active one 20 px) and the hints. |
| https://developer.android.com/develop/ui/compose/layouts/pager | `HorizontalPager` and `rememberPagerState(initialPage)` in `compose.foundation` give swipe with snapping, and `animateScrollToPage` drives the arrows. Compose Multiplatform ships the same foundation API. |

## Impact

| Category                                           | Verdict and evidence | Handled in         |
| -------------------------------------------------- | -------------------- | ------------------ |
| runtime | No new libraries: pager, Canvas and ImageVector come from Compose foundation and ui, which are already on the classpath (observed in `composeApp/build.gradle.kts`). Four Phosphor icons are added as vectors. | 2 |
| regression | High for UI tests, observed:<br>- `HomeScreenTest.bottomTabsHaveNoAction` and `AppTest.headerStaysAndBottomNavHidesOnTheEventPage` assert on "Explore".<br>- `EventScreenTest.ticketShareAndSaveDoNothing` asserts that the ticket button does nothing. The spec reverses that.<br>- `EventRow`'s striped thumbnail and the `stripe` token are replaced.<br>- `HomeDestination` becomes the root tab destination, which affects `AppTest`, `BrowserBackTest`, `SystemBackTest` and `RecreationTest` through navigation.
- `FoundationsPreview.kt` uses `HoIcons.Ticket`, and `KoinGraphTest` builds `Event(...)` directly. Both change with the icon work.<br>No safety-net phase is needed: the existing 50+ composeApp tests run after every phase. Those that encode old behavior are rewritten in the phase that changes it. | 3, 4 |
| architecture | Today one `NavHost` holds Home and Event, and Koin has data, domain and presentation modules. After this plan:<br>- `RootDestination` holds the tabs. The selected tab and the purchase confirmation live in `RootViewModel`'s own `SavedStateHandle`, and Pay reaches that same instance through the root entry. `EventDestination(id)`, `BuyDestination(id)` and `QrDestination(eventId, index)` push over it.<br>- An in-memory `OrderRepository` singleton (data) sits behind domain use cases `PlaceOrder` and `GetTicketGroups`.<br>- One ViewModel per new screen. | 1, 3, 4, 5 |
| documentation | Changes:<br>- memory `project-brief.md`: features and terms (order, ticket, ticket code)<br>- `architecture.md`: orders, the root destination<br>- `navigation.md`: the new graph<br>- `design.md`: glyphs, `onTertiary`<br>- `testing.md`: new tags<br>- README: none | 5 |

## Rules

| Rule   | Why it applies |
| ------ | -------------- |
| Clean architecture: `domain` has no UI, data or DI code (`architecture.md`) | Orders, payment methods, tickets and their codes are domain types and use cases. The repository implementation is in `data`. |
| Koin, one module per layer. ViewModels survive recreation (`dependency-injection.md`) | Each new screen gets a Koin `viewModelOf` ViewModel. `RecreationTest` gains the new screens. |
| The design is the source of truth (spec) | Copy, sizes, icons and colors come from version 1794071534-ba69. |
| WCAG AA for every text color (spec) | The new badge color joins `HoContrastTest`. |
| Commit after each validated task (`vcs.md`) | As before. |

## Decisions

| Decision   | Why   |
| ---------- | ----- |
| Ticket codes use the app's event ids, so they read `HO-ROOF-001` and `HO-NIGH-001` rather than the design's `HO-VINY` and `HO-MARK`. | The spec defines the code from the event id. Renaming ids would churn routes, test tags and URLs for a placeholder that the backend will replace. |
| Each event carries an `EventIcon` (`MUSIC_NOTES`, `VINYL_RECORD`, `FORK_KNIFE`, `WINE`) as data, mapped to Phosphor vectors in the UI. | The design picks the icon per event, not per category, like the poster colors. Domain stays free of Compose. |
| The design's custom 24-viewBox stroke icons are `ImageVector`s built in `designsystem` (`HoGlyphs`). That covers the tabs, the new ticket icon, minus and plus, the chevrons, the check circle, the QR mini, and phone, card and wallet. | The active tab fills its icon with a theme color, which a static vector resource can't do. One home for the design's own glyphs. |
| Pay gets the root entry's `RootViewModel` (`koinViewModel(viewModelStoreOwner = navController.getBackStackEntry<RootDestination>())`), calls `showTickets(confirmation)`, then pops back to the root. `RootViewModel` keeps the tab and the confirmation in its own `SavedStateHandle`. | It's the same instance the root screen uses, so nothing is copied between handles, and it survives recreation. Writing to `NavBackStackEntry.savedStateHandle` wouldn't work: in navigation 2.10 that handle belongs to a separate internal ViewModel (`NavBackStackEntry.kt`, read in the Gradle cache), not to the Koin one. |
| The QR placeholder is ported from the design's JS into a pure Kotlin function, checked against Node-generated fixtures. | It matches the design's pattern for the same code, and the check is mechanical. |
| Remove `HoColors.stripe` and the striped thumbnail. | No code uses them after this change. The spec forbids leftovers. |
