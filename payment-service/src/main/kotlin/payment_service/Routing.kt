package payment_service

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/payment") {
            call.respondText("This is payment service")
        }
    }
}
