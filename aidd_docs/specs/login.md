# homeout: Log in and Create account

## Target

On Android and iOS, people must have an account, created and checked on the phone, before they reach Home, through Log in and Create account screens matching the "Event Management UI Library" design, version 1794131199-48a3 (https://claude.ai/artifact/1krt444a78B46kwb7jYipL, Home Screen page, `login`, `signup` and their error states); Web stays open for browsing events without an account.

## Hard constraints

- Builds on `aidd_docs/specs/home-screen.md`, `aidd_docs/specs/event-page.md`, `aidd_docs/specs/tickets.md` and `aidd_docs/specs/dependency-injection.md`. Everything there still holds except what this spec replaces:
  - On Android and iOS, the app opens on Log in, unless someone is already signed in.
  - On Web, there's no Log in, no buying and no Tickets tab. Buy tickets opens Get the app.
- Accounts (Android and iOS):
  - Signing in with email is real, not simulated: Create account records an account on the phone, and Log in only succeeds with the email and password of an account recorded there. User decision, 2026-11-08.
  - "Continue with Google" and "Sign up with Google" are fake for now: each goes straight to Home, with no Google prompt. User decision, 2026-11-08.
  - Accounts are kept in the phone's storage, with no backend. User decision, 2026-11-08.
  - The password is never stored as typed.
  - Whoever signs in stays signed in across app restarts, and the next launch opens on Home. User decision, 2026-11-08.
- Shared by both screens:
  - The flower border stays behind them. The app header (logo and notifications) is hidden, and fades in when the person reaches Home.
  - Log in and Create account sit before the app. Going from one to the other, and from either to Home, uses the design's push transition: 360 ms, the new screen in from the right, the one behind easing 28% left and fading.
  - The main button stays disabled while any field is empty, the email isn't a valid address, or the password has fewer than 8 characters. Create account also stays disabled until the terms are ticked. User decisions, 2026-11-08.
  - After the email field loses focus, an invalid email turns its border to the error color and shows "Enter an email like you@example.com." After the password field loses focus, a password under 8 characters turns its border to the error color: on Log in with the message "At least 8 characters.", on Create account by turning the existing hint to the error color.
- Log in:
  - The homeout logo, large, and "Find what's on in Lisbon tonight, and keep every ticket in one place."
  - A "Continue with Google" button with the Google mark.
  - The divider "or log in with email".
  - An Email field (placeholder "you@example.com") and a Password field, with a "Forgot password?" button beside the Password label. Forgot password does nothing yet, as in the design.
  - A "Log in" button.
  - When the email and password match no account on the phone, an alert above the form reads "That email and password don't match an account on this phone. Check them, or create an account." It goes away once either field changes.
  - At the bottom: "New to homeout?" and a "Create an account" button that opens Create account.
- Create account:
  - A back button ("Back to log in") and the "Create account" title.
  - A "Sign up with Google" button with the Google mark.
  - The divider "or sign up with email".
  - Name (placeholder "Maya Almeida"), Email (placeholder "you@example.com") and Password fields, the Password field with the hint "At least 8 characters."
  - A checkbox: "I agree to the homeout Terms of Service and Privacy Policy."
  - A "Create account" button.
  - When an account already exists for that email on the phone, an alert above the form reads "There's already an account for this email on this phone." with a "Log in instead" button that opens Log in with that email filled in. The alert goes away once the email changes.
  - At the bottom: "Already have an account?" and a "Log in" button that returns to Log in.
- Back:
  - Back from Create account returns to Log in, the same way, including the Android system back gesture.
  - Back from Home, and from Log in, leaves the app. Signing in removes Log in and Create account from the back stack. User decision, 2026-11-08.
- Reaching Home opens it on the Home tab.
- Web:
  - It opens on Home. The header shows only the logo, with no notifications or account, and there's no bottom navigation.
  - Home and the event page are as in the app. Buy tickets (or Get free tickets) on the event page opens Get the app with the push transition, and its back returns to the event.
  - Get the app: a back button, the "Get the app" title, the event summary (poster color, title, date and time, venue), "Tickets live in the homeout app", "Download the app to buy tickets for this event and keep each ticket's QR code on your phone.", and "Download on the App Store" and "Get it on Google Play" badges.
  - The badges are placeholders that go nowhere yet. The store links come later. User decision, 2026-11-08.
- New colors in the design (field, field border, the hover surfaces, error and its tint) are added to the design system. Every text color, including placeholders and the password hint, meets WCAG AA in both themes.
- Fields carry visible labels, and the password hint is announced with its field.
- Clean architecture and Koin still hold: accounts are a domain concept provided through Koin, and each new screen gets its state from a ViewModel that survives activity recreation.
- The design is the source of truth. Any visual change is made in the design first, then in the app.
- Every behavior this spec requires has an automated test, and that test passes.
- No code, copy, screens or UI elements beyond what this spec and the design call for.

## Non-goals

- A backend, or accounts shared between devices.
- Real Google sign-in.
- Real App Store and Google Play links.
- Password reset. Forgot password stays inert.
- Terms of Service and Privacy Policy pages.
- Log out, the Profile tab, and editing the account.
- Email verification.
- Accounts and sign-in on Web.
- Building, running and testing on iOS. That waits for macOS.

## Done-when

- On Android, in light and dark, Log in, Create account and their error states match the design.
- On Web, in light and dark, Home without the tab bar and Get the app match the design.
- A first launch on Android shows Log in, with no app header.
- Create an account opens Create account with the push transition. Its back button and system back each return to Log in.
- Log in and Create account stay disabled with an empty field, an invalid email, or a password under 8 characters, and Create account also while the terms are unticked. They enable once every check holds.
- An invalid email or a short password shows its field error after the field loses focus.
- Creating an account leads to Home, on the Home tab, with the app header shown.
- Creating a second account with the same email shows the "already an account" alert, and Log in instead opens Log in with that email filled in.
- Logging in with an email and password that match no account on the phone shows the mismatch alert and stays on Log in. The matching email and password reach Home.
- Either Google button reaches Home.
- After a restart, the person who signed in lands on Home without logging in again.
- System back from Home leaves the app.
- On Web, the app opens on Home with no Log in, no account in the header and no tab bar. Buy tickets opens Get the app, whose back returns to the event.
- Every text color on both screens meets WCAG AA in both themes.
- Every required behavior is covered by a passing automated test.

## Open Questions

None

## Context

- Design source: the "Event Management UI Library" canvas (https://claude.ai/artifact/1krt444a78B46kwb7jYipL), version 1794131199-48a3, updated on 2026-11-08. The UI Library page has "Log in and create account", "Log in and create account: errors" and "Web" sections. In its Play mode, maya@example.com with lisbon2026 is an existing account. That account is prototype data only: the app starts with no accounts.
- The previous plan cited version 1794071534-ba69. Only the login, signup, error and Web screens are checked here as new; other pages weren't diffed.
- This replaces part of `aidd_docs/specs/tickets.md` on Web: buying, the Tickets tab and the QR code are mobile-only.
