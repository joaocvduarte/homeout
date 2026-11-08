---
status: done
---

# Instruction: Website page replaces the Web edition

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
.
└── composeApp/src/
    ├── commonMain/kotlin/app/homeout/
    │   ├── App.kt ✏️  (no edition: phone app only)
    │   ├── AppEdition.kt ❌
    │   ├── AppHeader.kt ✏️  (no showAccount)
    │   ├── Destinations.kt ✏️  (no GetAppDestination)
    │   ├── di/PresentationModule.kt ✏️  (no GetAppViewModel)
    │   ├── getapp/
    │   │   ├── GetAppScreen.kt ❌
    │   │   └── GetAppViewModel.kt ❌
    │   ├── root/RootScreen.kt ✏️  (no showTabs)
    │   └── website/
    │       ├── Website.kt ✅  (route, page scroll, section anchors)
    │       ├── WebsiteLayout.kt ✅  (breakpoints, clamp sizes)
    │       ├── PillButton.kt ✅  (pill and text link)
    │       ├── WebsiteNav.kt ✅
    │       ├── HeroSection.kt ✅
    │       ├── ShowcaseSection.kt ✅
    │       ├── EventsSection.kt ✅
    │       ├── StepsSection.kt ✅
    │       ├── GetAppSection.kt ✅
    │       └── WebsiteFooter.kt ✅
    ├── commonTest/kotlin/app/homeout/
    │   ├── di/KoinGraphTest.kt ✏️  (no GetAppViewModel)
    │   ├── getapp/
    │   │   ├── GetAppScreenTest.kt ❌
    │   │   └── WebEditionTest.kt ❌
    │   └── website/
    │       ├── WebsiteLayoutTest.kt ✅
    │       └── WebsiteTest.kt ✅
    ├── wasmJsMain/kotlin/app/homeout/main.kt ✏️  (shows Website)
    └── wasmJsTest/kotlin/app/homeout/BrowserBackTest.kt ✏️  (drop the three web* cases)
```

## Test Scope

| Scenario | Action | Observable outcome | Actor |
| -------- | ------ | ------------------ | ----- |
| Setup | `withKoin(friday, Europe/Lisbon, signedIn = false)`; `Website` in a box sized 1280×820, 900×820 or 375×740 | Page renders at the top | browser |
| Happy path | Read the page top to bottom | Nav, hero, showcase, next events, how it works, get the app, footer, in that order, with the design's copy | browser |
| Happy path | Next events at 1280 | Four sample event cards in one row, each with kicker, title, date · area, "Music · 21:30", price and Get tickets | browser |
| Happy path | Tap nav "Next events" / "How it works" / "Get the app" | The scroll lands on that section | browser |
| Happy path | Tap any Get tickets, "See all 4 events", "Download the app", hero "See what's on" | Ticket, download and see-all actions land on Get the app; "See what's on" lands on Next events | browser |
| Edge case | Width 900 | Event grid 2 columns, steps 1 column, nav links shown | browser |
| Edge case | Width 375 | Nav links hidden (Get the app pill stays), event cards scroll horizontally, steps stacked | browser |
| Edge case | Render under a light and a dark outer theme | Same design colors in both | browser |
| Edge case | Footer | "Events" and "How it works" only, no Terms or Privacy | browser |
| Edge case | Restored old URL `#app.homeout.EventDestination/fado-old-cistern` on Web | The website page shows; no event page, Buy or Get the app screen | browser |
| Edge case | Phone `App()` | All existing phone tests pass unchanged: Login first, tabs, header bell and avatar | system |
| Teardown | `withKoin` stops Koin; Browser tests reset the URL | Baseline restored | browser |

## Wireframe

```txt
┌──────────────────────────────────────────────────────┐
│ (1) Nav: [HOMEOUT]   Next events · How it works ·    │
│          Get the app            (Get the app) pill   │
├──────────────────────────────────────────────────────┤
│ (2) Hero  ✿ flowers left · mid · right ✿             │
│             [ HOMEOUT ]                              │
│             Head out tonight.                        │
│             intro paragraph                          │
│     (Download the app)  (See what's on)              │
│             Scroll ↓                                 │
├──────────────────────────────────────────────────────┤
│ (3) Showcase: rounded teal panel                     │
│   ONE APP / EVERY NIGHT OUT…  │  ┌──┐┌──┐┌──┐ posters │
│   body copy                   │  └──┘└──┘└──┘ (fan)   │
├──────────────────────────────────────────────────────┤
│ (4) Next events (light, rounded top)                 │
│   THIS WEEK IN LISBON / NEXT EVENTS  (See all N)     │
│   ┌────┐ ┌────┐ ┌────┐ ┌────┐                        │
│   │card│ │card│ │card│ │card│  4 · 2 · scroll row    │
│   └────┘ └────┘ └────┘ └────┘                        │
├──────────────────────────────────────────────────────┤
│ (5) How it works: headline                           │
│   ┌ 01 ┐ ┌ 02 ┐ ┌ 03 ┐   3 · 1 columns               │
├──────────────────────────────────────────────────────┤
│ (6) Get the app: HEAD OUT / TONIGHT, copy            │
│     (Get it for iPhone) (Get it for Android)         │
│      ✿ mirrored flowers ✿                            │
├──────────────────────────────────────────────────────┤
│ (7) Footer: [HOMEOUT] Made in Lisbon · © 2026        │
│                         Events · How it works        │
└──────────────────────────────────────────────────────┘
```

