---
objective: "Tapping an event on Home opens its event page as the design shows, on Android and Web, in light and dark. The ticket, Share and Save buttons do nothing. The page's Back button, Android system back and browser back all return to Home with its filter and search kept, and every behavior is covered by passing tests."
status: in-progress
---

# Plan: Event page

Source: `aidd_docs/tasks/2026_11/2026_11_07_event-page/spec.md`, and the design https://claude.ai/artifact/1krt444a78B46kwb7jYipL, page `Home Screen.dc.html` ("Home and event screens", version 1794062441-00bd, read 2026-11-07).

## Phases

| #   | Phase        | File                         | Depends on |
| --- | ------------ | ---------------------------- | ---------- |
| 1   | Event details in domain and data | [`phase-1.md`](./phase-1.md) | none |
| 2   | Design-system additions | [`phase-2.md`](./phase-2.md) | none |
| 3   | Event page screen | [`phase-3.md`](./phase-3.md) | 1, 2 |
| 4   | Navigation, back and app shell | [`phase-4.md`](./phase-4.md) | 3 |

## External sources

| URL   | What it settled   |
| ----- | ----------------- |
| https://claude.ai/artifact/1krt444a78B46kwb7jYipL | The Event page's markup and data:<br>- Floating buttons 44 px round, 28 px from the top and 32 px from each side. Back and Share use the glass fill (dark `rgba(40,52,50,0.72)`, light `rgba(224,235,233,0.8)`) with a divider border. Save uses the tonal fill.<br>- Poster 380 px tall, radius 8, padding 18, with `collage-top_right.png` at 190 px flush top-right. "homeout presents" in JetBrains Mono 11 px. Title in Big Shoulders 50 px, line height 0.92. A rule, then the date and area in mono 12 px.<br>- Day label: tertiary for tonight's events, accent text otherwise.<br>- Title 22/28 medium. Calendar and map-pin icons at 18 px in accent text.<br>- "About this event" label, then the description at 15/23.<br>- Bottom bar on the surface color: price 20/24 medium, a 12 px note, and a 52 px ticket button (accent border, accent tint, ticket icon).<br>- Per-event values: venue names, doors or end times, poster colors and descriptions for the four events.<br>- Transition: 360 ms `cubic-bezier(0.2, 0.8, 0.2, 1)`. Event slides from `translateX(100%)` to 0. Home moves to `translateX(-28%)` and fades to 0. Header and flowers stay fixed outside the swap. |
| https://kotlinlang.org/docs/multiplatform/compose-navigation.html | Navigation Compose: `NavHost` with `@Serializable` routes, and `enterTransition`/`exitTransition` on `NavHost`. Custom transitions turn off the default iOS swipe animation. |
| https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html | On Kotlin/Wasm, `navController.bindToBrowserNavigation()` (opt-in `ExperimentalBrowserHistoryApi`) ties the browser back and forward buttons to the back stack and puts the route in the URL fragment. |
| https://kotlinlang.org/docs/multiplatform/compose-navigation-3.html | Navigation 3 has no browser-history support in the base library yet (CMP-8924), so it can't meet the browser-back requirement. |
| https://repo1.maven.org/maven2/org/jetbrains/androidx/navigation/navigation-compose/2.10.0/navigation-compose-2.10.0.pom | `org.jetbrains.androidx.navigation:navigation-compose:2.10.0` is stable, published 2026-10-23 (more than two weeks ago). It is built against Compose Multiplatform 1.11.0 and pulls in `navigationevent-compose` 1.1.0 for system back. |
| https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-core/maven-metadata.xml | `kotlinx-serialization-core` 1.12.0 is the latest stable release. The serialization plugin 2.4.21 matches the project's Kotlin. |

## Impact

| Category                                           | Verdict and evidence | Handled in         |
| -------------------------------------------------- | -------------------- | ------------------ |
| runtime | New dependencies: `navigation-compose` 2.10.0, `kotlinx-serialization-core` 1.12.0 and the Kotlin serialization plugin. Navigation is built against CMP 1.11.0 while the project uses 1.12.2, so Gradle resolves Compose up to 1.12.2. That pairing is unverified until phase 4's build. One new font file (JetBrains Mono Regular, OFL, about 270 KB) and three Phosphor vector icons. Downloaded assets are unverified until fetched. | 2, 4 |
| regression | High for Home tests, low elsewhere:<br>- `HomeScreenTest.eventsBellAndOtherTabsHaveNoAction` asserts that event rows aren't clickable. It must flip to "rows open their event".<br>- `HomeScreenTest` and `AppTest` find "HOMEOUT" through Home. The header moves into the app shell, so those lookups move to `AppTest`.<br>- `SampleEventRepositoryTest` keeps its rows unchanged because `venue` stays the area.<br>- `GetUpcomingEventsTest` and `CountEventsThisWeekTest` build `Event`s, so the new fields need defaults or the tests need updating.<br>- No safety-net phase is needed: the six `HomeScreenTest` cases and `AppTest` already lock Home's behavior (observed) and run in every phase.<br>- `navigation-compose` 2.10.0 may raise `androidx.activity` above the pinned 1.10.1 that `MainActivity` uses (unverified). Phase 4 checks the resolved version and aligns the catalog.<br>- Whether Home's search text survives a `NavHost` pop through saveable state is unverified, and `AppTest` covers it. | 1, 4 |
| architecture | Today `App` builds the `HomeStateHolder` and shows `HomeRoute` directly. After this plan, `App` is a shell: background, flower border and header stay fixed, and a `NavHost` below them swaps Home and Event.<br>- Home loses its own header and flower border.<br>- A new `GetEvent` use case in `domain` returns one event and whether it's tonight.<br>- `Event` gains page details.<br>- The holder stays outside the `NavHost`, so Home's filter and search survive going back. | 1, 4 |
| documentation | Changes to `README.md`:<br>- the modules table: `composeApp` holds the Home and Event screens and navigation<br>- no new commands<br>The spec is unchanged. | 4 |

## Rules

| Rule   | Why it applies |
| ------ | -------------- |
| Clean architecture: domain independent of UI, data and platform (spec) | The page details and `GetEvent` live in `domain` with no Compose types. Poster colors are stored as plain ARGB numbers. |
| The design is the source of truth (spec) | Layout, copy, colors and transition timing come from the design's Event page. |
| Every required behavior has a passing automated test (spec) | Each phase's Test Scope maps the spec's done-when lines to tests. |
| No code, copy or UI beyond the spec and design (spec) | Share, Save and the ticket button get no-op handlers. Hover-only shades from the web design are left out. |

## Decisions

| Decision   | Why   |
| ---------- | ----- |
| Use Navigation Compose 2.10.0, not a hand-rolled screen switch or Navigation 3. | It's the only documented way to tie the browser back button to in-app screens on Web (`bindToBrowserNavigation`). It also handles Android system back and gives the push transition. Navigation 3 lacks browser history. |
| The header and flower border move out of Home into an app shell above the `NavHost`. | The design keeps them fixed while the screens slide beneath. The spec requires the header on the event page. |
| Poster colors are event data (`PosterColors` with ARGB `Long`s), not design-system tokens. | The design gives each event its own colors, as a backend would send them. Domain stays free of Compose. |
