---
status: pending
---

# Instruction: Website content: next events, copy, colors, flower and arrow assets

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
.
├── designsystem/src/
│   ├── commonMain/kotlin/app/homeout/designsystem/
│   │   ├── HoFlowerBorder.kt ✏️  (HoFlower(part) exposes each logo flower)
│   │   └── HoGlyphs.kt ✏️  (ArrowDown)
│   └── commonTest/kotlin/app/homeout/designsystem/
│       ├── HoFlowerBorderTest.kt ✏️
│       └── HoGlyphsTest.kt ✏️
└── composeApp/src/
    ├── commonMain/kotlin/app/homeout/
    │   ├── di/PresentationModule.kt ✏️  (WebsiteViewModel)
    │   └── website/
    │       ├── WebsiteColors.kt ✅
    │       ├── WebsiteFormat.kt ✅
    │       └── WebsiteViewModel.kt ✅
    └── commonTest/kotlin/app/homeout/
        ├── di/KoinGraphTest.kt ✏️
        └── website/
            ├── WebsiteColorsTest.kt ✅
            ├── WebsiteFormatTest.kt ✅
            └── WebsiteViewModelTest.kt ✅
```

## Test Scope

| Scenario | Action | Observable outcome | Actor |
| -------- | ------ | ------------------ | ----- |
| Setup | Fixed clock Friday 2026-11-13 19:00 UTC, `Europe/Lisbon`, `SampleEventRepository` | Four sample events: two tonight, two tomorrow | system |
| Happy path | `WebsiteViewModel` loads | `state.events` holds up to 4 upcoming events in time order, and `state.total` the upcoming count | system |
| Happy path | Format a paid event tonight | Kicker "Tonight", date "Fri 13 Nov · 21:30", line "Music · 21:30", price "€18 per person", label "Get tickets for Fado at the Old Cistern" | system |
| Edge case | Format a free event tomorrow | Kicker "Saturday", price "Free with a ticket" | system |
| Edge case | No upcoming events (empty repository) | `events` empty, `total` 0 | system |
| Edge case | Every text/background pair of `WebsiteColors` | Each ≥ 4.5:1, or ≥ 3:1 for large text (wordmark, headlines) | system |
| Happy path | `HoFlower(Left/Mid/Right)` rendered | Each image shows with no semantics | system |

## Tasks to do

### `1)` Next events state

> Web gets the app's upcoming events and their count from Koin.

1. `WebsiteViewModel(getUpcomingEvents)`: plain `MutableStateFlow<WebsiteState>`, loaded once in `init`; `events` = flattened day groups, first 4; `total` = all upcoming.
2. Register it in `presentationModule`; resolve it in `KoinGraphTest`.

### `2)` Website copy

> Each card shows the design's labels from event data.

1. `WebsiteFormat.kt`: `kicker` ("Tonight" if today in the time zone, else full weekday), poster date (reuse `Event.posterDate`), `categoryAndTime` ("Music · 21:30"), `websitePrice` ("€18 per person" / "Free with a ticket", reusing `Price.label`), `ticketsLabel`, `seeAllLabel(total)` ("See all 4 events", "See all 1 event").

### `3)` Fixed palette

> Web always uses the design's single look.

1. `WebsiteColors` object with the design's hexes: page `#283432`, ink `#E7ECEB`, muted `#B3BDBB`, brand `#64837E`, panel `#70A89F`, light section `#E0EBE9`, dark ink `#0F1817`, light muted `#4A5654`, kicker `#8E4754`, pill `#C9F2EB`/`#284D48`, footer `#1F2927`, step colors.
2. `WebsiteColorsTest`: every pair the page uses meets AA (same ratio formula as `PosterContrastTest`).

### `4)` Design assets

> The website reaches the logo flowers and the scroll arrow.

1. `HoFlower(part: HoFlowerPart, modifier)` in `HoFlowerBorder.kt`, decorative, no semantics; `HoFlowerBorder` and `HoPosterFlower` unchanged.
2. `HoGlyphs.ArrowDown` (`M12 5v14`, `M6 13l6 6 6-6`, regular stroke); add it to `HoGlyphsTest`'s single-color set.

## Test acceptance criteria

| Task | Acceptance criteria |
| ---- | ------------------- |
| 1 | On the Friday clock, `state.events` ids are `fado-old-cistern`, `rooftop-vinyl`, `night-market-river`, `petiscos-tasting` and `total` is 4; an empty repository gives no events and 0; `KoinGraphTest` resolves `WebsiteViewModel` |
| 2 | `WebsiteFormatTest` pins each label above for paid, free, tonight and tomorrow, and singular/plural "See all" |
| 3 | `WebsiteColorsTest` passes for every pair; a pair below its threshold fails the test |
| 4 | `HoFlowerBorderTest` finds each `HoFlower` part with no content description; `HoGlyphsTest` covers `ArrowDown` on the 24 grid; existing designsystem tests stay green |
