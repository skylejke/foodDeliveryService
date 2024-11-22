package payment_service

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    Database.connect(
        url = "jdbc:postgresql://${System.getenv("POSTGRES_HOST")}:${System.getenv("POSTGRES_PORT")}/${System.getenv("POSTGRES_DB")}",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "baksik"
    )

    RabbitMQConsumer.startListening()
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

