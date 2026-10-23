package ar.edu.um.prog2.quiroga.turnosapp.domain.validation

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AccountRulesTest {

    @Test
    fun loginAcceptsTheJhipsterCharactersAndLength() {
        assertNull(AccountRules.loginError("juan.perez"))
        assertNull(AccountRules.loginError("juan@example.com"))
        assertNotNull(AccountRules.loginError("ju"))
        assertNotNull(AccountRules.loginError("a".repeat(51)))
        assertNotNull(AccountRules.loginError("juan perez"))
    }

    @Test
    fun passwordMustHaveFourToOneHundredCharacters() {
        assertNull(AccountRules.passwordError("abcd"))
        assertNotNull(AccountRules.passwordError("abc"))
        assertNotNull(AccountRules.passwordError("a".repeat(101)))
    }

    @Test
    fun confirmationMustMatchThePassword() {
        assertNull(AccountRules.confirmationError("clave", "clave"))
        assertNotNull(AccountRules.confirmationError("clave", "otra"))
    }

    @Test
    fun emailIsOptionalButMustBeValidIfPresent() {
        assertNull(AccountRules.emailError(""))
        assertNull(AccountRules.emailError("juan@example.com"))
        assertNotNull(AccountRules.emailError("no-es-un-email"))
        assertNotNull(AccountRules.emailError("a@b"))
    }

    @Test
    fun namesHaveAtMostFiftyCharacters() {
        assertNull(AccountRules.nameError("Juan"))
        assertNotNull(AccountRules.nameError("a".repeat(51)))
    }
}
