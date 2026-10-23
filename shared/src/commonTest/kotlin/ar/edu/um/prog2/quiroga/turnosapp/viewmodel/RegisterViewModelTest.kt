package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AuthException
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Registration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val repository = FakeAuthRepository()
    private lateinit var viewModel: RegisterViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = RegisterViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fillValidForm() {
        viewModel.onLoginChange(" juan.perez ")
        viewModel.onPasswordChange("una-clave")
        viewModel.onConfirmationChange("una-clave")
        viewModel.onFirstNameChange("Juan")
        viewModel.onLastNameChange("")
        viewModel.onEmailChange("")
    }

    @Test
    fun invalidFieldsShowTheirErrorsAndNothingIsSent() {
        viewModel.onLoginChange("j")
        viewModel.onPasswordChange("abc")
        viewModel.onConfirmationChange("otra")
        viewModel.onEmailChange("no-es-email")

        viewModel.register()

        val errors = viewModel.uiState.value.fieldErrors
        assertNotNull(errors.login)
        assertNotNull(errors.password)
        assertNotNull(errors.confirmation)
        assertNotNull(errors.email)
        assertEquals(0, repository.registerCalls)
    }

    @Test
    fun validFormIsSentWithEmptyOptionalFieldsAsNull() {
        fillValidForm()

        viewModel.register()

        assertEquals(
            Registration(login = "juan.perez", password = "una-clave", firstName = "Juan"),
            repository.lastRegistration,
        )
        assertTrue(viewModel.uiState.value.registered)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun backendErrorIsShownAndTheUserIsNotRegistered() {
        repository.registerResult = Result.failure(AuthException("El usuario ya está en uso"))
        fillValidForm()

        viewModel.register()

        assertEquals("El usuario ya está en uso", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.registered)
    }

    @Test
    fun editingAFieldClearsOnlyItsError() {
        viewModel.onLoginChange("j")
        viewModel.onPasswordChange("abc")
        viewModel.register()

        viewModel.onLoginChange("juan")

        assertNull(viewModel.uiState.value.fieldErrors.login)
        assertNotNull(viewModel.uiState.value.fieldErrors.password)
    }

    @Test
    fun everyFieldClearsItsOwnError() {
        viewModel.onFirstNameChange("a".repeat(51))
        viewModel.onLastNameChange("a".repeat(51))
        viewModel.register()
        assertNotNull(viewModel.uiState.value.fieldErrors.firstName)
        assertNotNull(viewModel.uiState.value.fieldErrors.lastName)

        viewModel.onPasswordChange("una-clave")
        viewModel.onConfirmationChange("una-clave")
        viewModel.onFirstNameChange("Juan")
        viewModel.onLastNameChange("Perez")
        viewModel.onEmailChange("juan@example.com")

        val errors = viewModel.uiState.value.fieldErrors
        assertNull(errors.password)
        assertNull(errors.confirmation)
        assertNull(errors.firstName)
        assertNull(errors.lastName)
        assertNull(errors.email)
    }
}
