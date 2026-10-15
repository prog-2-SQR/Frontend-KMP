package ar.edu.um.prog2.quiroga.turnosapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform