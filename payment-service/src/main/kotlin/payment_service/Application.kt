package payment_service

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.slf4j.MDC
import java.util.UUID

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
    LoggerConfig // Инициализация логгера
    configureLogging()
    install(ContentNegotiation) {
        json()
    }
    routing {
        orderRouting()
    }
}

fun Application.configureLogging() {
    intercept(ApplicationCallPipeline.Setup) {
        // Генерация уникального RequestID
        val requestId = UUID.randomUUID().toString()
        MDC.put("RequestID", requestId)

        // Добавляем RequestID в заголовки ответа
        call.response.headers.append("X-Request-ID", requestId)
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        val logger = LoggerConfig.logger

        // Логирование запроса
        logger.info("Request received: [${call.request.httpMethod.value}] ${call.request.uri}, RequestID=${MDC.get("RequestID")}")

        proceed()

        // Логирование ответа
        logger.info("Response sent: [${call.response.status()}] ${call.request.uri}, RequestID=${MDC.get("RequestID")}")

        // Очищаем MDC после обработки
        MDC.clear()
    }
}
