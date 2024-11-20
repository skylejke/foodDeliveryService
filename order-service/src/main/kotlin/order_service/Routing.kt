package order_service

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/order") {
            call.respondText("This is order service")
        }
    }
}