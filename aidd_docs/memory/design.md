# Design

## System

- Source of truth: the Claude Design canvas "Event Management UI Library" (https://claude.ai/artifact/1krt444a78B46kwb7jYipL), with the pages homeout UI Library, Button Set and Home Screen. A visual change goes into the design first, then into the app.
- Read the canvas with the Artifact tool (`read` with `path: project/<page>`) before UI work, and check its version against the one the last plan cites.
- The `designsystem` module implements it with Compose. Screens use `HoTheme`, `HoText` and `HoButton`, never raw Material.

## Tokens

- Colors: `HoColors` (light and dark), with logo-teal backgrounds. `HoColorsTest` pins every hex to the design.
- Type: `HoTypography`, 15 roles. Display and Headline roles use Big Shoulders Stencil at weight 800 in uppercase, the rest Inter. JetBrains Mono (`typography.mono`) is only for poster labels, outside the scale.
- Fonts, icons (Phosphor) and the logo flower images live in `designsystem/src/commonMain/composeResources`, with licenses in `designsystem/licenses/`.

## Components

- `HoButton`: Primary (outlined accent), Secondary (tonal) and Outline (neutral), each with pressed and disabled states. `tinted` gives Primary a resting accent fill.
- `HoIconButton`, `HoFlowerBorder` (the top border behind the app) and `HoPosterFlower` (a poster's corner).

## Accessibility

- WCAG AA: text needs 4.5:1, or 3:1 for large text, on the background it sits on in both themes. `HoContrastTest` and `PosterContrastTest` enforce it.
- Icon-only buttons carry a content description. Decorative images have none.
