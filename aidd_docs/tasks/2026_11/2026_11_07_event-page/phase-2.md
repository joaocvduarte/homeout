---
status: done
---

# Instruction: Design-system additions

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
designsystem/
├── licenses/JetBrainsMono-OFL.txt                                     ✅
└── src/
    ├── commonMain/composeResources/
    │   ├── font/jetbrains_mono_regular.ttf                            ✅
    │   └── drawable/
    │       ├── ph_arrow_left.xml                                      ✅
    │       ├── ph_calendar_blank.xml                                  ✅
    │       └── ph_map_pin.xml                                         ✅
    ├── commonMain/kotlin/app/homeout/designsystem/
    │   ├── HoColors.kt          ✏️ glass token
    │   ├── HoIcons.kt           ✏️ ArrowLeft, CalendarBlank, MapPin
    │   ├── HoTypography.kt      ✏️ mono family for poster labels
    │   ├── HoButton.kt          ✏️ tinted option: Primary with the primary tint at rest
    │   └── HoFlowerBorder.kt    ✏️ HoPosterFlower: the top-right flower alone, for posters
    └── commonTest/kotlin/app/homeout/designsystem/
        ├── HoColorsTest.kt      ✏️ glass values
        ├── HoTypographyTest.kt  ✏️ mono family loads
        ├── HoButtonTest.kt      ✏️ tinted button renders and clicks
        └── HoFlowerBorderTest.kt ✏️ HoPosterFlower is decorative
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Download:<br>- JetBrains Mono Regular TTF and its OFL from the JetBrains/JetBrainsMono GitHub release<br>- Phosphor regular `arrow-left`, `calendar-blank` and `map-pin` SVGs from `@phosphor-icons/core@2.1.1` | Files in place. The icons are converted to 256-viewport vectors like the existing `ph_*.xml`. | cli |
| Happy path   | Run `HoColorsTest` | `glass` is `#283432` at 72% in dark and `#E0EBE9` at 80% in light | system |
| Happy path   | Run `HoTypographyTest` | Text in the mono family renders, and the 15 roles are unchanged | system |
| Happy path   | Render `HoButton(tinted = true)` | Its resting fill is `primaryPressed`, the design's accent tint, with the primary border. Pressing it still darkens the fill. | system |
| Happy path   | Render `HoPosterFlower` | One image, no semantics, using the theme's decoration alpha | system |
| Happy path   | Run the existing design-system tests | All pass, and a `HoButton` without `tinted` is unchanged | system |

## Tasks to do

### `1)` Assets

> The font and icons the event page needs.

1. Add `jetbrains_mono_regular.ttf` and its OFL license text.
2. Add `ph_arrow_left.xml`, `ph_calendar_blank.xml` and `ph_map_pin.xml`, and expose them in `HoIcons`.

### `2)` Tokens

> The glass fill and the mono family.

1. Add `glass` to `HoColors` for both themes, and pin it in `HoColorsTest`.
2. Add a `mono` `FontFamily` to `HoTypography`, outside the 15-role scale. Test that it loads.

### `3)` Components

> Pieces the event page can't build itself, because `Res` is internal to `designsystem`.

1. `HoButton` and `ButtonFrame` get `tinted: Boolean = false`. When it's true, the Primary resting container is `primaryPressed`. When pressed, it shows `primaryPressed` composited twice. Test it.
2. `HoPosterFlower(modifier)`: the `flowers_top_right` image, decorative. Test it has no semantics.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | The icons and the font resolve through `Res`, and the build passes |
| 2 | `HoColorsTest` and `HoTypographyTest` pass on wasmJs and Android |
| 3 | `HoButtonTest` and `HoFlowerBorderTest` pass, and the existing button tests pass unchanged |
