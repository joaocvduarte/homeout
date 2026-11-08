---
status: done
---

# Instruction: Spec update

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
aidd_docs/tasks/2026_11/2026_11_06_homeout-event-app/
└── spec.md    ✏️ logo-teal background, contrast and flower border rules
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | Read `spec.md` | Line 17 says "The theme follows the phone's light or dark setting."; line 18 says "The look matches the design's foundations:"; line 22 starts the Home bullet list | cli |
| Happy path   | Apply the edits below | The spec states the logo-teal backgrounds, AA contrast and the top flower border | cli |
| Edge case    | Read the spec back | No hex values, file names or code details; Open Questions still "None" | cli |

## Tasks to do

### `1)` Add the look rules

> The spec carries the user's decisions.

1. Under line 18 ("The look matches the design's foundations:"), add a first sub-bullet: "Backgrounds use the homeout logo teal: a pale tint in light and a deep teal in dark. Every text color meets WCAG AA (4.5:1) on the background it sits on."
2. In the Home screen list (from line 22), add a first item: "a border of real flowers cut from the homeout logo, across the top, behind the content".
3. In Done-when, add: "Every text color passes 4.5:1 contrast against the background it sits on, in both themes."
4. In Context, append: "Updated again on 2026-11-06: the backgrounds use the logo teal in both themes, and a flower border from the logo runs across the top of Home (design v10)."

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | The spec contains the four additions, keeps line 17 unchanged, and still says Open Questions: None |
