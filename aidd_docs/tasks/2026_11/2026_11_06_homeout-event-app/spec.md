# homeout: Music and Food Event App

## Target

Ship the Home screen of homeout, a new app where people find upcoming music and food events. It must match variant A of the Home screen in the "Event Management UI Library" design (https://claude.ai/artifact/1krt444a78B46kwb7jYipL).

## Hard constraints

- New project, started from scratch.
- Built with Kotlin Multiplatform, from one shared codebase, for Android, iOS and Web, the targets the design names.
- iOS: the target and its app entry are set up, and all logic and UI are shared with it. It is built and tested later, on a Mac or a macOS CI runner, because this run's machine is Windows and cannot build iOS. User decision, 2026-11-06.
- Uses clean architecture: domain rules stay independent of UI, data sources and platform. The user requires this.
- For people attending events. Organizer or event-management features are not part of it.
- Events come in exactly two categories, music and food, and every event belongs to one of them.
- Events come from local sample data. There is no backend. Each event has its artists, as real events from the future backend will.
- City, currency and language are as the design shows them: Lisbon, €, English.
- The theme follows the phone's light or dark setting.
- The look matches the design's foundations:
  - The color tokens follow the design: background, surface, onSurface, onSurfaceVariant, outline, primary, onPrimaryText, secondaryContainer, onSecondaryContainer, brand and tertiary. Each has the hex value the design gives for dark and for light.
  - The type scale follows the design: 15 roles from Display Large to Label Small. Display and Headline roles use Big Shoulders Stencil Display at weight 800, in uppercase. All other roles use Inter.
  - Buttons come in three variants: Primary (outlined accent), Secondary (tonal) and Outline (neutral). Each has default, pressed and disabled states, plus an icon-only form. Sizes are Large 52, Medium 44 and Small 32 (Small is web only). A full-width block form exists too.
- The Home screen matches variant A in the design. From top to bottom it shows:
  - the homeout wordmark, a notifications icon and an avatar
  - the "Upcoming near you" heading
  - the event search
  - the All / Music / Food category filter
  - upcoming events grouped by day, each showing its title, category, venue, time and price
  - the bottom navigation: Home, Explore, Tickets, Profile
- The design is the source of truth. Any visual change is made in the design first, then in the app.
- Every behavior the spec requires has an automated test, and that test passes.
- No code, copy, screens or UI elements beyond what this spec and the design call for. Placeholder or filler content counts.

## Non-goals

- The Explore, Tickets, Profile and notifications screens. They come later.
- The ticket-buying page that an event will open. It comes later.
- Building, running and testing on iOS. That happens later on macOS.
- Home variant B.
- A date-range control and an extra filter button. Both were removed from the design.
- A backend, accounts or real event data.
- Other cities, currencies or languages.
- Event categories other than music and food.

## Done-when

- On Android and Web, the Home screen matches design variant A, in light and dark.
- The iOS target and app entry exist and share all logic and UI. Building and verifying them waits for macOS.
- The theme switches when the phone's setting switches.
- Every color token, type role and button variant, state and size that the design defines is available in the app and looks the way the design shows it.
- Home lists the upcoming sample events, grouped by day, and each event shows its category.
- Picking Music or Food shows only events in that category. Picking All shows every event.
- A search shows the events whose title, venue or one of whose artists matches the query.
- With Music or Food picked, a search shows only matching events in that category.
- Every required behavior is covered by a passing automated test.

## Open Questions

None

## Context

- Design source: the "Event Management UI Library" canvas (https://claude.ai/artifact/1krt444a78B46kwb7jYipL), with the pages homeout UI Library, Button Set and Home Screen. It was exported from Claude Design on 2026-11-06. It was updated on 2026-11-06: the category filter was added to Home A, and the date range and filter button were removed.
