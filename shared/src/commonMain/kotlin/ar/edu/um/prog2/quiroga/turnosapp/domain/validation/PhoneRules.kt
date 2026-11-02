package ar.edu.um.prog2.quiroga.turnosapp.domain.validation

/**
 * La regla de la cátedra para el teléfono (anexo §15.5), la misma que valida Turnos: se sacan espacios, guiones,
 * paréntesis y puntos, y lo que queda tiene que cumplir ^\+?[0-9]{7,15}$. La app avisa antes de mandarlo.
 */
object PhoneRules {

    private val SEPARATORS = Regex("[\\s\\-().]")
    private val VALID = Regex("^\\+?[0-9]{7,15}\$")

    const val INVALID_PHONE = "El teléfono debe tener entre 7 y 15 dígitos (con + opcional)"

    fun phoneError(phone: String): String? =
        if (VALID.matches(phone.replace(SEPARATORS, ""))) null else INVALID_PHONE
}
