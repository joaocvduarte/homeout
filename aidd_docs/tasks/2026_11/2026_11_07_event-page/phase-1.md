---
status: in-progress
---

# Instruction: Event details in domain and data

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
domain/src/
├── commonMain/kotlin/app/homeout/domain/
│   ├── Event.kt              ✏️ venueName, doorsAt, endsAt, description, poster
│   ├── PosterColors.kt       ✅ background and content ARGB
│   └── GetEvent.kt           ✅ one event by id, with isToday
└── commonTest/kotlin/app/homeout/domain/
    ├── GetEventTest.kt                 ✅
    ├── GetUpcomingEventsTest.kt        ✏️ only if the new fields break its builders
    └── CountEventsThisWeekTest.kt      ✏️ only if the new fields break its builders
data/src/
├── commonMain/kotlin/app/homeout/data/SampleEventRepository.kt        ✏️ the design's page details
└── commonTest/kotlin/app/homeout/data/SampleEventRepositoryTest.kt    ✏️ details test
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Fixed clock at 2026-11-13T19:00Z (a Friday), UTC | Four sample events today and tomorrow | system |
| Happy path   | `GetEvent("fado-old-cistern")` | The Fado event, `isToday = true` | system |
| Happy path   | `GetEvent("petiscos-tasting")` | The Petiscos event, `isToday = false` | system |
| Happy path   | Read the sample events' details | Each one matches the design:<br>- venue names: Cisterna do Largo, Terraço Calçada, Praça do Cais, Tasca da Esquina<br>- doors 21:00 for Fado, and no end time<br>- end times: 02:00 the next day for Vinyl, 23:30 for the Market, 22:30 for Petiscos<br>- the poster colors<br>- the descriptions, word for word | system |
| Edge case    | `GetEvent("unknown")` | `null` | system |
| Edge case    | `GetEvent` for an event that has already started | Still returned. The page isn't limited to upcoming events. | system |
| Happy path   | Run the existing domain and data tests | All pass. `venue` is still the area. | system |

## Tasks to do

### `1)` Page details on Event

> An event carries everything its page shows.

1. Add `PosterColors(background: Long, content: Long)` (ARGB).
2. Add to `Event`: `venueName: String`, `doorsAt: Instant?`, `endsAt: Instant?`, `description: String` and `poster: PosterColors`. Keep `venue` as the area.

### `2)` GetEvent use case

> Look up one event by id.

1. `GetEvent(repository, clock, timeZone)`. `invoke(id)` returns `EventDetail(event, isToday)?`, where `isToday` compares the event's local date with today.
2. Test the happy path, the unknown id and an already-started event.

### `3)` Sample data from the design

> The four events get the design's page details.

1. Fill the new fields in `SampleEventRepository` with the design's values:
   - Fado: Cisterna do Largo, doors 21:00, `#8E4754`/`#FBE9EC`
   - Vinyl: Terraço Calçada, ends 02:00 the next day, `#335C57`/`#C9F2EB`
   - Market: Praça do Cais, ends 23:30, `#F69DAB`/`#283432`
   - Petiscos: Tasca da Esquina, ends 22:30, `#C9F2EB`/`#284D48`
   - each event gets the design's description, copied exactly
2. Add a test that pins these values.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | Domain compiles with no Compose dependency, and the existing domain tests pass |
| 2 | `GetEventTest` passes on JVM and Wasm |
| 3 | `SampleEventRepositoryTest` passes, including the new details test |
