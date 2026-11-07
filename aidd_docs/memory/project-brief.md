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

## Key features

- Home: upcoming events by day, with search over title, venue and artists, and an All / Music / Food filter.
- Event page: opened by tapping an event, with the poster, times, venue and description, and a ticket bar.
- The theme follows the phone's light or dark setting.
- The ticket, Share and Save buttons and the Explore, Tickets and Profile tabs don't do anything yet.
