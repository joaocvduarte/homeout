---
status: pending
---

# Instruction: Project memory

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
.
└── aidd_docs/memory/
    ├── architecture.md ✏️
    ├── codebase-map.md ✏️
    ├── design.md ✏️
    ├── navigation.md ✏️
    ├── mobile.md ✏️
    ├── project-brief.md ✏️
    └── testing.md ✏️
```

## Test Scope

| Scenario | Action | Observable outcome | Actor |
| -------- | ------ | ------------------ | ----- |
| Setup | Final code from phases 1–3 | Sources to describe | system |
| Happy path | Grep memory for `AppEdition`, `GetAppDestination`, `getapp`, `bindToBrowserNavigation` on Web | No stale mention | cli |
| Edge case | Each memory claim about Web | Matches a file or test in the repo | cli |

## Tasks to do

### `1)` Record the website

> Memory describes Web as it now is.

1. `architecture.md`: replace Editions with "Web shows `Website`, a single scroll page with `WebsiteViewModel`; `App` is the phone app"; drop Web history binding; add the motion and reduced-motion approach.
2. `codebase-map.md`: `website/` replaces `getapp/`.
3. `navigation.md`: Web has no destinations; anchors scroll to sections; old fragments show the page.
4. `project-brief.md`: Web edition feature rewritten per the spec.
5. `design.md`: add the "homeout Website" page, `WebsiteColors`, the web-only pill, `HoFlower`, `HoGlyphs.ArrowDown`.
6. `mobile.md`: Android and iOS share the domain, data and design system with Web, but not the UI.
7. `testing.md`: website tags and tests, removed Web tests, Browser QA entry.

## Test acceptance criteria

| Task | Acceptance criteria |
| ---- | ------------------- |
| 1 | The grep finds no stale term; each changed line names something present in the code |
