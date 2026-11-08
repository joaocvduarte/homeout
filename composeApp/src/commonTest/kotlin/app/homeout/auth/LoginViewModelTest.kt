package app.homeout.auth

import androidx.lifecycle.SavedStateHandle
import app.homeout.TestPasswordHasher
import app.homeout.data.InMemoryKeyValueStore
import app.homeout.data.StoredAccountRepository
import app.homeout.domain.Account
import app.homeout.domain.ContinueWithGoogle
import app.homeout.domain.LogIn
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
class LoginViewModelTest {

    @BeforeTest
    fun mainDispatcher() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun resetMainDispatcher() = Dispatchers.resetMain()

    private val repo = StoredAccountRepository(InMemoryKeyValueStore())

    private suspend fun viewModel(handle: SavedStateHandle = SavedStateHandle()): LoginViewModel {
        repo.add(Account("Ana", "ana@x.pt"), TestPasswordHasher.hash("12345678"))
        return LoginViewModel(handle, LogIn(repo, TestPasswordHasher), ContinueWithGoogle(repo))
    }

    @Test
    fun theButtonEnablesOnlyWhenTheEmailIsValidAndThePasswordHasEightCharacters() = runTest {
        val vm = viewModel()
        assertFalse(vm.state.value.canSubmit)
        vm.setEmail("ana@x")
        vm.setPassword("12345678")
        assertFalse(vm.state.value.canSubmit)
        vm.setEmail("ana@x.pt")
        vm.setPassword("1234567")
        assertFalse(vm.state.value.canSubmit)
        vm.setPassword("12345678")
        assertTrue(vm.state.value.canSubmit)
    }

    @Test
    fun fieldErrorsAppearOnlyAfterTheFieldLosesFocusAndHoldsSomethingWrong() = runTest {
        val vm = viewModel()
        vm.setEmail("maya@example")
        vm.setPassword("lisbon")
        assertNull(vm.state.value.emailError)
        assertNull(vm.state.value.passwordError)

        vm.touchEmail()
        vm.touchPassword()
        assertEquals("Enter an email like you@example.com.", vm.state.value.emailError)
        assertEquals("At least 8 characters.", vm.state.value.passwordError)

        vm.setEmail("maya@example.com")
        vm.setPassword("lisbon2026")
        assertNull(vm.state.value.emailError)
        assertNull(vm.state.value.passwordError)
    }

    @Test
    fun anEmptyTouchedFieldShowsNoError() = runTest {
        val vm = viewModel()
        vm.touchEmail()
        vm.touchPassword()
        assertNull(vm.state.value.emailError)
        assertNull(vm.state.value.passwordError)
    }

    @Test
    fun aWrongPasswordShowsTheMismatchAndEditingEitherFieldClearsIt() = runTest {
        val vm = viewModel()
        vm.setEmail("ana@x.pt")
        vm.setPassword("00000000")
        vm.submit()
        assertTrue(vm.state.value.mismatch)
        assertFalse(vm.state.value.busy)

        vm.setPassword("11111111")
        assertFalse(vm.state.value.mismatch)

        vm.submit()
        assertTrue(vm.state.value.mismatch)
        vm.setEmail("ana@x.pt ")
        assertFalse(vm.state.value.mismatch)
    }

    @Test
    fun anUnknownEmailIsAMismatchToo() = runTest {
        val vm = viewModel()
        vm.setEmail("nobody@x.pt")
        vm.setPassword("12345678")
        vm.submit()
        assertTrue(vm.state.value.mismatch)
        assertNull(repo.session())
    }

    @Test
    fun theRightEmailAndPasswordSignInOnceEvenIfTappedTwice() = runTest {
        val vm = viewModel()
        val events = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.signedIn.collect { events += it } }
        vm.setEmail("ana@x.pt")
        vm.setPassword("12345678")

        vm.submit()
        vm.submit()

        assertEquals(1, events.size)
        assertEquals("ana@x.pt", repo.session())
    }

    @Test
    fun googleSignsInOnceEvenIfTappedTwice() = runTest {
        val vm = viewModel()
        val events = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.signedIn.collect { events += it } }

        vm.google()
        vm.google()

        assertEquals(1, events.size)
        assertEquals("google@homeout.local", repo.session())
    }

    @Test
    fun theEmailAndFlagsSurviveANewViewModelButThePasswordIsNeverSaved() = runTest {
        val handle = SavedStateHandle()
        val first = viewModel(handle)
        first.setEmail("maya@example")
        first.setPassword("secret-password")
        first.touchEmail()

        assertTrue(handle.keys().none { handle.get<Any>(it) == "secret-password" }, "the password reached saved state")
        val second = viewModel(handle)
        assertEquals("maya@example", second.state.value.email)
        assertTrue(second.state.value.emailTouched)
        assertEquals("", second.state.value.password)
    }

    @Test
    fun theMismatchSurvivesANewViewModel() = runTest {
        val handle = SavedStateHandle()
        val first = viewModel(handle)
        first.setEmail("ana@x.pt")
        first.setPassword("00000000")
        first.submit()
        assertTrue(viewModel(handle).state.value.mismatch)
    }

    @Test
    fun prefillSetsTheEmailAndClearsThePasswordAndMismatch() = runTest {
        val vm = viewModel()
        vm.setEmail("ana@x.pt")
        vm.setPassword("00000000")
        vm.submit()

        vm.prefill("bia@x.pt")

        assertEquals("bia@x.pt", vm.state.value.email)
        assertEquals("", vm.state.value.password)
        assertFalse(vm.state.value.mismatch)
    }
}
