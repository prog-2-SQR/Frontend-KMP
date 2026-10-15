package ar.edu.um.prog2.quiroga.turnosapp

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello(platform.name)
    }
}