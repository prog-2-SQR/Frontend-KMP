package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AuthException
import kotlinx.coroutines.CompletableDeferred
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

// viewModelScope usa Dispatchers.Main, que en los tests no existe: se reemplaza por uno de prueba.
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val repository = FakeAuthRepository()
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = LoginViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun cannotSubmitUntilUsernameAndPasswordAreFilled() {
        assertFalse(viewModel.uiState.value.canSubmit)
        viewModel.onUsernameChange("juan")
        assertFalse(viewModel.uiState.value.canSubmit)
        viewModel.onPasswordChange("una-clave")
        assertTrue(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun loginIsIgnoredWhileTheFormIsIncomplete() {
        viewModel.login()

        assertNull(repository.lastLogin)
    }

    @Test
    fun successfulLoginExposesTheSession() {
        viewModel.onUsernameChange("  juan  ")
        viewModel.onPasswordChange("una-clave")
        viewModel.onRememberMeChange(true)

        viewModel.login()

        assertEquals(Triple("juan", "una-clave", true), repository.lastLogin)
        assertEquals("juan", viewModel.uiState.value.session?.login)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun failedLoginShowsTheErrorAndNoSession() {
        repository.loginResult = Result.failure(AuthException("Usuario o contraseña incorrectos"))
        viewModel.onUsernameChange("juan")
        viewModel.onPasswordChange("mala")

        viewModel.login()

        assertEquals("Usuario o contraseña incorrectos", viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.session)
    }

    @Test
    fun anErrorWithoutMessageShowsAGenericOne() {
        repository.loginResult = Result.failure(IllegalStateException())
        viewModel.onUsernameChange("juan")
        viewModel.onPasswordChange("una-clave")

        viewModel.login()

        assertEquals(ErrorMessages.GENERIC, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun showsLoadingWhileWaitingAndCannotSubmitTwice() {
        val pending = CompletableDeferred<Unit>()
        repository.pendingLogin = pending
        viewModel.onUsernameChange("juan")
        viewModel.onPasswordChange("una-clave")

        viewModel.login()

        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.canSubmit)
        pending.complete(Unit)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.session)
    }

    @Test
    fun typingClearsThePreviousError() {
        repository.loginResult = Result.failure(AuthException("Usuario o contraseña incorrectos"))
        viewModel.onUsernameChange("juan")
        viewModel.onPasswordChange("mala")
        viewModel.login()

        viewModel.onPasswordChange("otra")

        assertNull(viewModel.uiState.value.errorMessage)
    }
}
