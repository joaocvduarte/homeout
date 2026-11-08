---
status: pending
---

# Instruction: Scroll motion and reduced motion

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
.
└── composeApp/src/
    ├── commonMain/kotlin/app/homeout/website/
    │   ├── WebsiteMotion.kt ✅  (hero, showcase and fan frames from scroll progress)
    │   ├── Website.kt (from phase 2, modified)  (reveal-on-enter, nav scrolled state)
    │   ├── WebsiteNav.kt (from phase 2, modified)
    │   ├── HeroSection.kt (from phase 2, modified)
    │   ├── ShowcaseSection.kt (from phase 2, modified)
    │   ├── EventsSection.kt (from phase 2, modified)
    │   ├── StepsSection.kt (from phase 2, modified)
    │   └── GetAppSection.kt (from phase 2, modified)
    ├── commonTest/kotlin/app/homeout/website/
    │   ├── WebsiteMotionTest.kt ✅
    │   └── WebsiteTest.kt (from phase 2, modified)
    └── wasmJsMain/kotlin/app/homeout/main.kt ✏️  (reads prefers-reduced-motion)
```

## Test Scope

| Scenario | Action | Observable outcome | Actor |
| -------- | ------ | ------------------ | ----- |
| Setup | Pure functions with given scroll and viewport values | Frames computed without UI | system |
| Happy path | Hero at scroll 0 and at 0.8 viewport | translateY 0 → 120, scale 1 → 0.92, alpha 1 → 0.1 | system |
| Happy path | Showcase progress 0, 0.45, 1 | Panel scale 0.84 → 1 with radius 48 → 0; posters rise 140 → 0 and fan to ±92 px, ±9°; copy alpha 0 → 1 | system |
| Happy path | Scroll the page past 24 dp | Nav gets its translucent fill and bottom line | browser |
| Happy path | A reveal item enters the viewport | It fades and rises over 700 ms, its delay 90 ms (cards) or 110 ms (steps) per index, once | browser |
| Edge case | `reduceMotion = true` | Every frame is the final one; reveal items are visible at once; nav still changes fill; anchor scrolls jump | browser |
| Edge case | Browser with `prefers-reduced-motion: reduce` | `main.kt` passes `reduceMotion = true` | browser |

## Tasks to do

### `1)` Motion formulas

> The design's script, as testable functions.

1. `WebsiteMotion.kt`: `heroFrame(scrollY, viewportH)`, `showcaseFrame(progress)` (panel scale and radius, rise, fan per index k ∈ {-1,0,1}, copy alpha and offset), with the design's cubic ease-out and thresholds; each returns the final frame when `reduceMotion`.

### `2)` Apply motion

> Scroll drives the page like the design.

1. Hero and showcase read `ScrollState.value` and section tops, apply frames through `graphicsLayer` (no recomposition of content per frame).
2. Showcase pins its content: translate by `clamp(scroll − top, 0, height − viewport)`.
3. Nav: fill `#283432` at 0.86 alpha and a 1 dp `#E7ECEB` 12% line once scroll > 24, 240 ms.
4. Reveal: a modifier that starts hidden (alpha 0, +36 dp) and animates in once its top passes 92% of the viewport; delays per index.

### `3)` Reduced motion

> The page stays complete and still for visitors who ask.

1. `main.kt`: `window.matchMedia("(prefers-reduced-motion: reduce)").matches` → `reduceMotion`.
2. With it, skip reveal hiding, hero and showcase transforms, and animated anchor scroll.

## Test acceptance criteria

| Task | Acceptance criteria |
| ---- | ------------------- |
| 1 | `WebsiteMotionTest` pins the frames above at 0, mid and end progress, and the final frame under reduced motion |
| 2 | `WebsiteTest`: after scrolling to Next events every card is displayed; with `mainClock.autoAdvance = false`, a revealed card is not finished at 300 ms and is at 800 ms; nav state toggles past 24 dp; screenshots match the design's scroll states in the browser |
| 3 | `WebsiteTest` with `reduceMotion = true`: every section's copy and cards are displayed on the first frame without advancing the clock; in Chrome with reduced motion emulated, the page shows no motion |
