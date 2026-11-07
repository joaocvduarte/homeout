---
status: pending
---

# Instruction: Design-system additions

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
designsystem/src/
├── commonMain/composeResources/drawable/
│   ├── ph_music_notes.xml      ✅ Phosphor regular, @phosphor-icons/core 2.1.1
│   ├── ph_vinyl_record.xml     ✅
│   ├── ph_fork_knife.xml       ✅
│   ├── ph_wine.xml             ✅
│   ├── ph_compass.xml          ❌ Explore is gone
│   └── ph_ticket.xml           ❌ replaced by the design's own ticket glyph
├── commonMain/kotlin/app/homeout/designsystem/
│   ├── HoColors.kt             ✏️ onTertiary; remove stripe
│   ├── HoIcons.kt              ✏️ MusicNotes, VinylRecord, ForkKnife, Wine; drop Compass and Ticket
│   └── HoGlyphs.kt             ✅ the design's 24-viewBox stroke icons as ImageVectors
└── commonTest/kotlin/app/homeout/designsystem/
    ├── HoColorsTest.kt         ✏️ onTertiary
    ├── HoContrastTest.kt       ✏️ onTertiary on tertiary
    ├── HoGlyphsTest.kt         ✅
    └── HoButtonTest.kt         ✏️ uses HoGlyphs.Ticket in place of HoIcons.Ticket
designsystem/src/androidMain/kotlin/app/homeout/designsystem/FoundationsPreview.kt ✏️ HoGlyphs.Ticket in place of HoIcons.Ticket
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Download the four Phosphor SVGs and convert them to vectors like the existing `ph_*.xml` | Files in place | cli |
| Happy path   | `HoColorsTest` | `onTertiary` is `#ffffff` in light and `#283432` in dark | system |
| Happy path   | `HoContrastTest` | `onTertiary` on `tertiary` is at least 4.5:1 in both themes | system |
| Happy path   | `HoGlyphsTest` | Each glyph has a 24×24 viewport. The tab glyphs take a fill color: they're filled when active and unfilled otherwise. | system |
| Happy path   | All existing design-system tests | Pass, after `stripe`, Compass and the old Ticket are removed | system |

## Tasks to do

### `1)` Icons

> Every icon the new screens draw.

1. Add the four Phosphor event icons to `HoIcons`.
2. `HoGlyphs`, built from the design's SVG paths with stroke 1.8 (2 for minus, plus and chevrons):
   - `home(fill)`, `tickets(fill)` and `profile(fill)` for the tabs
   - `Ticket`: the CTA icon, with a dashed stub
   - `Minus`, `Plus`, `ChevronLeft` and `ChevronRight`
   - `CheckCircle`, `QrMini`, `Phone`, `Card` and `Wallet`
3. Remove `ph_compass` and `ph_ticket`, and their `HoIcons` entries. `FoundationsPreview` moves to `HoGlyphs.Ticket`.
4. The design's names map to existing tokens: `accentTint` is `primaryPressed`, `accentText` is `onPrimaryText`, `accent` is `primary`, `tonal` is `secondaryContainer`, and `hot` is `tertiary`. Only `onHot` (as `onTertiary`) is new.

### `2)` Colors

> The badge color and cleanup.

1. `onTertiary` on `HoColors`. Pin it in `HoColorsTest` and add it to `HoContrastTest`.
2. Remove `stripe`.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `HoGlyphsTest` passes, and the icons resolve through `HoIcons` |
| 2 | `HoColorsTest` and `HoContrastTest` pass, and no code references `stripe` |
