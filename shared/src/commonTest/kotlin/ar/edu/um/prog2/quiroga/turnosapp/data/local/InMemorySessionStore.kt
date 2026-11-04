package ar.edu.um.prog2.quiroga.turnosapp.data.local

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import kotlin.io.encoding.Base64

/** Sesión guardada en memoria, en lugar de las preferencias del teléfono. */
class InMemorySessionStore(var saved: AuthSession? = null) : SessionStore {

    override fun load(): AuthSession? = saved

    override fun save(session: AuthSession) {
        saved = session
    }

    override fun clear() {
        saved = null
    }
}

/** Un JWT de prueba con el vencimiento pedido. La firma no importa: la app no la valida. */
fun tokenExpiringAt(epochSeconds: Long): String {
    val base64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
    val header = base64.encode("""{"alg":"HS512"}""".encodeToByteArray())
    val payload = base64.encode("""{"sub":"juan","exp":$epochSeconds}""".encodeToByteArray())
    return "$header.$payload.firma"
}
