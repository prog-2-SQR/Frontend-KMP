package ar.edu.um.prog2.quiroga.turnosapp.domain.model

/** Datos que el usuario elige al registrarse (enunciado §5). El resto (id, roles, activación) lo pone el backend. */
data class Registration(
    val login: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val langKey: String = "es",
)
