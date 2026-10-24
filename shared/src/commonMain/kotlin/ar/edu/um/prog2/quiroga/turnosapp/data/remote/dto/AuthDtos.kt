package ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Body de POST /api/register (contrato de JHipster del backend de turnos). */
@Serializable
data class RegisterRequestDto(
    val login: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val langKey: String,
)

/** Body de POST /api/authenticate. */
@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String,
    val rememberMe: Boolean,
)

/** Respuesta del login: {"id_token": "..."}. */
@Serializable
data class TokenResponseDto(
    @SerialName("id_token") val idToken: String,
)

/** Error del backend en problem+json (anexo §13): la app decide por el code, nunca por el texto. */
@Serializable
data class ProblemDto(
    val status: Int? = null,
    val code: String? = null,
    val detail: String? = null,
)