1. Nav: wordmark (scrolls to top), section links (hidden under 760), Get the app pill; overlays the page, 72 high.
2. Hero: at least the viewport height minus the nav; flowers bleed from the top; the main message and calls to action.
3. Showcase: 2.4 viewports tall with its content pinned while scrolling; final, unanimated frame in this phase.
4. Next events: the app's upcoming events from `WebsiteViewModel`; each card's poster uses the event's colors and the corner flower.
5. How it works: the design's three steps and their colors.
6. Get the app: the target of every ticket, download and see-all action; badges go nowhere.
7. Footer: brand, line and the two links the spec keeps.

## Tasks to do

### `1)` Phone app without Web branches

> `App()` is the phone app only, unchanged in behavior.

1. Remove `edition`, `web` branches, `GetAppDestination` use from `App.kt`; delete `AppEdition.kt`.
2. Remove `showTabs` from `RootScreen` and `showAccount` from `AppHeader`.
3. Delete `GetAppDestination`, `getapp/`, its tests, its Koin binding and `KoinGraphTest` line.
4. Drop `webBrowserBackLeavesGetTheAppForTheEvent`, `webRestoredBuyAddressOpensGetTheApp`, `webRestoredLogInAddressShowsHome` from `BrowserBackTest`.

### `2)` Layout rules

> Sizes and columns follow the design's breakpoints.

1. `WebsiteLayout.kt`: `clampDp(min, vw, max, width)` for the design's `clamp()` sizes; event columns (4 above 1100, 2 above 640, else a horizontal snapping row at 82% width); steps (3 above 900, else 1); nav links shown above 760; fan scale 0.8 under 520; gutter `clamp(16, 4vw, 48)`, content max 1200.

### `3)` Page and sections

> Every section of the design, in order, with its copy.

1. `PillButton` (52 high, full-round, given fill and ink, 1.5 dp inset outline variant, hover lift, focus ring, `Role.Button`) and a text link (15/14 sp, underline on hover).
2. `Website(viewModel, timeZone, reduceMotion)` wraps itself in `HoTheme(darkTheme = true)`, so the system setting never reaches it; a `verticalScroll` column under an overlaid nav; each section records its top with `onGloballyPositioned`; `scrollTo(section)` animates (snaps when `reduceMotion`).
3. Build the seven sections per the wireframe, sizes and colors from the design file, using `HoText` with `WebsiteColors` and `HoTheme.typography`; flowers via `HoFlower`, posters via `HoPosterFlower`, arrow via `HoGlyphs.ArrowDown`, phone icon via `HoGlyphs.Phone`.
4. Test tags: `website`, `website-nav`, `section-top`, `section-events`, `section-how`, `section-app`, `website-event-<id>`, `website-tickets-<id>`, `website-see-all`, `website-footer`.

### `4)` Web entry

> The browser shows the website.

1. `main.kt`: keep `initKoin`, show `Website(koinViewModel(), koinInject(), reduceMotion = false)` inside `ComposeViewport`; drop `bindToBrowserNavigation`.

## Test acceptance criteria

| Task | Acceptance criteria |
| ---- | ------------------- |
| 1 | `./gradlew build` compiles every target; all phone UI tests, `SystemBackTest` and `RecreationTest` pass unchanged; no `AppEdition`, `GetApp` or `getapp-page` left in sources |
| 2 | `WebsiteLayoutTest` pins columns at 1280/1100/900/640/375 and clamp results at their bounds |
| 3 | `WebsiteTest` finds every section's copy in design order; four cards with each label at 1280; nav links reach their section; every Get tickets, see-all and download reaches `section-app`; footer has no "Terms" or "Privacy"; nav links absent at 375; wrapped in `HoTheme(darkTheme = false)` and `true`, captured pixels of the hero (`#283432`), the events section (`#E0EBE9`) and a pill (`#C9F2EB`) are identical and match the design |
| 4 | `wasmJsBrowserDevelopmentRun` shows the website at http://localhost:8080/ and at `#app.homeout.EventDestination/fado-old-cistern`; screenshots at 1280, 900 and 375 compared with the design |
