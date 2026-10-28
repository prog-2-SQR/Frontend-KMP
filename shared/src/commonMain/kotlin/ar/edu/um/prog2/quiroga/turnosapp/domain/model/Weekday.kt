package ar.edu.um.prog2.quiroga.turnosapp.domain.model

/** Días de atención. El nombre de cada constante es el valor que espera el backend (MONDAY … SUNDAY, anexo §7). */
enum class Weekday(val label: String) {
    MONDAY("Lunes"),
    TUESDAY("Martes"),
    WEDNESDAY("Miércoles"),
    THURSDAY("Jueves"),
    FRIDAY("Viernes"),
    SATURDAY("Sábado"),
    SUNDAY("Domingo"),
}
