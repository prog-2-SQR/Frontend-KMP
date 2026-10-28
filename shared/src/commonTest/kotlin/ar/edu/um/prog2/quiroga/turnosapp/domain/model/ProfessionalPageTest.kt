package ar.edu.um.prog2.quiroga.turnosapp.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProfessionalPageTest {

    private fun page(page: Int, totalPages: Int) =
        ProfessionalPage(professionals = emptyList(), page = page, totalPages = totalPages, totalElements = 0)

    @Test
    fun firstOfSeveralPagesOnlyHasNext() {
        assertFalse(page(0, 3).hasPrevious)
        assertTrue(page(0, 3).hasNext)
    }

    @Test
    fun middlePageHasBoth() {
        assertTrue(page(1, 3).hasPrevious)
        assertTrue(page(1, 3).hasNext)
    }

    @Test
    fun lastPageOnlyHasPrevious() {
        assertTrue(page(2, 3).hasPrevious)
        assertFalse(page(2, 3).hasNext)
    }

    @Test
    fun emptyResultHasNeither() {
        assertFalse(ProfessionalPage.EMPTY.hasPrevious)
        assertFalse(ProfessionalPage.EMPTY.hasNext)
    }

    @Test
    fun fullNameJoinsFirstAndLastName() {
        assertEquals("Ana Torres", Professional(id = 201, categoryId = 101, firstName = "Ana", lastName = "Torres", enabled = true).fullName)
    }
}
