---
status: done
---

# Instruction: Domain

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
domain/src/
├── commonMain/kotlin/app/homeout/domain/
│   ├── Event.kt                    ✅ id, title, category, artists, venue, startsAt, price
│   ├── Category.kt                 ✅ MUSIC, FOOD
│   ├── Price.kt                    ✅ Free | Amount(cents, EUR)
│   ├── EventRepository.kt          ✅ interface: events()
│   ├── EventFilter.kt              ✅ category (null = All) + query
│   ├── GetUpcomingEvents.kt        ✅ use case
│   └── DayGroup.kt                 ✅ date, isToday, events
└── commonTest/kotlin/app/homeout/domain/
    └── GetUpcomingEventsTest.kt    ✅
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | A fake repository and a fixed clock at Fri 14 Nov 2026 19:00 Europe/Lisbon | Deterministic input | system |
| Happy path   | Call with no filter | All upcoming events, sorted by start time, grouped by day; the first group is today | system |
| Happy path   | Filter by `MUSIC`, then by `FOOD` | Only events in that category | system |
| Happy path   | Query "fado", then a venue name, then an artist name | The events whose title, venue or one of whose artists contains the query | system |
| Edge case    | Query in different case with surrounding spaces | Matches the same as the trimmed lowercase query | system |
| Edge case    | Category `FOOD` with a query matching only a music event | Empty result | system |
| Edge case    | Blank query | Treated as no query | system |
| Edge case    | An event that started before now | Excluded | system |
| Edge case    | No events match | Empty list, no error | system |

## Tasks to do

### `1)` Model events

> The domain types the spec names, free of UI and data code.

1. `Event`, `Category`, `Price`, `DayGroup` and `EventFilter` as immutable types.

### `2)` Upcoming events use case

> One entry point that returns filtered upcoming events grouped by day.

1. `GetUpcomingEvents(repository, clock, timeZone)(filter): List<DayGroup>`.
2. Keep only events starting at or after now.
3. Apply the category, then the query against title, venue and artists (case-insensitive, trimmed).
4. Sort by start time, group by local date, and flag the group for today.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | The domain module compiles with no UI or data dependency |
| 2 | Every scenario above passes in `./gradlew :domain:allTests` |
