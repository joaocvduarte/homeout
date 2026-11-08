# homeout: Log in and Create account

## Target

On Android and iOS, people must have an account, created and checked on the phone, before they reach Home, through Log in and Create account screens matching the "Event Management UI Library" design, version 1794130040-56ab (https://claude.ai/artifact/1krt444a78B46kwb7jYipL, Home Screen page, `login` and `signup`); Web stays open for browsing events without an account.

## Hard constraints

- Builds on `aidd_docs/specs/home-screen.md`, `aidd_docs/specs/event-page.md`, `aidd_docs/specs/tickets.md` and `aidd_docs/specs/dependency-injection.md`. Everything there still holds except what this spec replaces:
  - On Android and iOS, the app opens on Log in, unless someone is already signed in.
  - On Web, there's no Log in, no buying and no Tickets tab. Buy tickets sends the person to the app.
- Accounts (Android and iOS):
  - Signing in is real, not simulated: Create account records an account on the phone, and Log in only succeeds with the email and password of an account recorded there. User decision, 2026-11-08.
  - Accounts are kept in the phone's storage, with no backend. User decision, 2026-11-08.
  - The password is never stored as typed.
  - Whoever signs in stays signed in across app restarts, and the next launch opens on Home. User decision, 2026-11-08.
- Shared by both screens:
  - The flower border stays behind them. The app header (logo and notifications) is hidden, and fades in when the person reaches Home.
  - Log in and Create account sit before the app. Going from one to the other, and from either to Home, uses the design's push transition: 360 ms, the new screen in from the right, the one behind easing 28% left and fading.
  - The main button stays disabled while any field is empty, the email isn't a valid address, or the password has fewer than 8 characters. User decision, 2026-11-08.
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
- Back:
  - Back from Create account returns to Log in, the same way, including the Android system back gesture.
  - Back from Home, and from Log in, leaves the app. Signing in removes Log in and Create account from the back stack. User decision, 2026-11-08.
- Reaching Home opens it on the Home tab.
- New colors in the design (field, field border, the hover surfaces) are added to the design system. Every text color, including placeholders and the password hint, meets WCAG AA in both themes.
- Fields carry visible labels, and the password hint is announced with its field.
- Clean architecture and Koin still hold: accounts are a domain concept provided through Koin, and each new screen gets its state from a ViewModel that survives activity recreation.
- The design is the source of truth. Any visual change, including the Web layout without buying or Tickets, is made in the design first, then in the app.
- Every behavior this spec requires has an automated test, and that test passes.
- No code, copy, screens or UI elements beyond what this spec and the design call for.

## Non-goals

- A backend, or accounts shared between devices.
- Password reset. Forgot password stays inert.
- Terms of Service and Privacy Policy pages.
- Log out, the Profile tab, and editing the account.
- Email verification.
- Accounts and sign-in on Web.
- Building, running and testing on iOS. That waits for macOS.

## Done-when

- On Android, in light and dark, Log in and Create account match the design.
- A first launch on Android shows Log in, with no app header.
- Create an account opens Create account with the push transition. Its back button and system back each return to Log in.
- Log in and Create account stay disabled with an empty field, an invalid email, or a password under 8 characters, and enable once all three hold.
- Creating an account leads to Home, on the Home tab, with the app header shown.
- After a restart, the person who signed in lands on Home without logging in again.
- Logging in with an email and password that match no account on the phone doesn't reach Home.
- System back from Home leaves the app.
- On Web, the app opens on Home with no Log in, shows no Tickets tab, and Buy tickets sends the person to the app.
- Every text color on both screens meets WCAG AA in both themes.
- Every required behavior is covered by a passing automated test.

## Open Questions

- TBD: What do "Continue with Google" and "Sign up with Google" do, now that sign-in is real: a real Google sign-in, or nothing yet like Forgot password?
- TBD: What does the person see when Log in fails (no account for that email, or a wrong password), and when Create account uses an email that already has an account? The design has no error states or copy.
- TBD: Does Create account also stay disabled until the Name field is filled and the terms checkbox is ticked?
- TBD: Where does Buy tickets on Web send the person, given the app isn't in any store yet: a store page, a download page, or a deep link that opens the event in the installed app?
- TBD: On Web, with no Tickets tab, what replaces the bottom navigation: only Home, or no bar at all?

## Context

- Design source: the "Event Management UI Library" canvas (https://claude.ai/artifact/1krt444a78B46kwb7jYipL), version 1794130040-56ab, read on 2026-11-08. The UI Library page adds a "Log in and create account" section: "The first screens of the app, under the flower border with the logo large … Logging in or creating an account slides into Home and brings in the app header." The design doesn't yet show a Web layout without buying or Tickets.
- The previous plan cited version 1794071534-ba69. Only the login and signup screens are checked here as new; other pages weren't diffed.
- This replaces part of `aidd_docs/specs/tickets.md` on Web: buying, the Tickets tab and the QR code are mobile-only.
