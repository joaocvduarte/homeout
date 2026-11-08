---
status: done
---

# Instruction: Flower border on Home

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
designsystem/src/
├── commonMain/composeResources/drawable/
│   ├── flowers_top_left.png     ✅ design asset collage-top_left.png
│   ├── flowers_top_mid.png      ✅ design asset collage-top_mid.png
│   └── flowers_top_right.png    ✅ design asset collage-top_right.png
├── commonMain/kotlin/app/homeout/designsystem/HoFlowerBorder.kt   ✅ the three images, top-aligned, theme alpha
└── commonTest/kotlin/app/homeout/designsystem/HoFlowerBorderTest.kt  ✅
composeApp/src/commonMain/kotlin/app/homeout/home/HomeScreen.kt     ✏️ border behind the content
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Download the three PNGs from design v10 (`project/assets/`) | Byte-identical files in `composeResources/drawable` | cli |
| Happy path   | Render `HoFlowerBorder` in a 372dp-wide box | Three images at the top: left piece at x 0 (124dp wide), middle centered (176dp), right piece flush right (150dp) | system |
| Happy path   | Read the border's semantics | Purely decorative: no content description and no click action | system |
| Happy path   | Run the existing `HomeScreenTest`, `AppTest` and state tests on wasmJs and the emulator | All pass unchanged; the border doesn't take taps from the search, filter or rows | system |
| Happy path   | Screenshot Android (night mode on and off) and Web (dark and light) | Teal background and the top flower border match design v10 Home A; the status-bar icons stay readable | system |
| Edge case    | Scroll Home when the list is longer than the screen | Content scrolls over the fixed border | system |

## Wireframe

```txt
┌──────────────────────────────────────┐
│ (1)✿✿✿✿ flowers ✿✿✿ flowers ✿✿✿✿✿✿✿ │
│ (2) [HOMEOUT]            🔔   (MA)   │
├──────────────────────────────────────┤
│ (3) UPCOMING NEAR YOU                │
│     Lisbon · N events this week      │
│ (4) [🔍 Search events, venues, artists]│
│ (5) [  All  |  Music  |  Food  ]     │
│ (6) TONIGHT · FRI 6 NOV              │
│   ┌──┐ Rooftop Vinyl Session  [Free] │
│   └──┘ Music · Bairro Alto · 22:00   │
├──────────────────────────────────────┤
│ (7) Home   Explore   Tickets  Profile│
└──────────────────────────────────────┘
```

1. Flower border: three real flower pieces flush with the top edge, behind the status bar and header. It doesn't scroll.
2. Header: the wordmark (original teal block), bell and avatar over the flowers.
3. Heading and subtitle on the teal background.
4. Search field on the surface tint.
5. Category filter.
6. Day groups and event rows.
7. Bottom bar on the surface tint.

## Tasks to do

### `1)` Flower border component

> The design's top border as a reusable piece.

1. Copy the three design PNGs into `designsystem` drawable resources.
2. `HoFlowerBorder(modifier)`: a full-width `Box` with left, centered and right `Image`s aligned to the top, at 124, 176 and 150 dp wide, `contentDescription = null`, alpha `HoTheme.colors.decorationAlpha`.
3. Test: the three images are placed and sized as in the design, and the border has no semantics.

### `2)` Place it on Home

> Home shows the border behind its content.

1. In `HomeScreen`, wrap the screen in a `Box` with the background color: `HoFlowerBorder` first, then the existing content `Column` on top. The border stays outside the scroll.

### `3)` Verify against the design

> Same checks as before.

1. Run `./gradlew build` and the connected device tests.
2. Screenshot Android (night mode on and off) and Web (dark and light) and compare with design v10 Home A.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `HoFlowerBorderTest` passes on wasmJs and Android |
| 2 | All Home tests pass unchanged, and the border sits behind the header |
| 3 | The screenshots match design v10 Home A in both themes, with readable status-bar icons |
