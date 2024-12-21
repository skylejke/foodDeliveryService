package order_service

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module(testing: Boolean = false) {
    configureLogging()

    configureDatabase(testing)

    val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = prometheusMeterRegistry
    }


    install(ContentNegotiation) {
        json()
    }
    routing {
        orderRouting()
    }
}

fun configureDatabase(testing: Boolean = false) {
    if (testing) {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    } else {
        Database.connect(
            url = "jdbc:postgresql://51.250.26.59:5432/ikbo_07_21_damarad_d_v_db",
            driver = "org.postgresql.Driver",
            user = "secUREusER",
            password = "StrongEnoughPassword)"
        )
    }
}


fun Application.configureLogging() {
    // При каждом запросе генерируем уникальный requestId и проставляем в MDC
    intercept(ApplicationCallPipeline.Setup) {
        val requestId = UUID.randomUUID().toString()
        MDC.put("requestId", requestId)
        call.response.headers.append("X-Request-ID", requestId)
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        val logger = LoggerFactory.getLogger("main-logger")

        // 1. Логируем, какой endpoint был вызван
        logger.info("Endpoint triggered: [${call.request.httpMethod.value}] ${call.request.uri}, RequestID=${MDC.get("requestId")}")

        // 2. Логируем параметры (query-параметры)
        val queryParams = call.request.queryParameters.flattenEntries().joinToString { "${it.first}=${it.second}" }
        logger.info("Parameters: $queryParams, RequestID=${MDC.get("requestId")}")

        proceed()

        // 3. Логируем результат - статус ответа
        logger.info("Response sent: [${call.response.status()}] ${call.request.uri}, RequestID=${MDC.get("requestId")}")

        MDC.clear()
    }
}


