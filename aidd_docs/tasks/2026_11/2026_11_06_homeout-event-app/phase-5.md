---
status: done
---

# Instruction: Home screen

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
composeApp/src/
├── commonMain/kotlin/app/homeout/
│   ├── App.kt                                  ✅ replaces the phase 1 stub: HoTheme { HomeScreen(...) } with manual wiring
│   └── home/
│       ├── HomeState.kt                        ✅ query, category, groups, eventsThisWeek
│       ├── HomeStateHolder.kt                  ✅ StateFlow over GetUpcomingEvents
│       ├── HomeScreen.kt                       ✅ layout per wireframe
│       ├── CategoryFilter.kt                   ✅ All / Music / Food segmented control
│       ├── EventRow.kt                         ✅ thumbnail, title, category · venue · time, price tag
│       └── HomeBottomBar.kt                    ✅ Home, Explore, Tickets, Profile
├── commonTest/kotlin/app/homeout/home/
│   ├── HomeStateHolderTest.kt                  ✅
│   └── HomeScreenTest.kt                       ✅
└── androidDeviceTest/                          ✅ enabled by withDeviceTest { } in phase 1; runs the common UI tests on the emulator
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | `HomeScreen` wired with `SampleEventRepository` and a fixed clock | Four sample events available | system |
| Happy path   | Open Home | "Tonight · Fri 14 Nov" with the two music events, then "Sat 15 Nov" with the two food events; each row shows its category | browser |
| Happy path   | Tap Music, then Food, then All | The list shows only music, then only food, then all events; the selected segment is highlighted | browser |
| Happy path   | Type an artist's name | Only that artist's event is shown | browser |
| Edge case    | With Food selected, type "fado" | No events are shown | browser |
| Edge case    | Clear the search | The list returns to the selected category's events | browser |
| Happy path   | Open Home | The heading reads "UPCOMING NEAR YOU" and the day headers are in uppercase | browser |
| Edge case    | Switch the system theme | The screen recolors to the other token set | system |
| Edge case    | Tap an event, the bell, Explore, Tickets or Profile | Nothing happens (these screens come later) | browser |
| Teardown     | Close the test host | Baseline restored | system |

## Wireframe

```txt
┌──────────────────────────────────────┐
│ (1) [HOMEOUT]            🔔   (MA)   │
├──────────────────────────────────────┤
│ (2) UPCOMING NEAR YOU                │
│     Lisbon · N events this week      │
│ (3) [🔍 Search events, venues, artists]│
│ (4) [  All  |  Music  |  Food  ]     │
│ (5) TONIGHT · FRI 14 NOV             │
│   ┌──┐ Fado at the Old Cistern  [€18]│
│   └──┘ Music · Alfama · 21:30        │
│   (6) …                              │
│     SAT 15 NOV                       │
│   ┌──┐ Night Market by the River     │
│   └──┘ Food · Cais do Sodré · 18:00  │
├──────────────────────────────────────┤
│ (7) Home   Explore   Tickets  Profile│
└──────────────────────────────────────┘
```

1. Header: wordmark on the brand-teal block, a notifications icon, an initials avatar.
2. Heading: Headline in uppercase stencil, with the city and this week's event count from the data.
3. Search: a text input on the surface color, with the search icon and the design's placeholder.
4. Category filter: three segments; the selected one has the accent ring and accent text.
5. Day header: Label Small in uppercase; tertiary (hot) for today, muted for later days.
6. Event row: a 56dp striped thumbnail as in the design, title, category · venue · time, and a tonal price tag.
7. Bottom bar: four tabs on the surface color; Home is active in accent.

## Tasks to do

### `1)` Home state

> One state holder that turns the category and query into grouped events.

1. `HomeStateHolder` exposes `StateFlow<HomeState>` and `onQueryChange` and `onCategorySelect`, which call `GetUpcomingEvents`.
2. Count this week's events for the subtitle.

### `2)` Home layout

> The screen matches design variant A.

1. Build regions 1–7 with `HoTheme` tokens and type roles only, with no hard-coded colors.
2. Label each day "Tonight · EEE d MMM" for today and "EEE d MMM" otherwise.
3. Leave event, bell and the three other tabs without actions.
4. When nothing matches, show the empty list only. A no-results message waits until the design shows one.

### `3)` Wire and verify on devices

> The real app shows Home on every buildable target.

1. Wire `SampleEventRepository`, the system clock and Europe/Lisbon into `App.kt`.
2. Run the common UI tests on wasmJs and with `connectedAndroidTest` on `Pixel_8_Pro`.
3. Compare Android and Chrome, in light and dark, side by side with the design's Home A.
4. iOS: compiles only on macOS. Record it as unverified in the README.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `HomeStateHolderTest` passes for category, query, both combined and blank query |
| 2 | `HomeScreenTest` passes every scenario above on wasmJs and Android |
| 3 | The app on the Android emulator and in Chrome matches design Home A in both themes; filter and search behave as the spec says |
