package ar.edu.um.prog2.quiroga.turnosapp.data.local

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.io.encoding.Base64
import kotlin.time.Instant

/**
 * Lee cuándo vence un JWT (claim exp) para no restaurar una sesión vencida. No valida la firma: eso lo hace el backend
 * en cada request. Un JWT son tres partes en Base64 separadas por puntos; el vencimiento está en la del medio.
 */
object JwtExpiry {

    // Los JWT usan Base64 para URL y sin el relleno "=" del final.
    private val base64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

    /** null si el token no tiene el formato esperado o no trae exp. */
    fun of(token: String): Instant? = runCatching {
        val payload = base64.decode(token.split('.')[1]).decodeToString()
        val exp = Json.parseToJsonElement(payload).jsonObject.getValue("exp").jsonPrimitive.long
        Instant.fromEpochSeconds(exp)
    }.getOrNull()
}
