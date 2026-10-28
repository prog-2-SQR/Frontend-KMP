package ar.edu.um.prog2.quiroga.turnosapp.domain.model

/** Profesional del catálogo. Deshabilitado = baja lógica: se puede ver, pero no reservar. */
data class Professional(
    val id: Long,
    val categoryId: Long,
    val firstName: String,
    val lastName: String,
    val enabled: Boolean,
) {
    val fullName: String get() = "$firstName $lastName"
}
