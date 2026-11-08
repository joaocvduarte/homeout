# Forms

## Approach

- No form library. Home's search is a `BasicTextField` with `rememberTextFieldState`, which streams its text to `HomeStateHolder.onQueryChange`.
- Log in and Create account use `HoTextField` (a label, placeholder, error or hint) and `HoCheckbox`. Each field's `TextFieldState` is a plain `remember` seeded from the ViewModel and kept in step with it, so a prefilled email can't be overwritten by a saved copy. The password's state is never saved.

## Conventions

- Search filters as you type, with no submit step. A blank query matches everything.
- The account forms have a submit button that stays disabled until every check holds: a non-blank name (Create account), a valid email, a password of 8 or more characters, and the terms (Create account).
- A field's error shows only after it loses focus and holds something wrong; an empty field shows none. Alerts (wrong login, taken email) clear when the input changes. Submitting is guarded so a double tap acts once.
- The keyboard's Next action moves between fields.
