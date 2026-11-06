---
objective: "The app matches design v10: logo-teal backgrounds that follow the phone's theme (pale tint in light, deep teal in dark), every text color at WCAG AA, and a border of real flowers from the logo across the top of Home."
status: in-progress
---

# Plan: Logo teal background and flower border

Source: user request, 2026-11-06: "update the code to reflect the current ui design". It follows the approved design iterations on https://claude.ai/artifact/1krt444a78B46kwb7jYipL, live version 1794004141-f649 (v10), checked unchanged before planning. The user's decisions:
- Logo colors in the background, with the theme following the phone: light uses a pale logo-teal tint and dark text, dark uses a deep logo teal and off-white text.
- All text passes WCAG AA (4.5:1).
- The wordmark keeps its original teal block with off-white letters.
- A border of real flowers cut from the logo runs across the top: left, middle and right pieces. There's nothing at the bottom.

## Phases

| #   | Phase        | File                         | Depends on |
| --- | ------------ | ---------------------------- | ---------- |
| 1   | Spec update | [`phase-1.md`](./phase-1.md) | none |
| 2   | Teal tokens | [`phase-2.md`](./phase-2.md) | 1 |
| 3   | Flower border on Home | [`phase-3.md`](./phase-3.md) | 2 |

## External sources

| URL   | What it settled   |
| ----- | ----------------- |
| https://claude.ai/artifact/1krt444a78B46kwb7jYipL | Design v10 holds:<br>- New hex values in the UI Library palettes and in the Button Set and Home Screen `theme()`.<br>- Flower layer opacity (`floraOpacity`): 1 in light, 0.92 in dark.<br>- Three flower images under `project/assets/` (`collage-top_left.png` 118×124, `collage-top_mid.png` 330×80, `collage-top_right.png` 194×135), placed flush top at 124, 176 and 150 px wide. |
| https://www.w3.org/TR/WCAG21/#contrast-minimum | Text needs at least 4.5:1, or 3:1 for large text. The ratios here use WCAG 2.1's relative luminance formula. |
| https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources-usage.html | PNG images go in `composeResources/drawable` and load with `painterResource(Res.drawable.x)` on every platform. |

## Impact

| Category                                           | Verdict and evidence | Handled in         |
| -------------------------------------------------- | -------------------- | ------------------ |
| runtime | None for dependencies. The three PNGs add about 155 KB to each app. The images come from the 1044×596 WhatsApp copy of the logo, so they look soft on high-density screens (unverified on a real device). | 3 |
| regression | Medium. `HoColorsTest` pins today's hex values and will fail until updated (observed: it's the only test with hard-coded hex). `HoButtonPressedTest` reads `HoColors` fields, so it follows the new values. `HoThemeTest` compares by reference. The Home and design-system UI tests check text and structure, not color. The flower layer sits outside the scrolling column, so the existing Home tests' node lookups are unaffected. | 2, 3 |
| system bars | Low. `MainActivity` calls `enableEdgeToEdge()` with defaults, which picks status-bar icon colors from the phone's night mode. Both themes now follow that mode with a light-on-dark or dark-on-light background, so the icons stay readable. Flowers behind the status bar get a screenshot check. | 3 |
| architecture | None. Token values and a new decoration composable in `designsystem`; `composeApp` only places it. | none |
| documentation | `spec.md` (homeout event app) gains the logo-teal background, AA and flower-border rules. | 1 |

## Decisions

| Decision   | Why   |
| ---------- | ----- |
| The flower border is a `designsystem` component (`HoFlowerBorder`) with its images in `designsystem` resources. | It's brand decoration like the fonts and icons, and it can be reused when the other screens arrive. |
| The middle flower piece is centered horizontally instead of at the design's fixed 112 px. | It lines up with the design at phone width (its center is 200 px of 372, the design's 112 + 176/2) and stays centered on wider web windows. |
| The layer opacity becomes a theme value (`HoColors.decorationAlpha`: 1 light, 0.92 dark). | The design sets it per theme, like a color token. |
