---
status: pending
---

# Instruction: Design system

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
designsystem/src/
├── commonMain/
│   ├── composeResources/font/
│   │   ├── big_shoulders_stencil_display_extrabold.ttf   ✅ Google Fonts, OFL
│   │   ├── inter_regular.ttf, inter_medium.ttf           ✅ Google Fonts, OFL
│   ├── composeResources/drawable/
│   │   └── ph_*.xml                ✅ Phosphor icons (MIT) the design uses: bell, magnifying-glass, house (fill), compass, ticket, user, plus, bookmark-simple, heart, arrow-right, share-network
│   └── kotlin/app/homeout/designsystem/
│       ├── HoColors.kt             ✅ dark + light token sets
│       ├── HoTypography.kt         ✅ 15 roles
│       ├── HoText.kt               ✅ text in a type role; uppercases Display and Headline roles
│       ├── HoTheme.kt              ✅ follows isSystemInDarkTheme()
│       ├── HoButton.kt             ✅ variant, size, icon, iconOnly, block, enabled
│       └── HoIcons.kt              ✅ icon resource accessors
├── androidMain/kotlin/app/homeout/designsystem/
│   └── FoundationsPreview.kt       ✅ dev-only Android Studio previews: color swatches, type scale and buttons, dark and light (not an app screen)
├── commonTest/kotlin/app/homeout/designsystem/
│   ├── HoColorsTest.kt             ✅
│   ├── HoTypographyTest.kt         ✅
│   ├── HoTextTest.kt               ✅
│   ├── HoThemeTest.kt              ✅
│   └── HoButtonTest.kt             ✅
└── androidDeviceTest/kotlin/app/homeout/designsystem/
    └── HoButtonPressedTest.kt      ✅ pixel check of the pressed state
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Run `runComposeUiTest` on wasmJs and on the Android emulator | Test host ready | system |
| Happy path   | Read every dark and light token | Each of the 11 tokens equals its design hex value from the homeout UI Library page | system |
| Happy path   | Read every type role | Size, line height, weight and letter-spacing match the UI Library page's type scale; Display and Headline roles use Big Shoulders Stencil at 800 | system |
| Happy path   | Render `HoText("Upcoming near you")` in each Display and Headline role | The node's text is "UPCOMING NEAR YOU" | system |
| Edge case    | Render `HoText` in a Title, Body or Label role | The text keeps its original case | system |
| Happy path   | Render `HoTheme` with the system dark setting on, then off | Dark tokens, then light tokens | system |
| Happy path   | Render each `HoButton` variant and size | Heights 52, 44 and 32; label and icon shown; a block button fills the width | system |
| Happy path   | Press and hold each variant (`performTouchInput { down(center) }`), then capture the button | The background at its center becomes the variant's pressed color (Primary: accent tint, Secondary: tonal hover, Outline: neutral hover); after release it goes back to default | system |
| Edge case    | Disabled button is clicked | `onClick` is not called and the button reports as disabled | system |
| Edge case    | Icon-only button | 44×44, exposing a content description | system |
| Edge case    | Small size on Android or iOS | Not offered (web only, per the design) | system |
| Happy path   | Open `FoundationsPreview` in Android Studio next to the UI Library and Button Set pages | Every swatch, type sample and button matches the design, in dark and light | system |

## Tasks to do

### `1)` Colors and typography

> The design's tokens and type scale as theme values.

1. Encode the 11 tokens per theme with the hex values from the homeout UI Library page.
2. Bundle the fonts and define the 15 type roles from its type scale.
3. `HoText` renders text in a role and uppercases it for the Display and Headline roles.

### `2)` Theme

> One theme that follows the phone's light or dark setting.

1. `HoTheme` picks the token set from `isSystemInDarkTheme()` and provides colors and typography.

### `3)` Button and icons

> `HoButton` as the Button Set page shows it.

1. Primary (accent outline), Secondary (tonal), Outline (neutral divider); default, pressed and disabled (45% opacity) states; 8dp corners.
2. Large 52, Medium 44 and Small 32 (wasmJs only); icon-only 44×44; block width.
3. Convert the design's Phosphor icons to vector drawables.
4. Test the pressed state by pixel color in `androidDeviceTest`. Image capture is on the Android device, where it is supported.

### `4)` Visual check of the foundations

> Every token, type role and button is checked by eye against the design, not only by value.

1. `FoundationsPreview` renders the color swatches, the 15 type samples (the design's sample text) and the button grid in both themes, as the UI Library and Button Set pages lay them out.
2. Add `ui-tooling` through `androidRuntimeClasspath`, as the AGP 9 migration guide says.
3. Compare each preview with its design page side by side and fix any difference.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | Token, type and `HoText` tests pass on wasmJs and Android |
| 2 | Theme follows the system setting in the UI test |
| 3 | Button tests pass, including the pressed-state pixel test on the emulator |
| 4 | Every swatch, type sample and button in the previews matches the UI Library and Button Set pages in both themes |
