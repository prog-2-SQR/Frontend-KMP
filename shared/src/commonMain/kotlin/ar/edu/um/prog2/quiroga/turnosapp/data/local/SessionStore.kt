package ar.edu.um.prog2.quiroga.turnosapp.data.local

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import com.russhwolf.settings.Settings

/** Dónde queda guardada la sesión entre una apertura de la app y la siguiente ("Recordarme"). Los tests usan una en memoria. */
interface SessionStore {

    fun load(): AuthSession?

    fun save(session: AuthSession)

    fun clear()
}

/**
 * La sesión en las preferencias del teléfono, con multiplatform-settings (en Android, SharedPreferences privadas de la
 * app). Settings() sin argumentos usa las preferencias por defecto: no hace falta pasarle el Context de Android.
 */
class SettingsSessionStore(private val settings: Settings = Settings()) : SessionStore {

    override fun load(): AuthSession? {
        val login = settings.getStringOrNull(LOGIN) ?: return null
        val token = settings.getStringOrNull(TOKEN) ?: return null
        return AuthSession(login = login, token = token)
    }

    override fun save(session: AuthSession) {
        settings.putString(LOGIN, session.login)
        settings.putString(TOKEN, session.token)
    }

    override fun clear() {
        settings.remove(LOGIN)
        settings.remove(TOKEN)
    }

    private companion object {
        const val LOGIN = "session.login"
        const val TOKEN = "session.token"
    }
}
