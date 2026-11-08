---
status: done
---

# Instruction: Log in and Create account screens

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
.
├── composeApp/src/commonMain/kotlin/app/homeout/
│   ├── Destinations.kt ✏️ LoginDestination, SignupDestination
│   ├── App.kt ✏️ start destination from GetSession, sign-in pops both, header fades
│   ├── AppHeader.kt ✏️ alpha and cleared semantics while hidden
│   ├── auth/
│   │   ├── AuthForm.kt ✅ the shared pieces: Google button, divider, alert, link button
│   │   ├── LoginScreen.kt ✅ LoginRoute + LoginScreen
│   │   ├── LoginViewModel.kt ✅
│   │   ├── SignupScreen.kt ✅ SignupRoute + SignupScreen
│   │   └── SignupViewModel.kt ✅
│   └── di/PresentationModule.kt ✏️ LoginViewModel, SignupViewModel
├── composeApp/src/commonTest/kotlin/app/homeout/
│   ├── auth/LoginViewModelTest.kt ✅
│   ├── auth/SignupViewModelTest.kt ✅
│   ├── auth/LoginScreenTest.kt ✅
│   ├── auth/SignupScreenTest.kt ✅
│   ├── auth/AuthFlowTest.kt ✅ App-level: start, transitions, header, sign-in paths
│   └── di/KoinGraphTest.kt ✏️ the two ViewModels
├── composeApp/src/androidDeviceTest/kotlin/app/homeout/SystemBackTest.kt ✏️ Signup → Login; Home leaves the app
└── androidApp/src/androidTest/kotlin/app/homeout/android/RecreationTest.kt ✏️ clears storage first, signs in; forms and session survive
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | `withKoin(signedIn = false)` and `App()` | Log in shows; the header's "HOMEOUT" and "Notifications" aren't in the tree | system |
| Happy path   | Create an account (Ana, ana@x.pt, 12345678, terms ticked) | Home on the Home tab, with the header shown | browser |
| Happy path   | Log in as an account created through `CreateAccount` | Home | browser |
| Edge case    | Fields: empty, `maya@example`, a 7-character password, terms unticked | Log in and Create account are disabled; they enable once every check holds | browser |
| Edge case    | Type `maya@example` and move focus away | The email border turns `error` and "Enter an email like you@example.com." shows. Typing a valid email clears it | browser |
| Edge case    | A 7-character password, then blur | Log in shows "At least 8 characters." in `error`; Create account turns its hint `error` | browser |
| Edge case    | Log in with a wrong password, or an unknown email | The mismatch alert shows and Log in stays. Editing either field removes it | browser |
| Edge case    | Create account with an existing email | The "already an account" alert shows. Log in instead opens Log in with that email filled in | browser |
| Edge case    | Continue with Google, or Sign up with Google | Home | browser |
| Edge case    | Create an account, Back to log in, the footer's Log in button, and system back on Signup | Signup pushes in at 360 ms; each back route returns to Log in | browser |
| Edge case    | System back on Home after signing in | The activity finishes | system |
| Edge case    | System back on Log in | The activity finishes | system |
| Edge case    | `withKoin(signedIn = true)` and `App()` | Home, with Log in never composed | system |
| Edge case    | Recreate the activity with Signup's fields filled | Name, email, password and terms are kept | system |
| Edge case    | Sign in, close the scenario and relaunch `MainActivity` | Home, not Log in | system |
| Edge case    | Forgot password? | Nothing changes | browser |
| Edge case    | Double-tap Create account, or Log in | One account, one Home entry: back from Home finishes the activity | browser |
| Edge case    | Recreate with the password typed | The password is kept (same ViewModel) and isn't in `SavedStateHandle` | system |
| Teardown     | `RecreationTest`'s first rule clears `homeout.accounts` before the activity launches | Each test starts signed out | system |

## Wireframe

```txt
┌───────────────────────────────┐   ┌───────────────────────────────┐
│ (1) status band, header hidden│   │ (1) status band, header hidden│
│                               │   │ (6) [←] CREATE ACCOUNT        │
│ (2) [HOMEOUT] large           │   │ (3) [  Sign up with Google  ] │
│     tagline                   │   │     ── or sign up with email ─│
│                               │   │ (8) Taken alert (when shown)  │
│ (3) [ Continue with Google  ] │   │ (7) Name                      │
│     ── or log in with email ──│   │     [__________________]      │
│ (4) Error alert (when shown)  │   │     Email                     │
│     Email                     │   │     [__________________]      │
│     [__________________]      │   │     error text                │
│     error text                │   │     Password                  │
│     Password  Forgot password?│   │     [__________________]      │
│     [__________________]      │   │     At least 8 characters.    │
│     error text                │   │     [x] I agree to the terms  │
│     [         Log in        ] │   │     [     Create account    ] │
│                               │   │                               │
│ (5) New to homeout? Create... │   │ (5) Already have an account?  │
└───────────────────────────────┘   └───────────────────────────────┘
```

1. Status band: the header is hidden and doesn't take taps or announce anything.
2. Brand: the 52 px logo and the tagline.
3. Google: the fake sign-in, then the divider.
4. Log in form: the mismatch alert above the fields, field errors under each field, then the main button.
5. Footer: the link to the other screen.
6. Signup top bar: back to Log in and the title.
7. Signup form: name, email, password with its hint, and the terms.
8. Taken alert, as the design places it: above the fields, with Log in instead.

