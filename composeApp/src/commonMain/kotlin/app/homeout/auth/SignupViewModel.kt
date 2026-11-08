package app.homeout.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.homeout.domain.ContinueWithGoogle
import app.homeout.domain.CreateAccount
import app.homeout.domain.CreateAccountResult
import app.homeout.domain.Credentials
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** What Create account shows. Same rules as [LoginState], plus a name and the terms. */
data class SignupState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val terms: Boolean = false,
    val emailTouched: Boolean = false,
    val passwordTouched: Boolean = false,
    val taken: Boolean = false,
    val busy: Boolean = false,
) {
    val emailError: String? get() = EMAIL_ERROR.takeIf { emailTouched && email.isNotEmpty() && !Credentials.isValidEmail(email) }

    /** The password hint turns to the error color, since "At least 8 characters." is already on screen. */
    val passwordInvalid: Boolean get() = passwordTouched && password.isNotEmpty() && !Credentials.isValidPassword(password)

    val canSubmit: Boolean
        get() = name.isNotBlank() && Credentials.isValidEmail(email) && Credentials.isValidPassword(password) && terms && !busy
}

/** Name, email, terms, the touched flags and the taken alert are saved; the password stays in memory only. */
class SignupViewModel(
    private val handle: SavedStateHandle,
    private val createAccount: CreateAccount,
    private val continueWithGoogle: ContinueWithGoogle,
) : ViewModel() {
    private val _state = MutableStateFlow(
        SignupState(
            name = handle[NAME] ?: "",
            email = handle[EMAIL] ?: "",
            terms = handle[TERMS] ?: false,
            emailTouched = handle[EMAIL_TOUCHED] ?: false,
            passwordTouched = handle[PASSWORD_TOUCHED] ?: false,
            taken = handle[TAKEN] ?: false,
        ),
    )
    val state: StateFlow<SignupState> = _state.asStateFlow()

    private val signedInEvents = Channel<Unit>(Channel.CONFLATED)

    /** One event when an account is created or Google signs in. */
    val signedIn: Flow<Unit> = signedInEvents.receiveAsFlow()

    fun setName(name: String) {
        if (name == _state.value.name) return
        handle[NAME] = name
        _state.update { it.copy(name = name) }
    }

    fun setEmail(email: String) {
        if (email == _state.value.email) return
        handle[EMAIL] = email
        setTaken(false)
        _state.update { it.copy(email = email) }
    }

    fun setPassword(password: String) {
        if (password == _state.value.password) return
        _state.update { it.copy(password = password) }
    }

    fun setTerms(terms: Boolean) {
        handle[TERMS] = terms
        _state.update { it.copy(terms = terms) }
    }

    fun touchEmail() {
        handle[EMAIL_TOUCHED] = true
        _state.update { it.copy(emailTouched = true) }
    }

    fun touchPassword() {
        handle[PASSWORD_TOUCHED] = true
        _state.update { it.copy(passwordTouched = true) }
    }

    fun submit() {
        val current = _state.value
        if (!current.canSubmit) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            var created = false
            try {
                when (createAccount(current.name, current.email, current.password, current.terms)) {
                    is CreateAccountResult.Created -> created = true
                    CreateAccountResult.EmailTaken -> setTaken(true)
                }
            } finally {
                // After a sign-in the screen is on its way out; staying busy stops a second tap.
                if (!created) _state.update { it.copy(busy = false) }
            }
            if (created) signedInEvents.send(Unit)
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

    private fun setTaken(value: Boolean) {
        handle[TAKEN] = value
        _state.update { it.copy(taken = value) }
    }

    private companion object {
        const val NAME = "name"
        const val EMAIL = "email"
        const val TERMS = "terms"
        const val EMAIL_TOUCHED = "emailTouched"
        const val PASSWORD_TOUCHED = "passwordTouched"
        const val TAKEN = "taken"
    }
}
