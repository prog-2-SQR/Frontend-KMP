package ar.edu.um.prog2.quiroga.turnosapp.domain.validation

/**
 * Las mismas reglas que valida el backend de turnos (anexo §5.1 y contrato de JHipster). La app avisa antes de
 * mandar el formulario; el backend igual vuelve a validar. Cada función devuelve el mensaje de error o null si está bien.
 */
object AccountRules {

    private val LOGIN_REGEX =
        Regex("^(?>[a-zA-Z0-9!\$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)\$")
    private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+\$")

    fun loginError(login: String): String? = when {
        login.length !in 3..50 -> "El usuario debe tener entre 3 y 50 caracteres"
        !LOGIN_REGEX.matches(login) -> "El usuario solo admite letras, números y . _ @ -"
        else -> null
    }

    fun passwordError(password: String): String? =
        if (password.length in 4..100) null else "La contraseña debe tener entre 4 y 100 caracteres"

    fun confirmationError(password: String, confirmation: String): String? =
        if (password == confirmation) null else "Las contraseñas no coinciden"

    // El email es opcional: vacío está bien.
    fun emailError(email: String): String? = when {
        email.isEmpty() -> null
        email.length !in 5..254 || !EMAIL_REGEX.matches(email) -> "Email inválido"
        else -> null
    }

    fun nameError(name: String): String? = if (name.length <= 50) null else "Máximo 50 caracteres"
}
