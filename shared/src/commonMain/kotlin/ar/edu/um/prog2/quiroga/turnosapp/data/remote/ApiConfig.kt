package ar.edu.um.prog2.quiroga.turnosapp.data.remote

/**
 * Dónde están los backends. 10.0.2.2 es la PC de desarrollo vista desde el emulador de Android.
 * La app habla con los dos: cuentas y turnos con Turnos, búsqueda de profesionales con el Catálogo
 * (contrato turnos-catalogo, "otras operaciones disponibles": las usa la app).
 */
object ApiConfig {

    const val TURNOS_BASE_URL = "http://10.0.2.2:8081"
    const val CATALOGO_BASE_URL = "http://10.0.2.2:8080"
}
