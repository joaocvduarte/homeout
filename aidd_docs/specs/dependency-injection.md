# homeout: Dependency Injection and ViewModels

## Target

homeout's screens get their state from Kotlin ViewModels, and every dependency, from repository to use cases to ViewModels, is provided by Koin on Android, iOS and Web, with the app behaving exactly as it does today.

## Hard constraints

- Koin is the dependency injection framework, wired from the shared code so the same graph serves Android, iOS and Web. User decision, 2026-11-07. Hilt was ruled out because it can't inject shared multiplatform code.
- Screen state comes from Kotlin (multiplatform) ViewModels. User decision, 2026-11-07.
- They are proper ViewModels: their state survives Android configuration changes such as rotation, without reloading or resetting the filter, search or open event. User decision, 2026-11-07.
- Clean architecture still holds: `domain` stays free of UI, data, platform and DI framework code. Use cases and repositories are plain classes that Koin builds, with no DI annotations inside `domain`.
- One Koin module per layer: data, domain (use cases) and presentation (ViewModels). Platform entry points only start Koin and show the app.
- The clock and time zone (`Europe/Lisbon`) are provided through Koin, so tests can swap them.
- Home's filter and search still survive opening an event and coming back.
- No visible change: every screen, transition and behavior in `aidd_docs/specs/home-screen.md` and `aidd_docs/specs/event-page.md` stays the same.
- Tests can swap any binding (for example a fake repository or a fixed clock) without touching production code.
- Every behavior this spec requires has an automated test, and that test passes. The existing tests keep passing.
- No code beyond what this spec calls for.

## Non-goals

- Hilt or any Android-only DI.
- New screens or features, and any visual change.
- A backend or real data source.
- Building and testing on iOS. That waits for macOS, as before.

## Done-when

- Android, iOS (compiled on macOS later) and Web start the app through Koin, and the app looks and behaves as before.
- Home and the event page each get their state from a ViewModel that Koin provides.
- No screen or entry point builds a repository, use case or state holder by hand.
- `domain` holds no DI framework code.
- A check verifies that the Koin graph resolves every ViewModel and use case.
- Home's filter and search survive opening an event and going back, on Android and Web.
- On Android, rotating the phone on Home or on an event page keeps the same screen and state: the filter, the search and the open event.
- Tests can replace the repository and the clock through Koin.
- Every existing test passes, and the new behavior is covered by passing tests.

## Open Questions

None

## Context

- Today the wiring is manual: `App` builds `SampleEventRepository`, the use cases and `HomeStateHolder` with `remember` (see `aidd_docs/memory/architecture.md`).
- Specs this must not break: `aidd_docs/specs/home-screen.md` and `aidd_docs/specs/event-page.md`.
