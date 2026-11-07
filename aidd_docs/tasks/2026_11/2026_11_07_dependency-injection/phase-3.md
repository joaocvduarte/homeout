---
status: pending
---

# Instruction: Recreation survival and docs

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
gradle/libs.versions.toml                          ✏️ androidx-test-ext-junit 1.3.0, compose ui-test-junit4 1.12.1
androidApp/build.gradle.kts                        ✏️ testInstrumentationRunner, androidTest dependencies
androidApp/src/androidTest/kotlin/app/homeout/android/RecreationTest.kt  ✅
aidd_docs/memory/architecture.md                   ✏️ Koin wiring, ViewModels
aidd_docs/memory/testing.md                        ✏️ withKoin harness, recreation test and its command
aidd_docs/memory/codebase-map.md                   ✏️ di/, HomeoutApplication
aidd_docs/memory/coding-assertions.md              ✏️ androidApp connected test in the push gate
README.md                                          ✏️ modules table (data and composeApp hold Koin modules)
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | `createAndroidComposeRule<MainActivity>()` on the emulator. Koin is started by `HomeoutApplication`. | Home is shown | system |
| Happy path   | Pick Food, type "Petiscos", then `activityRule.scenario.recreate()` | After recreation: Food selected (from `HomeViewModel`), "Petiscos" in the field (from the saveable `rememberTextFieldState`, which re-sends it to the ViewModel), only Petiscos listed | system |
| Happy path   | Open Petiscos, `recreate()` | The event page for Petiscos is still shown. Back returns Home with Food and "Petiscos" kept. | system |
| Happy path   | Rotate the emulator on an event page (`adb shell settings put system user_rotation 1`) | Same page and state, nothing reloads | system |
| Happy path   | Full assertion gate (`coding-assertions.md`) plus `:androidApp:connectedDebugAndroidTest` | All pass | system |
| Happy path   | Screenshot Android (dark and light) and Web, Home and an event page | Identical to the event page plan's screenshots | system |

## Tasks to do

### `1)` Recreation test

> Prove the ViewModels survive the activity being rebuilt.

1. Add `androidTest` to `androidApp`:
   - `testInstrumentationRunner`
   - `ui-test-junit4` 1.12.1
   - `androidx.test.ext:junit` 1.3.0
   - `androidx.test:runner`
   - `ui-test-manifest` only if needed, as `debugImplementation` (MainActivity is already in the app manifest)
2. `RecreationTest`: the Home case and the event page case, using `scenario.recreate()`.
3. Check rotation by hand on the emulator once.

### `2)` Docs

> Memory and README describe the new wiring.

1. Update the memory files and the README listed above. The memory records only what was built.
2. Run the full gate and take the screenshots.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `RecreationTest` passes on the emulator, and manual rotation keeps the page and state |
| 2 | The memory and README match the code, the full gate passes, and the screenshots show no visual change |
