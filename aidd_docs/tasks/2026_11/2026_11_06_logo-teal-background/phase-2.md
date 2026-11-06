---
status: pending
---

# Instruction: Teal tokens

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
designsystem/src/
├── commonMain/kotlin/app/homeout/designsystem/HoColors.kt     ✏️ v10 values + decorationAlpha
└── commonTest/kotlin/app/homeout/designsystem/
    ├── HoColorsTest.kt                                         ✏️ expected hex from design v10
    └── HoContrastTest.kt                                       ✅ WCAG ratio for every text pair
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Emulator and adb server running as in the first build | Device ready | system |
| Happy path   | Run `HoColorsTest` | The 11 tokens per theme equal design v10 (light background #e0ebe9 … tertiary #8e4754; dark background #283432 … tertiary #f69dab) | system |
| Happy path   | Run `HoContrastTest` | onSurface, onSurfaceVariant, onPrimaryText and tertiary are at least 4.5:1 on background and surface; onSecondaryContainer is at least 4.5:1 on secondaryContainer and secondaryPressed; primary is at least 3:1 on background | system |
| Happy path   | Run all design-system tests on wasmJs and the emulator | All pass; `HoButtonPressedTest` passes with the new pressed shades | system |
| Edge case    | Swap one token for a failing value, such as light onSurfaceVariant set to #8a9694 | `HoContrastTest` fails and names the pair; revert | system |

## Tasks to do

### `1)` New token values

> `HoColors` equals design v10.

1. Light: background #e0ebe9, surface #d1e0de, onSurface #0f1817, onSurfaceVariant #4a5654, outline #b4cbc7, primary #4f817a, onPrimaryText #335c57, secondaryContainer #c9f2eb, onSecondaryContainer #284d48, brand #64837e, tertiary #8e4754. Derived: divider is onSurface at 18%, primaryPressed is primary at 12%, secondaryPressed #aee0d8, neutralPressed is onSurface at 7%, stripe #bdd1ce.
2. Dark: background #283432, surface #334340, onSurface #e7eceb, onSurfaceVariant #b3bdbb, outline #4f6864, primary #70a89f, onPrimaryText #aee0d8, secondaryContainer #3e514e, onSecondaryContainer #c9f2eb, brand #64837e, tertiary #f69dab. Derived: divider is onSurface at 16%, primaryPressed is primary at 14%, secondaryPressed #4b625e, neutralPressed is onSurface at 8%, stripe #3c4e4b.
3. Add `decorationAlpha`: 1.0 in light, 0.92 in dark.
4. Update `HoColorsTest` to these hex values.

### `2)` Contrast test

> A token change that breaks AA fails the build.

1. Add `HoContrastTest` with a WCAG 2.1 luminance and ratio helper, and assert the pairs listed in Test Scope for both themes.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `HoColorsTest` passes on wasmJs and Android with the v10 values |
| 2 | `HoContrastTest` passes, and fails when a token is made too weak |
