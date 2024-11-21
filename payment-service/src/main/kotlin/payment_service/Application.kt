package payment_service

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/ma_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "baksik"
    )

    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        orderRouting()
    }
}

