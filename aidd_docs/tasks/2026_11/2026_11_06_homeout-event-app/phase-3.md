---
status: done
---

# Instruction: Sample data

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
data/src/
├── commonMain/kotlin/app/homeout/data/
│   └── SampleEventRepository.kt       ✅ implements EventRepository
└── commonTest/kotlin/app/homeout/data/
    └── SampleEventRepositoryTest.kt   ✅
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Build the repository with a fixed clock | Events dated relative to that day | system |
| Happy path   | Read `events()` | The design's four events: Fado at the Old Cistern (Music, Alfama, today 21:30, €18), Rooftop Vinyl Session (Music, Bairro Alto, today 22:00, Free), Night Market by the River (Food, Cais do Sodré, tomorrow 18:00, Free), Petiscos Tasting Night (Food, Campo de Ourique, tomorrow 20:00, €25) | system |
| Happy path   | Check each event | It has at least one artist, a category of music or food, and a unique id | system |
| Edge case    | Build with a different clock date | Events shift with it, so they stay upcoming | system |

## Tasks to do

### `1)` Sample repository

> Local mocked events that match the design, with artists.

1. Encode the four design events with their venue, time, price and category, and give each its artists.
2. Compute dates from the injected clock (today and tomorrow, Europe/Lisbon).

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `./gradlew :data:allTests` passes every scenario above |
