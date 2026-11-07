---
status: done
---

# Instruction: Event page screen

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
composeApp/src/
├── commonMain/kotlin/app/homeout/
│   ├── home/HomeFormat.kt                 ✏️ shared day and time labels made reusable
│   └── event/
│       ├── EventScreen.kt                 ✅ poster, details, about and the bottom bar
│       ├── EventFormat.kt                 ✅ poster date, the "when" line, full venue, CTA copy
│       └── FloatingButtons.kt             ✅ Back, Share, Save over the page
└── commonTest/kotlin/app/homeout/event/
    ├── EventScreenTest.kt                 ✅
    ├── EventFormatTest.kt                 ✅
    └── PosterContrastTest.kt              ✅
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Render `EventScreen` for a sample event, clock fixed at 2026-11-13T19:00Z (a Friday), UTC, dark and light | The page is shown | system |
| Happy path   | Fado (paid, tonight) | It shows:<br>- "HOMEOUT PRESENTS" and the title<br>- "MUSIC · TONIGHT · FRI 13 NOV"<br>- "Fri 13 Nov · Doors 21:00 · Starts 21:30"<br>- "Cisterna do Largo, Alfama"<br>- "ABOUT THIS EVENT" and the description<br>- "€18", "per person" and "Buy tickets" | system |
| Happy path   | Rooftop Vinyl (free) | "Free", "Ticket required", "Get free tickets", and "Fri 13 Nov · 22:00 – 02:00" | system |
| Happy path   | Petiscos (tomorrow) | The day label "FOOD · SAT 14 NOV" is in the accent-text color, not tertiary | system |
| Happy path   | Tap the ticket button, then Share, then Save | No callback fires and nothing on screen changes. Each one still reads as a button with a label. | system |
| Happy path   | Tap Back | `onBack` fires once | system |
| Happy path   | Compute poster contrast for all four events | Each content color is at least 4.5:1 on its poster background | system |
| Happy path   | Run `HoContrastTest` | It still passes. The page's other text uses only tokens it already checks in both themes: onSurface and onSurfaceVariant on background and surface, onPrimaryText (the ticket label, calendar and pin, and non-tonight days) and tertiary (tonight). | system |
| Edge case    | An event with neither doors nor an end time | The "when" line is "<day> · <start>" | system |
| Edge case    | A long description on a small screen | The content scrolls, and the bottom bar stays fixed and visible | system |

## Wireframe

```txt
┌──────────────────────────────────────┐
│ (1) (←)                     (⇪) (🔖) │
│ ┌──────────────────────────────────┐ │
│ │ (2)                      ✿flower │ │
│ │ HOMEOUT PRESENTS                 │ │
│ │ FADO AT THE                      │ │
│ │ OLD CISTERN                      │ │
│ │ ──────────────────────────────── │ │
│ │ FRI 13 NOV · 21:30      ALFAMA   │ │
│ └──────────────────────────────────┘ │
│ (3) MUSIC · TONIGHT · FRI 13 NOV     │
│     Fado at the Old Cistern          │
│ (4) 📅 Fri 13 Nov · Doors 21:00 · …  │
│     📍 Cisterna do Largo, Alfama     │
│ ──────────────────────────────────── │
│ (5) ABOUT THIS EVENT                 │
│     An intimate night of fado …      │
├──────────────────────────────────────┤
│ (6) €18          [ 🎫 Buy tickets ]  │
│     per person                       │
└──────────────────────────────────────┘
```

1. Floating buttons: Back and Share in glass circles, and Save in a tonal circle, over the top of the poster. They stay put while the page scrolls.
2. Poster: the event's colors, a flower in the top-right corner, a mono label, the stencil title, and a rule with the date and area.
3. Category and day label, then the title.
4. When and where, with calendar and pin icons.
5. About: label and description.
6. Bottom bar on the surface color: price, price note and the ticket button. It doesn't scroll.

## Tasks to do

### `1)` Formatting

> The page's text from an `EventDetail`.

1. Make the existing day and time label helpers in `HomeFormat.kt` reusable, without changing Home's output.
2. `EventFormat.kt` builds:
   - the poster date ("Fri 13 Nov · 21:30")
   - the "when" line (doors form, end-time form, or the plain start time)
   - the full venue ("<venueName>, <area>")
   - the category and day label
   - the price note and CTA copy, which depend on whether the event is free
3. Test each form.

### `2)` EventScreen

> The design's Event page as one composable.

1. `EventScreen(detail, timeZone, onBack)`: a scrolling column (poster, labels, when and where, divider, about) above a fixed bottom bar.
2. Poster: 380 dp tall, colors from `PosterColors`, and `HoPosterFlower` (phase 2) at 190 dp in the top-right corner, decorative. Text uses the mono and stencil families at the design's sizes.
3. Bottom bar: `HoButton` with the `tinted` option from phase 2 (Primary, Large, block, ticket icon). `onClick = {}`.
4. `FloatingButtons`: 44 dp circles 12 dp inside the poster's top corners (28 and 32 px in the design). Back calls `onBack`. Share and Save have labelled no-op clicks.
5. Test the content, the free and paid copy, the day color, the no-op buttons and Back.

### `3)` Poster contrast

> The text on every poster is readable.

1. `PosterContrastTest`: for each sample event, check the poster content color against its background at 4.5:1 or more, using the WCAG 2.1 formula from `HoContrastTest`.
2. Use only tokens that `HoContrastTest` already checks for every other piece of text on the page, so both themes stay covered.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `EventFormatTest` passes, and the Home tests pass unchanged |
| 2 | `EventScreenTest` passes on wasmJs and Android, in both themes |
| 3 | `PosterContrastTest` passes for all four events |
