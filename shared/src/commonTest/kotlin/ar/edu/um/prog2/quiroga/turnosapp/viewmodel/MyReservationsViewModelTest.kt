package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentException
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.CatalogException
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationPage
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.FakeAppointmentRepository.Companion.reservation
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MyReservationsViewModelTest {

    private val appointments = FakeAppointmentRepository()
    private val professionals = FakeProfessionalRepository()

    private val twoReservationsOfTheSameProfessional = ReservationPage(
        reservations = listOf(
            reservation(ReservationStatus.CONFIRMED, id = "proceso-2"),
            reservation(ReservationStatus.FAILED, id = "proceso-1"),
        ),
        page = 0,
        totalPages = 2,
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsTheFirstPageWithTheNameOfEachProfessionalOnce() {
        appointments.myReservationsResult = Result.success(twoReservationsOfTheSameProfessional)

        val state = MyReservationsViewModel(appointments, professionals).uiState.value

        assertEquals(listOf(0), appointments.myReservationsRequests)
        assertEquals(listOf("proceso-2", "proceso-1"), state.reservations.map { it.id })
        assertEquals(listOf(218L), professionals.professionalRequests)
        assertEquals("Ana Torres", state.professionalName(218))
    }

    @Test
    fun withoutTheCatalogTheNumberIsShown() {
        appointments.myReservationsResult = Result.success(twoReservationsOfTheSameProfessional)
        professionals.professionalResult = { Result.failure(CatalogException("caído")) }

        val state = MyReservationsViewModel(appointments, professionals).uiState.value

        assertEquals("Profesional 218", state.professionalName(218))
        assertTrue(state.errorMessage == null)
    }

    @Test
    fun pagesForwardAndBackAndRefreshGoesToTheFirstPage() {
        appointments.myReservationsResult = Result.success(twoReservationsOfTheSameProfessional)
        val viewModel = MyReservationsViewModel(appointments, professionals)

        viewModel.previousPage()
        viewModel.nextPage()
        appointments.myReservationsResult = Result.success(twoReservationsOfTheSameProfessional.copy(page = 1))
        viewModel.nextPage()
        viewModel.refresh()

        assertEquals(listOf(0, 1, 1, 0), appointments.myReservationsRequests)
    }

    @Test
    fun withoutReservationsItSaysSo() {
        val state = MyReservationsViewModel(appointments, professionals).uiState.value

        assertTrue(state.showsNoReservations)
    }

    @Test
    fun anErrorIsShown() {
        appointments.myReservationsResult = Result.failure(AppointmentException("El sistema de turnos no responde."))

        val state = MyReservationsViewModel(appointments, professionals).uiState.value

        assertEquals("El sistema de turnos no responde.", state.errorMessage)
        assertFalse(state.showsNoReservations)
    }
}
