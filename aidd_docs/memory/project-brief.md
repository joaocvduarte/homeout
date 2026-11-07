# Project Brief

## What it is

- homeout: an app where people in Lisbon find upcoming music and food events, for Android, iOS and Web.

## Why it exists

- One place to see what's on near you tonight and this week, filtered by music or food.
- Built for people attending events. Organizer and event-management features are out of scope.

## Domain language

| Term | Meaning |
| ---- | ------- |
| Event | Something to attend, always in exactly one category, with a venue, start time and price |
| Category | `MUSIC` or `FOOD`, nothing else |
| Venue / area | `venue` is the neighbourhood shown on Home ("Alfama"); `venueName` is the place itself ("Cisterna do Largo") |
| Tonight | An event whose local date is today in Lisbon time, highlighted in the tertiary color |
| Day group | Home's list, grouped by local day |
| Poster | The event page's colored header, its colors carried by the event as data |
| Order | 1 to 8 tickets for one event, with a payment method, or none when the event is free |
| Ticket | One place in an order, numbered per event ("Ticket 2 of 3") with a code `HO-<first 4 letters of the event id>-<nnn>` |
| Payment method | MB WAY, card, Apple Pay or Google Wallet. Payment is simulated, and no provider is contacted. |

## Key features

- Home: upcoming events by day, with search over title, venue and artists, and an All / Music / Food filter.
- Event page: opened by tapping an event, with the poster, times, venue, description and a ticket bar.
- Buy tickets: a 1-to-8 counter and a payment method, or a reservation for a free event. It lands on the Tickets tab with a confirmation.
- Tickets tab: tickets grouped by event, with a count badge on the tab. Each ticket opens its QR code, swipeable between that event's tickets.
- The theme follows the phone's light or dark setting.
- Orders live only while the app runs, and the QR codes are placeholders. Both wait for a backend.
- Share, Save, notifications and the Profile tab don't do anything yet.
