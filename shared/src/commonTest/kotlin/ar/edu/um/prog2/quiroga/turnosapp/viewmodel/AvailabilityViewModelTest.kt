package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentException
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.FakeAppointmentRepository.Companion.NINE
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
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
class AvailabilityViewModelTest {

    private val repository = FakeAppointmentRepository()
    private val today = LocalDate(2026, 10, 30)
    private val monday = LocalDate(2026, 11, 2)

    private fun viewModel() = AvailabilityViewModel(professionalId = 201, professionalName = "Ana Torres", repository, today)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startsWithTodayAndItsFreeSlots() {
        val state = viewModel().uiState.value

        assertEquals(listOf(201L to today), repository.availabilityRequests)
        assertEquals(today, state.selectedDate)
        assertEquals(AvailabilityViewModel.DAYS_SHOWN, state.days.size)
        assertEquals(LocalDate(2026, 11, 12), state.days.last())
        assertEquals(2, state.slots.size)
        assertFalse(state.isLoading)
    }

    @Test
    fun choosingAnotherDayLoadsItsSlots() {
        val viewModel = viewModel()

        viewModel.onDateSelected(monday)

        assertEquals(201L to monday, repository.availabilityRequests.last())
        assertEquals(monday, viewModel.uiState.value.selectedDate)
    }

    @Test
    fun showsLoadingWhileTheSlotsArrive() {
        repository.gate = CompletableDeferred()
        val viewModel = viewModel()
        assertTrue(viewModel.uiState.value.isLoading)

        repository.gate!!.complete(Unit)

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun aDayWithoutSlotsSaysSo() {
        repository.availabilityResult = Result.success(emptyList())

        assertTrue(viewModel().uiState.value.showsNoSlots)
    }

    @Test
    fun aLoadingErrorIsShownAndCanBeRetried() {
        repository.availabilityResult = Result.failure(AppointmentException("El sistema de turnos no responde."))
        val viewModel = viewModel()
        assertEquals("El sistema de turnos no responde.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.showsNoSlots)

        repository.availabilityResult = Result.success(listOf(NINE))
        viewModel.retry()

        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(listOf(NINE), viewModel.uiState.value.slots)
    }

    @Test
    fun confirmingASlotReservesItAndShowsTheResult() {
        val viewModel = viewModel()
        viewModel.onDateSelected(monday)

        viewModel.onSlotSelected(NINE)
        viewModel.confirmReservation()

        val sent = repository.reservations.single()
        assertEquals(201L, sent.professionalId)
        assertEquals(monday, sent.date)
        assertEquals(NINE.startTime, sent.startTime)
        assertNull(sent.patientFirstName)
        val state = viewModel.uiState.value
        assertNull(state.confirmation)
        assertEquals(ReservationStatus.WAITING_FOR_PHONE, state.reservation?.status)
    }

    @Test
    fun aFailedReservationKeepsTheDialogWithTheMessage() {
        repository.reserveResults += Result.failure(AppointmentException("Ese turno ya no está libre. Elegí otro."))
        val viewModel = viewModel()

        viewModel.onSlotSelected(NINE)
        viewModel.confirmReservation()

        val confirmation = assertNotNull(viewModel.uiState.value.confirmation)
        assertEquals("Ese turno ya no está libre. Elegí otro.", confirmation.errorMessage)
        assertFalse(confirmation.isReserving)
        assertFalse(confirmation.askPatientName)
        assertNull(viewModel.uiState.value.reservation)
    }

    @Test
    fun closingTheDialogAfterAnErrorReloadsTheSlots() {
        repository.reserveResults += Result.failure(AppointmentException("Ese turno ya no está libre. Elegí otro."))
        val viewModel = viewModel()
        viewModel.onSlotSelected(NINE)
        viewModel.confirmReservation()

        viewModel.onConfirmationDismissed()

        assertNull(viewModel.uiState.value.confirmation)
        assertEquals(2, repository.availabilityRequests.size)
    }

    @Test
    fun closingTheDialogWithoutTryingDoesNotReload() {
        val viewModel = viewModel()
        viewModel.onSlotSelected(NINE)

        viewModel.onConfirmationDismissed()

        assertEquals(1, repository.availabilityRequests.size)
    }

    @Test
    fun withoutNamesInTheAccountItAsksForThemAndRetries() {
        repository.reserveResults += Result.failure(AppointmentException("Completá tu nombre y apellido.", needsPatientName = true))
        val viewModel = viewModel()
        viewModel.onSlotSelected(NINE)
        viewModel.confirmReservation()

        val asking = assertNotNull(viewModel.uiState.value.confirmation)
        assertTrue(asking.askPatientName)
        assertFalse(asking.canConfirm, "sin nombres no se puede confirmar")

        viewModel.onPatientFirstNameChange(" Ana ")
        viewModel.onPatientLastNameChange("Torres")
        viewModel.confirmReservation()

        val retried = repository.reservations.last()
        assertEquals("Ana", retried.patientFirstName)
        assertEquals("Torres", retried.patientLastName)
        assertNotNull(viewModel.uiState.value.reservation)
    }

    @Test
    fun aOneLetterNameIsNotEnough() {
        repository.reserveResults += Result.failure(AppointmentException("Completá tu nombre y apellido.", needsPatientName = true))
        val viewModel = viewModel()
        viewModel.onSlotSelected(NINE)
        viewModel.confirmReservation()

        viewModel.onPatientFirstNameChange("A")
        viewModel.onPatientLastNameChange("Torres")
        viewModel.confirmReservation()

        assertFalse(viewModel.uiState.value.confirmation!!.canConfirm)
        assertEquals(1, repository.reservations.size)
    }

    @Test
    fun confirmingWithoutAChosenSlotDoesNothing() {
        val viewModel = viewModel()

        viewModel.confirmReservation()

        assertTrue(repository.reservations.isEmpty())
    }
}
