package app.homeout.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoButton
import app.homeout.designsystem.HoButtonSize
import app.homeout.designsystem.HoCheckbox
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTextField
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.event.RoundButton

/**
 * Create account: collects the ViewModel. [onSignedIn] fires once an account is created or Google signs in;
 * [onLoginInstead] receives the taken email.
 */
@Composable
fun SignupRoute(
    viewModel: SignupViewModel,
    onBack: () -> Unit,
    onSignedIn: () -> Unit,
    onLoginInstead: (email: String) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val signedIn by rememberUpdatedState(onSignedIn)
    LaunchedEffect(viewModel) { viewModel.signedIn.collect { signedIn() } }
    SignupScreen(
        state = state,
        name = rememberFieldState(state.name, viewModel::setName),
        email = rememberFieldState(state.email, viewModel::setEmail),
        password = rememberFieldState(state.password, viewModel::setPassword),
        onEmailBlur = viewModel::touchEmail,
        onPasswordBlur = viewModel::touchPassword,
        onTermsChange = viewModel::setTerms,
        onCreate = viewModel::submit,
        onGoogle = viewModel::google,
        onBack = onBack,
        onLoginInstead = { onLoginInstead(state.email) },
    )
}

@Composable
internal fun SignupScreen(
    state: SignupState,
    name: TextFieldState,
    email: TextFieldState,
    password: TextFieldState,
    onEmailBlur: () -> Unit,
    onPasswordBlur: () -> Unit,
    onTermsChange: (Boolean) -> Unit,
    onCreate: () -> Unit,
    onGoogle: () -> Unit,
    onBack: () -> Unit,
    onLoginInstead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HoTheme.colors
    Column(modifier.fillMaxSize().imePadding()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .testTag("signup-page")
                // The design puts the back button 56 below the status band; the hidden header already holds 50 of that.
                .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                RoundButton(HoIcons.ArrowLeft, "Back to log in", onBack, colors.glass, colors.divider, colors.onSurface, 22.dp)
                HoText(
                    "Create account",
                    HoTypeRole.DisplaySmall,
                    modifier = Modifier.semantics { heading() },
                    style = TextStyle(fontSize = 36.sp, lineHeight = 36.sp, letterSpacing = 0.5.sp),
                )
            }
            GoogleButton("Sign up with Google", onGoogle, Modifier.testTag("signup-google"))
            OrDivider("or sign up with email")
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (state.taken) {
                    ErrorAlert(
                        "There's already an account for this email on this phone.",
                        Modifier.testTag("signup-taken"),
                        action = { LinkButton("Log in instead", onLoginInstead, Modifier.testTag("signup-login-instead")) },
                    )
                }
                HoTextField(
                    label = "Name",
                    state = name,
                    modifier = Modifier.testTag("signup-name"),
                    placeholder = "Maya Almeida",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                HoTextField(
                    label = "Email",
                    state = email,
                    modifier = Modifier.testTag("signup-email"),
                    placeholder = "you@example.com",
                    error = state.emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    onFocusLost = onEmailBlur,
                )
                HoTextField(
                    label = "Password",
                    state = password,
                    modifier = Modifier.testTag("signup-password"),
                    password = true,
                    supporting = PASSWORD_ERROR,
                    invalid = state.passwordInvalid,
                    onFocusLost = onPasswordBlur,
                )
                HoCheckbox(
                    checked = state.terms,
                    onCheckedChange = onTermsChange,
                    label = "I agree to the homeout Terms of Service and Privacy Policy.",
                    modifier = Modifier.testTag("signup-terms"),
                )
                HoButton(
                    "Create account",
                    onClick = onCreate,
                    modifier = Modifier.padding(top = 4.dp).testTag("signup-submit"),
                    size = HoButtonSize.Large,
                    block = true,
                    tinted = true,
                    enabled = state.canSubmit,
                )
            }
        }
        AuthFooter("Already have an account?", "Log in", onBack, "signup-login")
    }
}
