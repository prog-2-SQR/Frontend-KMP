package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionViewModelTest {

    private val repository = FakeAuthRepository()
    private val juan = AuthSession(login = "juan", token = "token")

    @Test
    fun startsWithTheSavedSession() {
        repository.savedSession = juan

        assertEquals(juan, SessionViewModel(repository).session.value)
    }

    @Test
    fun withoutASavedSessionItAsksForTheLogin() {
        assertNull(SessionViewModel(repository).session.value)
    }

    @Test
    fun loggingInShowsTheAppAndLoggingOutForgetsTheSession() {
        val viewModel = SessionViewModel(repository)

        viewModel.onLoggedIn(juan)
        assertEquals(juan, viewModel.session.value)

        viewModel.logout()
        assertNull(viewModel.session.value)
        assertEquals(1, repository.logoutCalls)
    }
}
