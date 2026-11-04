package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentException
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus
import ar.edu.um.prog2.quiroga.turnosapp.domain.validation.PhoneRules
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.FakeAppointmentRepository.Companion.reservation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// El reloj de las corrutinas es virtual: advanceTimeBy hace pasar los 3 s entre consultas sin esperarlos.
@OptIn(ExperimentalCoroutinesApi::class)
class ReservationDetailViewModelTest {

    private val scheduler = TestCoroutineScheduler()
    private val repository = FakeAppointmentRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher(scheduler))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = ReservationDetailViewModel("proceso-1", "Romina Acosta", repository)

    private fun afterOnePoll() {
        scheduler.advanceTimeBy(ReservationDetailViewModel.POLL_INTERVAL_MILLIS + 1)
    }

    @Test
    fun followsTheReservationWhileItIsInProgressAndStopsAtTheEnd() {
        repository.reservationResults = mutableListOf(
            Result.success(reservation(ReservationStatus.WAITING_FOR_PHONE)),
            Result.success(reservation(ReservationStatus.WAITING_FOR_PHONE, phoneRequested = true)),
            Result.success(reservation(ReservationStatus.CONFIRMED)),
        )
        val viewModel = viewModel()
        assertFalse(viewModel.uiState.value.asksForPhone)

        afterOnePoll()
        assertTrue(viewModel.uiState.value.asksForPhone)
        afterOnePoll()
        assertEquals(ReservationStatus.CONFIRMED, viewModel.uiState.value.reservation?.status)

        afterOnePoll()
        afterOnePoll()
        assertEquals(3, repository.reservationRequests.size, "terminada, no se consulta más")
    }

    @Test
    fun aFinishedReservationIsReadOnce() {
        repository.reservationResults = mutableListOf(Result.success(reservation(ReservationStatus.CANCELLED)))
        val viewModel = viewModel()

        afterOnePoll()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, repository.reservationRequests.size)
    }

    @Test
    fun aReadingErrorIsShownAndRetried() {
        repository.reservationResults = mutableListOf(
            Result.failure(AppointmentException("El sistema de turnos no responde.")),
            Result.success(reservation(ReservationStatus.CONFIRMED)),
        )
        val viewModel = viewModel()
        assertEquals("El sistema de turnos no responde.", viewModel.uiState.value.errorMessage)

        afterOnePoll()

        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(ReservationStatus.CONFIRMED, viewModel.uiState.value.reservation?.status)
    }

    @Test
    fun anInvalidPhoneIsMarkedWithoutSendingIt() {
        repository.reservationResults = mutableListOf(Result.success(reservation(ReservationStatus.WAITING_FOR_PHONE, phoneRequested = true)))
        val viewModel = viewModel()

        viewModel.onPhoneChange("123")
        viewModel.submitPhone()

        assertEquals(PhoneRules.INVALID_PHONE, viewModel.uiState.value.phoneError)
        assertTrue(repository.submittedPhones.isEmpty())
    }

    @Test
    fun aValidPhoneIsSentAndTheReservationIsFollowedUntilTheResult() {
        repository.reservationResults = mutableListOf(
            Result.success(reservation(ReservationStatus.WAITING_FOR_PHONE, phoneRequested = true)),
            Result.success(reservation(ReservationStatus.PHONE_SUBMITTED)),
            Result.success(reservation(ReservationStatus.CONFIRMED)),
        )
        repository.submitPhoneResult = Result.success(reservation(ReservationStatus.PHONE_SUBMITTED))
        val viewModel = viewModel()

        viewModel.onPhoneChange("+54 9 261 555-1234")
        viewModel.submitPhone()

        assertEquals(listOf("proceso-1" to "+54 9 261 555-1234"), repository.submittedPhones)
        assertEquals("", viewModel.uiState.value.phone)
        assertFalse(viewModel.uiState.value.asksForPhone)
        afterOnePoll()
        assertEquals(ReservationStatus.CONFIRMED, viewModel.uiState.value.reservation?.status)
    }

    @Test
    fun aRejectedSubmissionShowsTheMessage() {
        repository.reservationResults = mutableListOf(Result.success(reservation(ReservationStatus.WAITING_FOR_PHONE, phoneRequested = true)))
        repository.submitPhoneResult = Result.failure(AppointmentException("La reserva ya no está esperando un teléfono."))
        val viewModel = viewModel()

        viewModel.onPhoneChange("+5492615551234")
        viewModel.submitPhone()

        assertEquals("La reserva ya no está esperando un teléfono.", viewModel.uiState.value.phoneError)
        assertFalse(viewModel.uiState.value.isSubmittingPhone)
    }

    @Test
    fun withoutARequestThePhoneCannotBeSent() {
        repository.reservationResults = mutableListOf(Result.success(reservation(ReservationStatus.WAITING_FOR_PHONE)))
        val viewModel = viewModel()

        viewModel.onPhoneChange("+5492615551234")
        viewModel.submitPhone()

        assertFalse(viewModel.uiState.value.canSubmitPhone)
        assertTrue(repository.submittedPhones.isEmpty())
    }

    @Test
    fun aConfirmedReservationIsCancelledAfterConfirmingIt() {
        repository.reservationResults = mutableListOf(Result.success(reservation(ReservationStatus.CONFIRMED)))
        val viewModel = viewModel()
        assertTrue(viewModel.uiState.value.canCancel)

        viewModel.onCancelRequested()
        assertTrue(viewModel.uiState.value.isConfirmingCancel)
        assertTrue(repository.cancelled.isEmpty(), "pedir que confirme todavía no cancela")
        viewModel.confirmCancel()

        assertEquals(listOf("proceso-1"), repository.cancelled)
        val state = viewModel.uiState.value
        assertEquals(ReservationStatus.CANCELLED, state.reservation?.status)
        assertFalse(state.isConfirmingCancel)
        assertFalse(state.canCancel)
    }

    @Test
    fun aFailedCancellationKeepsTheTurnAndShowsWhy() {
        repository.reservationResults = mutableListOf(Result.success(reservation(ReservationStatus.CONFIRMED)))
        repository.cancelResult = Result.failure(AppointmentException("El sistema de turnos no responde. Probá en un rato."))
        val viewModel = viewModel()

        viewModel.onCancelRequested()
        viewModel.confirmCancel()

        val state = viewModel.uiState.value
        assertEquals("El sistema de turnos no responde. Probá en un rato.", state.cancelError)
        assertEquals(ReservationStatus.CONFIRMED, state.reservation?.status)
        assertTrue(state.isConfirmingCancel, "el diálogo sigue abierto para reintentar o volver")
        assertFalse(state.isCancelling)
    }

    @Test
    fun goingBackFromTheDialogDoesNotCancel() {
        repository.reservationResults = mutableListOf(Result.success(reservation(ReservationStatus.CONFIRMED)))
        val viewModel = viewModel()

        viewModel.onCancelRequested()
        viewModel.onCancelDismissed()
        viewModel.confirmCancel()

        assertFalse(viewModel.uiState.value.isConfirmingCancel)
        assertTrue(repository.cancelled.isEmpty())
    }

    @Test
    fun onlyAConfirmedReservationCanBeCancelled() {
        repository.reservationResults = mutableListOf(Result.success(reservation(ReservationStatus.WAITING_FOR_PHONE)))
        val viewModel = viewModel()

        viewModel.onCancelRequested()
        viewModel.confirmCancel()

        assertFalse(viewModel.uiState.value.canCancel)
        assertFalse(viewModel.uiState.value.isConfirmingCancel)
        assertTrue(repository.cancelled.isEmpty())
    }
}
