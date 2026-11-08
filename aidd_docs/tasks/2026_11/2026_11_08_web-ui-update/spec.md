# Web edition UI update

## Target

The Web edition is the updated homeout website design: a single scrolling page with a navigation bar, hero, app showcase, next events, how it works, a get-the-app call to action and a footer, replacing the previous Web screens.

## Hard constraints

- The updated design ("homeout Website" page of the Event Management UI Library canvas) is the source of truth. A close match is acceptable.
- It works at desktop, tablet and phone widths, as the design does.
- Web always uses the design's single look, whatever the visitor's light or dark setting.
- Text keeps WCAG AA contrast on its background.
- Motion (hero fade, panel growth, poster fan, reveals) is off when the visitor prefers reduced motion, and the page stays fully readable without it.
- The next-events section is fed by the app's events, which are the mocked sample events for now, not the design's fixed samples.
- The phone app (Android, iOS) looks and behaves unchanged. The new styling (pill buttons, large rounded sections, scroll motion) exists on Web only.
- Nothing on Web offers buying or accounts: every ticket, download or "see all" action leads to getting the app.

## Non-goals

- Changing the phone app's screens or shared styling.
- Keeping the previous Web Home (search, filters, day groups) or event page.
- Web accounts, tabs or in-browser buying.
- Linking the App Store and Google Play badges to real stores.
- Terms and Privacy: the footer omits them.
- A backend for real events.

## Done-when

- Each section of the design appears on Web in the same order, with the same copy, and a close visual match at desktop, tablet and phone widths.
- The next-events section lists the app's upcoming events, each with its category, time, price and a Get tickets action.
- Navigation links reach their section; every ticket, download and "see all" action reaches the get-the-app section.
- The footer shows no Terms or Privacy link.
- The previous Web Home and event page are no longer reachable.
- The phone app is visually and behaviorally unchanged.

## Open Questions

None.

## Context

- Design source: the Claude Design canvas "Event Management UI Library", page "homeout Website" (artboard e664ab12b053). A visual change goes into the design first, then the app.
