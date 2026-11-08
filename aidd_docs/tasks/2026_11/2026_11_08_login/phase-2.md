---
status: done
---

# Instruction: Design-system additions

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
.
└── designsystem/
    ├── src/commonMain/kotlin/app/homeout/designsystem/
    │   ├── HoColors.kt ✏️ error, errorContainer, field, fieldBorder, surfaceHover
    │   ├── HoTextField.kt ✅ labelled field: placeholder, password, error border, supporting text
    │   ├── HoCheckbox.kt ✅ 20 px box with a label, toggled by the whole row
    │   ├── HoGlyphs.kt ✏️ Account (Google button mark), AlertCircle
    │   └── HoIcons.kt ✏️ AppleLogoFill, GooglePlayLogoFill
    ├── src/commonMain/composeResources/drawable/
    │   ├── ph_apple_logo_fill.xml ✅ Phosphor (MIT)
    │   └── ph_google_play_logo_fill.xml ✅ Phosphor (MIT)
    ├── src/androidMain/kotlin/app/homeout/designsystem/FoundationsPreview.kt ✏️ field, checkbox
    └── src/commonTest/kotlin/app/homeout/designsystem/
        ├── HoColorsTest.kt ✏️ pins the new hexes
        ├── HoContrastTest.kt ✏️ error and placeholder pairs
        ├── HoTextFieldTest.kt ✅
        ├── HoCheckboxTest.kt ✅
        └── HoGlyphsTest.kt ✏️ the new glyphs
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | `HoTheme` in light and dark | Tokens resolve | system |
| Happy path   | Type into a `HoTextField` labelled "Email" | The text shows. The node is found by its label. The placeholder is gone | system |
| Edge case    | A field with `error = "Enter an email like you@example.com."` | The message shows and the node reports an error state | system |
| Edge case    | A password field | It shows dots and reports password semantics | system |
| Edge case    | A field losing focus | `onFocusLost` fires once | system |
| Edge case    | Tap a `HoCheckbox` row's label | It toggles, and reports checked or unchecked | system |
| Edge case    | Contrast: `error` on `background` and on `field`; `onSurfaceVariant` on `field`; `onSurface` and `onPrimaryText` (the "Log in instead" link) on `errorContainer` | Each reaches 4.5:1. `fieldBorder` on `background` reaches 3:1 | system |

## Tasks to do

### `1)` Tokens

> Add the design's form and error colors.

1. Add to `HoColors`, from the design (light / dark):
   - `error`: `#a12d3a` / `#ffb3b8`
   - `errorContainer`: `#a12d3a` at 10% / `#ffb3b8` at 14%
   - `field`: white at 55% / `#334340` at 85%
   - `fieldBorder`: `#7d9894` / `#6f8a86`
   - `surfaceHover`: `#c4d6d3` / `#3c4e4b`
2. Pin them in `HoColorsTest`. Add the Test Scope's pairs to `HoContrastTest`, including `onPrimaryText` on `errorContainer`, compositing the translucent fills over `background`.

### `2)` Field and checkbox

> Form inputs built from the design.

1. `HoTextField`:
   - takes a `TextFieldState` owned by the caller, never creating its own. A password's state must be a plain `remember { TextFieldState(initial) }`: `rememberTextFieldState` is saveable and would write the password to saved state
   - shows the label (13 px, weight 500) above the field
   - takes a placeholder, `password` (via `BasicSecureTextField`), `error: String?` and a `supporting: String?` hint
   - the field: 48 px, radius 8, `field` fill, a 1 px border in `fieldBorder` (or `error` when in error), a cursor in `primary`
   - error and hint text at 12 px, in `error` or `onSurfaceVariant`
   - the hint or error is announced with the field
   - `onFocusLost`
2. `HoCheckbox(checked, onCheckedChange, label)`: 20 px, `primary` when checked, the whole row toggles, with checkbox semantics.
3. Tests for both, and an entry in `FoundationsPreview`.

### `3)` Glyphs and icons

> Marks for the Google button, alerts and the store badges.

1. `HoGlyphs.Account`: circle r9, head r3 at (12, 10), shoulders `M6.5 18.2a6.5 6.5 0 0 1 11 0`, stroke 1.8. It's the design's Google mark.
2. `HoGlyphs.AlertCircle`: circle r9, `M12 7.5v5.5`, `M12 16.5h.01`, stroke 2.
3. Phosphor fill `apple-logo` and `google-play-logo` as vector drawables, exposed in `HoIcons`. The Phosphor MIT license already covers them.
4. Add them to `HoGlyphsTest`.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `HoColorsTest` pins the five tokens in both themes. `HoContrastTest` passes with the new pairs |
| 2 | A field shows its label, placeholder, hint or error, and reports password and error semantics. The checkbox toggles from its label |
| 3 | The glyphs build on a 24 grid and the icons load |
