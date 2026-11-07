# Forms

## Approach

- No form library. The only input is Home's search, a `BasicTextField` with `rememberTextFieldState`, which streams its text to `HomeStateHolder.onQueryChange`.

## Conventions

- Inputs filter as you type, with no submit step. A blank query matches everything.
