package app.homeout.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoButton
import app.homeout.designsystem.HoButtonSize
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTextField
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole

/** Log in: collects the ViewModel, and reports when someone has signed in. */
@Composable
fun LoginRoute(viewModel: LoginViewModel, onSignedIn: () -> Unit, onCreateAccount: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val signedIn by rememberUpdatedState(onSignedIn)
    LaunchedEffect(viewModel) { viewModel.signedIn.collect { signedIn() } }
    LoginScreen(
        state = state,
        email = rememberFieldState(state.email, viewModel::setEmail),
        password = rememberFieldState(state.password, viewModel::setPassword),
        onEmailBlur = viewModel::touchEmail,
        onPasswordBlur = viewModel::touchPassword,
        onLogIn = viewModel::submit,
        onGoogle = viewModel::google,
        onCreateAccount = onCreateAccount,
    )
}

@Composable
internal fun LoginScreen(
    state: LoginState,
    email: TextFieldState,
    password: TextFieldState,
    onEmailBlur: () -> Unit,
    onPasswordBlur: () -> Unit,
    onLogIn: () -> Unit,
    onGoogle: () -> Unit,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HoTheme.colors
    Column(modifier.fillMaxSize().imePadding()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .testTag("login-page")
                // The design puts the logo 96 below the status band; the hidden header already holds 50 of that.
                .padding(start = 24.dp, end = 24.dp, top = 46.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BigLogo()
                HoText(
                    "Find what's on in Lisbon tonight, and keep every ticket in one place.",
                    HoTypeRole.TitleLarge,
                    modifier = Modifier.semantics { heading() },
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                GoogleButton("Continue with Google", onGoogle, Modifier.testTag("login-google"))
                OrDivider("or log in with email")
                if (state.mismatch) {
                    ErrorAlert(
                        "That email and password don't match an account on this phone. Check them, or create an account.",
                        Modifier.testTag("login-mismatch"),
                    )
                }
                HoTextField(
                    label = "Email",
                    state = email,
                    modifier = Modifier.testTag("login-email"),
                    placeholder = "you@example.com",
                    error = state.emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    onFocusLost = onEmailBlur,
                )
                HoTextField(
                    label = "Password",
                    state = password,
                    modifier = Modifier.testTag("login-password"),
                    password = true,
                    error = state.passwordError,
                    labelTrailing = {
                        // Forgot password does nothing yet, as in the design.
                        HoText(
                            "Forgot password?",
                            HoTypeRole.LabelLarge,
                            color = colors.onPrimaryText,
                            style = TextStyle(fontSize = 13.sp),
                            modifier = Modifier.testTag("login-forgot").clickable(role = Role.Button, onClick = {}),
                        )
                    },
                    onFocusLost = onPasswordBlur,
                )
                HoButton(
                    "Log in",
                    onClick = onLogIn,
                    modifier = Modifier.padding(top = 4.dp).testTag("login-submit"),
                    size = HoButtonSize.Large,
                    block = true,
                    tinted = true,
                    enabled = state.canSubmit,
                )
            }
        }
        AuthFooter("New to homeout?", "Create an account", onCreateAccount, "login-create")
    }
}
