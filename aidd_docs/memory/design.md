# Design

## System

- Source of truth: the Claude Design canvas "Event Management UI Library" (https://claude.ai/artifact/1krt444a78B46kwb7jYipL), with the pages homeout UI Library, Button Set, Home Screen and homeout Website. A visual change goes into the design first, then into the app.
- Read the canvas with the Artifact tool (`read` with `path: project/<page>`) before UI work, and check its version against the one the last plan cites.
- The `designsystem` module implements it with Compose. Screens use `HoTheme`, `HoText` and `HoButton`, never raw Material.

## Tokens

- Colors: `HoColors` (light and dark), with logo-teal backgrounds. `HoColorsTest` pins every hex to the design.
- Type: `HoTypography`, 15 roles. Display and Headline roles use Big Shoulders Stencil at weight 800 in uppercase, the rest Inter. JetBrains Mono (`typography.mono`) is only for poster labels, outside the scale.
- Fonts, icons (Phosphor) and the logo flower images live in `designsystem/src/commonMain/composeResources`, with licenses in `designsystem/licenses/`.
- `HoGlyphs` holds the design's own 24-grid stroke icons as `ImageVector`s: tabs (taking stroke and fill), ticket, counter, chevrons, check, QR mark, and the payment methods, the account mark on the Google buttons and the alert mark. Phosphor icons stay in `HoIcons`.
- The design's names map to the tokens: `accent` is `primary`, `accentText` is `onPrimaryText`, `accentTint` is `primaryPressed`, `tonal` is `secondaryContainer`, `hot` is `tertiary` and `onHot` is `onTertiary`.

## Website (Web only)

- The "homeout Website" page is the Web edition's design: one look in both themes, built in `composeApp/.../website/`, not in `designsystem`.
- `WebsiteColors` holds its fixed palette, and `WebsiteColorsTest` enforces AA on every text and background pair. `PillButton` is its full-round button (hover lift, focus ring), used only on Web.
- It reuses `HoFlower(part)` (a logo flower at full strength, decorative), `HoStoreBadge(store)` (the App Store and Google Play badge artwork, rendered from the design's SVGs to PNG) and `HoGlyphs.ArrowDown` from the design system.
- A close match is accepted: the nav's backdrop blur has no Compose equivalent, so the 0.86 fill stands alone.

## Components

- `HoButton`: Primary (outlined accent), Secondary (tonal) and Outline (neutral), each with pressed and disabled states. `tinted` gives Primary a resting accent fill.
- `HoTextField` (a labelled 48 dp field with placeholder, password, error or hint, and a slot at the end of the label) and `HoCheckbox` (the whole row toggles). The form tokens are `field`, `fieldBorder`, `error`, `errorContainer` and `surfaceHover`. The design's `fieldBorder` is 2.5:1 (light) and 2.9:1 (dark) against the field, below the 3:1 for a control boundary: the design's values are kept and flagged.
- `HoIconButton`, `HoFlowerBorder` (the top border behind the app) and `HoPosterFlower` (a poster's corner). `HoButton` takes a Phosphor `icon` or a `glyph`.
- The QR placeholder (`composeApp/.../qr/QrPattern.kt`) is the design's `qrPath` ported as is. It's checked against Node output of the design's JS.

## Accessibility

- WCAG AA: text needs 4.5:1, or 3:1 for large text, on the background it sits on in both themes. `HoContrastTest` and `PosterContrastTest` enforce it.
- Icon-only buttons carry a content description. Decorative images have none.
