# homeout: Buying Tickets, the Tickets Tab and Ticket QR Codes

## Target

From an event page, people can choose how many tickets they want and how to pay, then find those tickets in a Tickets tab and open each ticket's QR code. All of it matches the "Event Management UI Library" design, version 1794071534-ba69 (https://claude.ai/artifact/1krt444a78B46kwb7jYipL, Home Screen page).

## Hard constraints

- Builds on `aidd_docs/specs/home-screen.md`, `aidd_docs/specs/event-page.md` and `aidd_docs/specs/dependency-injection.md`. Everything there still holds except what this spec replaces:
  - The ticket button on the event page now opens Buy tickets. It no longer does nothing.
  - The bottom navigation is Home, Tickets and Profile. Explore is removed.
- Navigation:
  - Event page → Buy tickets → (Pay or Reserve) → the Tickets tab.
  - Tickets tab → a ticket → its QR code.
  - Every new screen slides in from the right and its back returns the same way: the push transition, 360 ms, that the event page uses.
  - Back from Buy tickets returns to the event page. Back from the QR code returns to the Tickets tab.
  - On Android the system back gesture, and on Web the browser back button, do the same as each screen's own back button.
- Bottom navigation:
  - Home and Tickets switch between their tabs. Profile does nothing yet.
  - The current tab shows as selected, with the design's tinted icon.
  - Switching tabs plays the design's short fade-up.
  - While any ticket exists, the Tickets tab shows a badge with the total number of tickets.
- Home: each event row's thumbnail is the event's poster color with an icon for that event (music notes, a vinyl record, a fork and knife, a wine glass), as the design shows. It replaces the striped placeholder.
- Buy tickets:
  - A back button, the "Buy tickets" title, and a summary of the event: poster color, title, date and time, venue.
  - A "General admission" counter, from 1 to 8 tickets, starting at 1. The minus and plus buttons are disabled at the limits. "Up to 8 tickets per order."
  - Paid events: a choice of payment method among MB WAY, Credit or debit card, Apple Pay and Google Wallet, each with the design's note. MB WAY is chosen by default, and exactly one method is chosen at a time.
  - Free events: no payment method. Instead the note "This event is free. No payment needed: your tickets go straight to the Tickets tab."
  - A bar fixed at the bottom shows "<n> ticket(s) · Total" and the total, updated as the count changes, with a "Pay €<total>" button, or "Reserve <n> ticket(s)" for free events.
- Paying or reserving:
  - Records the order (event, number of tickets, payment method or free) and opens the Tickets tab.
  - Shows a confirmation, "Payment confirmed. <n> ticket(s) for <event> added.", or "Reserved. …" for free events.
  - The confirmation goes away when the person switches tab or opens a ticket.
- Tickets tab:
  - The "Your tickets" title.
  - A summary, "<n> tickets · <m> events", or "Nothing booked yet".
  - One card per event, in agenda order and in the event's poster colors. Each card shows the title, date and time, area, and the ticket count.
  - Every ticket is listed under its event's card: "Ticket <i> of <n>", "General admission · Paid with <method>" or "General admission · Free", and its code, `HO-<first four letters of the event id>-<three-digit number>`.
  - Buying more tickets for the same event adds them to that event's card.
  - With no tickets: "No tickets yet", "Tickets you buy show up here, grouped by event.", and a "Find events" button that switches to the Home tab.
- Ticket QR code:
  - The "Your ticket" title and "<event> · Ticket <i> of <n>".
  - One card per ticket of that event, opening on the ticket that was tapped. Each card has the event in its poster colors, a QR code on white (readable in dark mode), the ticket code, and the ticket's label and detail.
  - With several tickets: swipe sideways between them, with dots and previous and next arrows that are disabled at the ends. The hint reads "Swipe sideways for your other tickets. Show each code at the entrance.", or "Show this code at the entrance." for a single ticket.
  - The QR pattern is a placeholder derived from the ticket code, until real codes come from a backend.
- Payment is simulated: choosing a method and paying records the order without contacting any payment provider.
- Orders live only while the app runs, as in the design's prototype. They're gone after the app is closed. User decision, 2026-11-07.
- The Tickets tab starts empty, with no sample orders. User decision, 2026-11-07.
- New colors in the design (the badge text on the highlight color) are added to the design system and meet WCAG AA in both themes, like every other text color.
- Clean architecture and Koin still hold: orders and tickets are domain concepts, provided through Koin, and each new screen gets its state from a ViewModel that survives activity recreation.
- The design is the source of truth. Any visual change is made in the design first, then in the app.
- Every behavior this spec requires has an automated test, and that test passes.
- No code, copy, screens or UI elements beyond what this spec and the design call for.

## Non-goals

- Real payments, payment providers, card entry or wallet integration.
- Keeping tickets after the app is closed. That comes with the backend.
- Real ticket codes or scannable QR codes from a backend.
- The Profile tab and the notifications screen.
- Share and Save on the event page, which still do nothing.
- Refunds, cancelling or transferring tickets.
- Building, running and testing on iOS. That waits for macOS.

## Done-when

- On Android and Web, in light and dark, Home, the event page, Buy tickets, the Tickets tab (with tickets and empty) and the QR code screen match the design.
- Buying 2 tickets for a paid event with Google Wallet shows "Payment confirmed. 2 tickets for <event> added." on the Tickets tab, with the event's card listing "Ticket 1 of 2" and "Ticket 2 of 2", each "Paid with Google Wallet".
- Reserving a free event lists its tickets as "General admission · Free", with "Reserve" wording and no payment step.
- The counter can't go below 1 or above 8, and the total follows the count.
- Buying more tickets for an event that already has some adds them to the same card, and the Tickets badge shows the new total.
- Tapping a ticket opens its QR code on that ticket. Swiping, or using the arrows, moves between that event's tickets.
- Back, system back and browser back each return to the previous screen with the push transition.
- With no tickets, the Tickets tab shows the empty state, and Find events switches to Home.
- Every text color, including the badge, meets WCAG AA in both themes.
- Every required behavior is covered by a passing automated test.

## Open Questions

None

## Context

- Design source: the "Event Management UI Library" canvas (https://claude.ai/artifact/1krt444a78B46kwb7jYipL), version 1794071534-ba69, read on 2026-11-07. Its Home Screen page now covers Home, Event, Buy tickets, Tickets and Ticket QR code. The UI Library page describes each of them.
- This replaces two points of `aidd_docs/specs/event-page.md`: the ticket button no longer does nothing, and the bottom navigation no longer includes Explore.
