package app.homeout.auth

import androidx.lifecycle.SavedStateHandle
import app.homeout.TestPasswordHasher
import app.homeout.data.InMemoryKeyValueStore
import app.homeout.data.StoredAccountRepository
import app.homeout.domain.Account
import app.homeout.domain.ContinueWithGoogle
import app.homeout.domain.CreateAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SignupViewModelTest {

    @BeforeTest
    fun mainDispatcher() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun resetMainDispatcher() = Dispatchers.resetMain()

    private val repo = StoredAccountRepository(InMemoryKeyValueStore())

    private fun viewModel(handle: SavedStateHandle = SavedStateHandle()) =
        SignupViewModel(handle, CreateAccount(repo, TestPasswordHasher), ContinueWithGoogle(repo))

    private fun SignupViewModel.fill(name: String = "Ana", email: String = "ana@x.pt", password: String = "12345678", terms: Boolean = true) {
        setName(name)
        setEmail(email)
        setPassword(password)
        setTerms(terms)
    }

    @Test
    fun theButtonNeedsANameAValidEmailEightCharactersAndTheTerms() {
        val vm = viewModel()
        assertFalse(vm.state.value.canSubmit)
        vm.fill(name = "  ")
        assertFalse(vm.state.value.canSubmit)
        vm.fill(email = "ana@x")
        assertFalse(vm.state.value.canSubmit)
        vm.fill(password = "1234567")
        assertFalse(vm.state.value.canSubmit)
        vm.fill(terms = false)
        assertFalse(vm.state.value.canSubmit)
        vm.fill()
        assertTrue(vm.state.value.canSubmit)
    }

    @Test
    fun fieldErrorsAppearOnlyAfterTheFieldLosesFocus() {
        val vm = viewModel()
        vm.fill(email = "maya@example", password = "lisbon")
        assertNull(vm.state.value.emailError)
        assertFalse(vm.state.value.passwordInvalid)

        vm.touchEmail()
        vm.touchPassword()

        assertEquals("Enter an email like you@example.com.", vm.state.value.emailError)
        assertTrue(vm.state.value.passwordInvalid)
    }

    @Test
    fun creatingAnAccountStoresItSignsInAndEmitsOnce() = runTest {
        val vm = viewModel()
        val events = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.signedIn.collect { events += it } }
        vm.fill(name = " Ana ", email = "Ana@X.pt")

        vm.submit()
        vm.submit()

        assertEquals(1, events.size)
        assertEquals(Account("Ana", "ana@x.pt"), repo.find("ana@x.pt"))
        assertEquals("ana@x.pt", repo.session())
    }

    @Test
    fun anExistingEmailShowsTheTakenAlertAndEditingTheEmailClearsIt() = runTest {
        repo.add(Account("Ana", "ana@x.pt"), TestPasswordHasher.hash("12345678"))
        val vm = viewModel()
        vm.fill(email = "ANA@x.pt")

        vm.submit()

        assertTrue(vm.state.value.taken)
        assertFalse(vm.state.value.busy)
        assertNull(repo.session())
        vm.setEmail("bia@x.pt")
        assertFalse(vm.state.value.taken)
    }

    @Test
    fun googleSignsInWithoutAnAccount() = runTest {
        val vm = viewModel()
        val events = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.signedIn.collect { events += it } }

        vm.google()
        vm.google()

        assertEquals(1, events.size)
        assertEquals("google@homeout.local", repo.session())
    }

    @Test
    fun theFormSurvivesANewViewModelButThePasswordIsNeverSaved() {
        val handle = SavedStateHandle()
        viewModel(handle).fill(name = "Ana", email = "ana@x", password = "secret-password", terms = true)
        viewModel(handle).touchEmail()

        assertTrue(handle.keys().none { handle.get<Any>(it) == "secret-password" }, "the password reached saved state")
        val restored = viewModel(handle)
        assertEquals("Ana", restored.state.value.name)
        assertEquals("ana@x", restored.state.value.email)
        assertTrue(restored.state.value.terms)
        assertTrue(restored.state.value.emailTouched)
        assertEquals("", restored.state.value.password)
    }
}