## Tasks to do

### `1)` ViewModels

> Form state and sign-in, surviving recreation.

1. `LoginViewModel(handle, logIn, continueWithGoogle)`:
   - state as a plain `MutableStateFlow`: email, password, touched flags, `mismatch`, and derived `emailError`, `passwordError`, `canSubmit`
   - email, touched flags and `mismatch` saved in `SavedStateHandle`; the password in memory only
   - editing either field clears `mismatch`
   - `busy` while the hash runs: `submit()` is ignored and the button disabled, so a double tap can't sign in or navigate twice
   - `submit()` and `google()` emit one `signedIn` event on a `Channel`, collected once by the route
   - `prefill(email)` for Log in instead
2. `SignupViewModel(handle, createAccount, continueWithGoogle)`:
   - the same pattern, for name, email, password, terms and `taken`
   - `canSubmit` needs a non-blank name, a valid email, 8+ characters and the terms ticked
   - editing the email clears `taken`
   - the same `busy` guard and one-shot `signedIn` event as Log in, so a double tap can't create two accounts
3. Register both with `viewModelOf`, and add them to `KoinGraphTest`.
4. ViewModel unit tests with `Dispatchers.setMain`.

### `2)` Screens

> The design's Log in and Create account.

1. `AuthForm.kt`:
   - the Google button (52 px, `surface` fill, `divider` border, `HoGlyphs.Account`, `surfaceHover` when hovered)
   - the "or … with email" divider, hidden from accessibility
   - the alert box (`error` border, `errorContainer`, `HoGlyphs.AlertCircle`, live-region semantics)
   - the underlined `onPrimaryText` link button
2. `LoginScreen`: brand block, Google, divider, mismatch alert, Email and Password `HoTextField`s with the inert Forgot password? button, and a tinted Large "Log in" `HoButton` that's disabled until `canSubmit`. A footer pinned to the bottom. The column scrolls and takes `imePadding()`: with edge-to-edge on and no `windowSoftInputMode`, the keyboard would otherwise cover the fields and the button.
   - Each field's `TextFieldState` starts from the ViewModel's value, so a recreated screen shows what was typed. The password's state is a plain `remember`, per `HoTextField`. Changes stream back to the ViewModel.
3. `SignupScreen`:
   - the back button ("Back to log in") and the 36 px title
   - Google and the divider
   - the taken alert, with "Log in instead"
   - Name, Email and Password (with its hint) fields
   - `HoCheckbox` terms, the tinted "Create account" button, and the footer
4. Test tags: `login-page`, `signup-page`, `login-email`, `login-password`, `login-submit`, `login-mismatch`, `signup-name`, `signup-email`, `signup-password`, `signup-terms`, `signup-submit`, `signup-taken`.
5. Screen tests for every row of the Test Scope that doesn't need the `NavHost`.
6. Design check:
   - screenshot Log in, Create account and the three error states (mismatch, field errors, taken) on Android in light and dark
   - compare each with the design's `login`, `signup`, `login-error`, `signup-invalid` and `signup-taken` frames
   - fix each difference, or record why it stays

### `3)` Navigation and the header

> Log in first, then Home, and no way back.

1. Add `LoginDestination` and `SignupDestination`.
2. `App` reads `GetSession()` once (`remember`), and starts on `RootDestination` when there's a session, `LoginDestination` otherwise.
3. Signing in: `navigate(RootDestination) { popUpTo<LoginDestination> { inclusive = true } }`, so back from Home leaves the app.
4. Log in instead: take Log in's entry before popping, prefill its `LoginViewModel` (`koinViewModel(viewModelStoreOwner = loginEntry)`), as Pay does with `RootViewModel`, then pop Signup.
5. Navigate on `signedIn` only while the current destination is Login or Signup, so a late event after recreation can't push a second Home.
6. The header fades over 360 ms while the current destination is Login or Signup. Its space stays reserved, so the login screens' top padding is the design's 96 and 56 minus the header's 50. While hidden it takes no taps and its semantics are cleared.
7. `AuthFlowTest` for the App-level rows, including `mainClock.autoAdvance = false` for the 360 ms push.

### `4)` Device tests

> Prove back, recreation and the persisted session on Android.

1. `SystemBackTest`:
   - with `signedIn = false`: back from Signup returns to Log in, and back from Log in finishes the activity
   - after signing in: back on Home finishes the activity
2. `RecreationTest`:
   - a first rule clears `homeout.accounts` before `MainActivity` launches, through `getSharedPreferences(…).edit().clear().commit()` so the in-memory preferences are cleared too, not by deleting the file
   - the existing tests sign in with Continue with Google first
   - new: Signup's fields survive recreation
   - new: after signing in, closing and relaunching the activity opens on Home

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | The buttons enable only when every check holds. Field errors show only after blur. Alerts clear on edit. Fields and flags survive a new ViewModel from the same handle; the password isn't in the handle |
| 2 | Both screens show the design's copy, fields, alerts and states in light and dark. The disabled buttons ignore taps. The Android screenshots match the design's frames, or each difference is recorded |
| 3 | Signed out opens on Log in with no header; signed in opens on Home. Every sign-in path reaches Home, and Log in and Signup leave the back stack. Log in instead fills the email. Pushes take 360 ms |
| 4 | System back from Signup returns to Log in, and from Home or Log in finishes the activity. Form state survives recreation, and the session survives a relaunch |
