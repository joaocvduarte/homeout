# homeout: Event Page

## Target

Tapping an event on Home opens that event's page, and the page matches the Event page in the "Home and event screens" design (https://claude.ai/artifact/1krt444a78B46kwb7jYipL, page Home Screen).

## Hard constraints

- Builds on the shipped Home screen. Everything in `aidd_docs/tasks/2026_11/2026_11_06_homeout-event-app/spec.md` still holds: platforms, clean architecture, sample data only, Lisbon, €, English, the foundations, and the theme following the phone's light or dark setting.
- Every event row on Home opens the page of that event, and only that event.
- The page opens with the design's push transition: the event page slides in from the right while Home eases left and fades out. Going back reverses it.
- The app header (homeout wordmark, notifications icon, avatar) stays at the top on the event page. The bottom navigation is not shown on the event page.
- The event page matches the design. From top to bottom it shows:
  - floating Back, Share and Save buttons over the top of the page
  - a poster in the event's own colors, with a flower from the homeout logo, "homeout presents", the title in large uppercase, and a ruled line with the poster date and the area
  - the category and day label, with the day in the accent color for tonight's events and the muted color otherwise, then the title
  - the date and times line, and the full venue
  - a divider
  - "About this event" and the event's description
  - a bar fixed at the bottom with the price, a price note, and the ticket button
- The page scrolls while the bottom bar stays in place.
- Paid events show "per person" and a "Buy tickets" button. Free events show "Free", "Ticket required" and a "Get free tickets" button.
- The ticket button does nothing when tapped. User decision, 2026-11-07.
- The Share button does nothing when tapped. User decision, 2026-11-07.
- The Save button does nothing when tapped. User decision, 2026-11-07.
- Back returns to Home as it was: the same category filter and search. The page's Back button, the Android system back and the browser back on Web all do this. User decision, 2026-11-07.
- Each sample event carries the details the page shows: poster date, date and times line, full venue, poster colors and description, with the values the design gives.
- The design is the source of truth. Any visual change is made in the design first, then in the app.
- Every behavior this spec requires has an automated test, and that test passes.
- No code, copy, screens or UI elements beyond what this spec and the design call for.

## Non-goals

- Buying or reserving tickets, and the ticket-buying page. They come later.
- Sharing an event, for example via WhatsApp or social media. It comes later.
- Saving an event to a section of the user's profile. It comes later.
- Page details for events other than the four the design gives. They come later from the backend. User decision, 2026-11-07.
- The Explore, Tickets, Profile and notifications screens.
- Building, running and testing on iOS. That happens later on macOS.
- A backend, accounts or real event data.
- Opening an event page from a link or from anywhere other than Home.

## Done-when

- On Android and Web, tapping any event on Home opens its page, and the page matches the design's Event page, in light and dark.
- The page shows the tapped event's own title, category, day, times, venue, description, price and poster colors.
- A paid event shows "Buy tickets" and "per person". A free event shows "Get free tickets" and "Ticket required".
- Tapping the ticket, Share or Save button changes nothing on screen.
- The page's Back button, the Android system back and the browser back on Web each return to Home with the same category and search as before the event was opened.
- The event page opens and closes with the push transition the design shows.
- Every text color on the event page, including on each poster, meets WCAG AA contrast (4.5:1, or 3:1 for large text) against the background it sits on, in both themes.
- Every required behavior is covered by a passing automated test.

## Open Questions

None

## Context

- Design source: the "Event Management UI Library" canvas (https://claude.ai/artifact/1krt444a78B46kwb7jYipL), page Home Screen, now titled "Home and event screens", version 1794062441-00bd, read on 2026-11-07. It adds the Event page, reached by tapping an event row on Home A.
- Parent spec: `aidd_docs/tasks/2026_11/2026_11_06_homeout-event-app/spec.md`. Its non-goal "the ticket-buying page that an event will open" still holds; this spec only adds the event page in front of it.
