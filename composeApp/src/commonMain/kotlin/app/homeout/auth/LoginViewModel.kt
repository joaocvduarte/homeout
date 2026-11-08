package app.homeout.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.homeout.domain.ContinueWithGoogle
import app.homeout.domain.Credentials
import app.homeout.domain.LogIn
import app.homeout.domain.LogInResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val EMAIL_ERROR = "Enter an email like you@example.com."
internal const val PASSWORD_ERROR = "At least 8 characters."

/** What Log in shows. A field's error appears only once the field has lost focus and holds something wrong. */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailTouched: Boolean = false,
    val passwordTouched: Boolean = false,
    val mismatch: Boolean = false,
    val busy: Boolean = false,
) {
    val emailError: String? get() = EMAIL_ERROR.takeIf { emailTouched && email.isNotEmpty() && !Credentials.isValidEmail(email) }
    val passwordError: String? get() = PASSWORD_ERROR.takeIf { passwordTouched && password.isNotEmpty() && !Credentials.isValidPassword(password) }
    val canSubmit: Boolean get() = Credentials.isValidEmail(email) && Credentials.isValidPassword(password) && !busy
}

/**
 * The email, the touched flags and the mismatch alert live in saved state. The password stays in this
 * ViewModel's memory only: saved state can be written to disk when the process dies.
 */
class LoginViewModel(
    private val handle: SavedStateHandle,
    private val logIn: LogIn,
    private val continueWithGoogle: ContinueWithGoogle,
) : ViewModel() {
    private val _state = MutableStateFlow(
        LoginState(
            email = handle[EMAIL] ?: "",
            emailTouched = handle[EMAIL_TOUCHED] ?: false,
            passwordTouched = handle[PASSWORD_TOUCHED] ?: false,
            mismatch = handle[MISMATCH] ?: false,
        ),
    )
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val signedInEvents = Channel<Unit>(Channel.CONFLATED)

    /** One event when someone has signed in. */
    val signedIn: Flow<Unit> = signedInEvents.receiveAsFlow()

    fun setEmail(email: String) {
        if (email == _state.value.email) return
        handle[EMAIL] = email
        setMismatch(false)
        _state.update { it.copy(email = email) }
    }

    fun setPassword(password: String) {
        if (password == _state.value.password) return
        setMismatch(false)
        _state.update { it.copy(password = password) }
    }

    fun touchEmail() {
        handle[EMAIL_TOUCHED] = true
        _state.update { it.copy(emailTouched = true) }
    }

    fun touchPassword() {
        handle[PASSWORD_TOUCHED] = true
        _state.update { it.copy(passwordTouched = true) }
    }

    /** Log in instead: the email Create account found taken, with the password left to type. */
    fun prefill(email: String) {
        handle[EMAIL] = email
        setMismatch(false)
        _state.update { it.copy(email = email, password = "") }
    }

    fun submit() {
        val current = _state.value
        if (!current.canSubmit) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            var signedIn = false
            try {
                when (logIn(current.email, current.password)) {
                    LogInResult.SignedIn -> signedIn = true
                    LogInResult.Mismatch -> setMismatch(true)
                }
            } finally {
                // After a sign-in the screen is on its way out; staying busy stops a second tap.
                if (!signedIn) _state.update { it.copy(busy = false) }
            }
            if (signedIn) signedInEvents.send(Unit)
        }
    }

    fun google() {
        if (_state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            continueWithGoogle()
            signedInEvents.send(Unit)
        }
    }

    private fun setMismatch(value: Boolean) {
        handle[MISMATCH] = value
        _state.update { it.copy(mismatch = value) }
    }

    private companion object {
        const val EMAIL = "email"
        const val EMAIL_TOUCHED = "emailTouched"
        const val PASSWORD_TOUCHED = "passwordTouched"
        const val MISMATCH = "mismatch"
    }
}
