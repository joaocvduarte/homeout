# homeout: Log in and Create account

## Target

The app opens on a Log in screen, with a Create account screen beside it, and either one leads into Home, matching the "Event Management UI Library" design, version 1794130040-56ab (https://claude.ai/artifact/1krt444a78B46kwb7jYipL, Home Screen page, `login` and `signup`).

## Hard constraints

- Builds on `aidd_docs/specs/home-screen.md`, `aidd_docs/specs/event-page.md`, `aidd_docs/specs/tickets.md` and `aidd_docs/specs/dependency-injection.md`. Everything there still holds except what this spec replaces:
  - The app no longer opens on Home. It opens on Log in.
- Shared by both screens:
  - The flower border stays behind them. The app header (logo and notifications) is hidden, and fades in when the person reaches Home.
  - Log in and Create account sit before the app. Going from one to the other, and from either to Home, uses the design's push transition: 360 ms, the new screen in from the right, the one behind easing 28% left and fading.
- Log in:
  - The homeout logo, large, and "Find what's on in Lisbon tonight, and keep every ticket in one place."
  - A "Continue with Google" button with the Google mark.
  - The divider "or log in with email".
  - An Email field (placeholder "you@example.com") and a Password field, with a "Forgot password?" button beside the Password label. Forgot password does nothing yet, as in the design.
  - A "Log in" button.
  - At the bottom: "New to homeout?" and a "Create an account" button that opens Create account.
- Create account:
  - A back button ("Back to log in") and the "Create account" title.
  - A "Sign up with Google" button with the Google mark.
  - The divider "or sign up with email".
  - Name (placeholder "Maya Almeida"), Email (placeholder "you@example.com") and Password fields, the Password field with the hint "At least 8 characters."
  - A checkbox: "I agree to the homeout Terms of Service and Privacy Policy."
  - A "Create account" button.
  - At the bottom: "Already have an account?" and a "Log in" button that returns to Log in.
- Back from Create account returns to Log in, the same way. On Android the system back gesture, and on Web the browser back button, do the same.
- Reaching Home opens it on the Home tab.
- New colors in the design (field, field border, the hover surfaces) are added to the design system. Every text color, including placeholders and the password hint, meets WCAG AA in both themes.
- Fields carry visible labels, and the password hint is announced with its field.
- Clean architecture and Koin still hold: each new screen gets its state from a ViewModel that survives activity recreation.
- The design is the source of truth. Any visual change is made in the design first, then in the app.
- Every behavior this spec requires has an automated test, and that test passes.
- No code, copy, screens or UI elements beyond what this spec and the design call for.

## Non-goals

- Password reset. Forgot password stays inert.
- Terms of Service and Privacy Policy pages.
- Log out, the Profile tab, and editing the account.
- Email verification.
- Building, running and testing on iOS. That waits for macOS.

## Done-when

- On Android and Web, in light and dark, Log in and Create account match the design.
- Launching the app shows Log in, with no app header.
- Create an account opens Create account with the push transition. Its back button, system back and browser back each return to Log in.
- Log in, Continue with Google, Sign up with Google and Create account each lead to Home, on the Home tab, with the app header shown.
- Every text color on both screens meets WCAG AA in both themes.
- Every required behavior is covered by a passing automated test.

## Open Questions

- TBD: Is signing in real or simulated? There's no backend, and the design's prototype lets any input through, like the simulated payment. If simulated, do Google buttons skip any Google prompt?
- TBD: Which checks gate Log in and Create account: filled fields, a valid email, a password of 8 or more characters, the terms checkbox ticked? The design shows no error states or error copy, so if any check applies, what does a failure look like?
- TBD: Once signed in, what do system back and browser back do from Home: leave the app (Android) and stay put (Web), or return to Log in?
- TBD: Is the signed-in state kept between launches, or does every launch start on Log in, as orders are lost on close?
- TBD: On Web, does a direct event link (`#…EventDestination/<id>`) open the event without logging in, or go to Log in first?

## Context

- Design source: the "Event Management UI Library" canvas (https://claude.ai/artifact/1krt444a78B46kwb7jYipL), version 1794130040-56ab, read on 2026-11-08. The UI Library page adds a "Log in and create account" section: "The first screens of the app, under the flower border with the logo large … Logging in or creating an account slides into Home and brings in the app header."
- The previous plan cited version 1794071534-ba69. Only the login and signup screens are checked here as new; other pages weren't diffed.
