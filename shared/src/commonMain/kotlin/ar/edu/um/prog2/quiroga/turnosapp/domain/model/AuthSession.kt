package ar.edu.um.prog2.quiroga.turnosapp.domain.model

/** Sesión iniciada: el login del usuario y el JWT que la app manda en cada request (Authorization: Bearer). */
data class AuthSession(
    val login: String,
    val token: String,
)
