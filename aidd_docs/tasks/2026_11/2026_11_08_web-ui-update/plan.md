---
objective: "Web shows the single-page homeout website design, fed by the app's events, while the phone app stays unchanged."
status: in-progress
---

# Plan: Web edition UI update

Source: `aidd_docs/tasks/2026_11/2026_11_08_web-ui-update/spec.md`; design canvas "Event Management UI Library" version `1794141947-b53f`, page `project/homeout Website.dc.html`.

## Phases

| #   | Phase | File | Depends on |
| --- | ----- | ---- | ---------- |
| 1   | Website content: next events, copy, colors, flower and arrow assets | [`phase-1.md`](./phase-1.md) | none |
| 2   | Website page replaces the Web edition | [`phase-2.md`](./phase-2.md) | 1 |
| 3   | Scroll motion and reduced motion | [`phase-3.md`](./phase-3.md) | 2 |
| 4   | Project memory | [`phase-4.md`](./phase-4.md) | 3 |

## External sources

| URL | What it settled |
| --- | --------------- |
| none | The design file and the codebase settled every assumption; Compose APIs used (`ScrollState`, `graphicsLayer`, `BoxWithConstraints`, `onGloballyPositioned`) are already in use or standard foundation. |

## Impact

| Category | Verdict and evidence | Handled in |
| -------- | -------------------- | ---------- |
| Runtime | `window.matchMedia("(prefers-reduced-motion: reduce)")` read in `wasmJsMain/main.kt`: unverified in Kotlin/Wasm interop until phase 3 runs it in the browser. Backdrop blur of the scrolled nav has no Compose Wasm equivalent (`Modifier.blur` blurs content, not what is behind): unverified, replaced by the 0.86-alpha fill (close match allowed). | 3 |
| Regression | Medium. `App.kt`, `RootScreen.kt` and `AppHeader.kt` lose their Web branches (`edition`, `showTabs`, `showAccount`). Covered by `AppTest`, `RootScreenTest`, `AuthFlowTest`, `PurchaseFlowTest`, `TicketQrFlowTest`, phone `BrowserBackTest` cases, `SystemBackTest`, `RecreationTest`, `SessionTest`. No safety-net phase: these already lock phone behavior, and phase 2 runs them unchanged. | 2 |
| Architecture | From: one `App(edition)` with a `NavHost` whose Web branch redirects Login/Signup/Buy/QR and adds `GetAppDestination`. To: `App()` is the phone app only; Web's `main()` shows a separate `Website` composable (one scroll page, no `NavHost`, no browser-history binding) with its own `WebsiteViewModel`. `AppEdition`, `GetAppDestination` and `getapp/` are removed. | 2 |
| Documentation | `architecture.md` (Editions, Web history binding), `codebase-map.md` (`website/` replaces `getapp/`), `navigation.md` (Web routing), `project-brief.md` (Web edition feature), `design.md` (Website page, web-only palette and pill), `testing.md` (tags, Web tests, Browser QA entry), `mobile.md` (line 5 says Android and iOS share all UI with Web; only logic and the design system are shared now). | 4 |

## Rules

| Rule | Why it applies |
| ---- | -------------- |
| design.md: the canvas is the source of truth; read it before UI work | Every section, copy and size comes from `homeout Website.dc.html` |
| design.md: designsystem `Res` is internal | Flower images reach the website only through a designsystem component |
| design.md / spec: WCAG AA contrast | Every text and background pair of the fixed palette is tested |
| architecture.md: plain `MutableStateFlow` for screen state | `combine` + `stateIn` left a Web UI test blank before |
| architecture.md: Koin starts once in `main()` | The website's ViewModel comes from Koin, not from a composable-started graph |
| testing.md: `withKoin` inside `runComposeUiTest`; Friday 2026-11-13 19:00 UTC | All website UI tests |
| coding-assertions.md: build, domain/data, Wasm UI, device tests | Each phase's validation |
| vcs.md: Conventional Commits, auto commit after task | One commit per phase |

## Decisions

| Decision | Why |
| -------- | --- |
| Build the website in Compose (commonMain `website/`), not an HTML/DOM page | Keeps one stack, the shared events and Koin, and the existing Compose UI tests on Wasm and Android. Tradeoff: canvas rendering, so no SEO, no selectable text, no native `#anchor` URLs. |
| `Website` is separate from `App`; `App` drops `AppEdition` | The spec removes every Web screen of the app shell, so the Web branches in `App`, `RootScreen` and `AppHeader` become dead code. |
| Web-only palette and pill live in `composeApp/website/`, not in `designsystem` | The spec limits the new styling to Web; keeping it out of `HoColors`/`HoButton` guarantees the phone app's look can't change. |
| Motion is pure functions of scroll progress, the same formulas as the design's script | Testable without pixels, and reduced motion returns the final frame. |
