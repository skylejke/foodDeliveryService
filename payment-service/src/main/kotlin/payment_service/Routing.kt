package payment_service

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import payment_service.database.mapToCardDTO
import payment_service.database.mapToPaymentDTO

fun Application.orderRouting() {
    routing {
        get("/payment/test") {
            call.respondText("This is payment service")
        }

        // Создать оплату
        post("/payment") {
            try {
                val paymentRequest = call.receive<CreatePaymentRequest>()
                val payment = paymentRequest.mapToPaymentDTO()
                val paymentId = PaymentController.addPayment(payment)
                call.respond(HttpStatusCode.Created, mapOf("paymentId" to paymentId))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Payment creation error: ${e.message}")
            }
        }

        // Получить все оплаты
        get("/payment") {
            val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val payments = PaymentController.getAllPayments(count, offset)
            call.respond(HttpStatusCode.OK, payments)
        }

        // Получить информацию об оплате
        get("/payment/{paymentId}") {
            val paymentId = call.parameters["paymentId"]
            if (paymentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid payment ID")
                return@get
            }
            val payment = PaymentController.getPaymentById(paymentId)
            if (payment == null)
                call.respond(HttpStatusCode.NotFound, "The payment was not found")
            else
                call.respond(HttpStatusCode.OK, payment)
        }

        // Оплатить заказ
        patch("/payment/{paymentId}/pay") {
            val paymentId = call.parameters["paymentId"]
            if (paymentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid payment ID")
                return@patch
            }
            if (PaymentController.payOrder(paymentId))
                call.respond(HttpStatusCode.OK, "The order has been successfully paid for")
            else
                call.respond(HttpStatusCode.NotFound, "The payment was not found")
        }

        // Привязать карту
        post("/payment/cards") {
            try {
                val cardRequest = call.receive<CreateCardRequest>()
                val card = cardRequest.mapToCardDTO()
                CardController.addCard(card)
                call.respond(HttpStatusCode.Created, "The card has been successfully linked")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error linking the card: ${e.message}")
            }
        }

        // Удалить карту
        delete("/payment/cards/{cardId}") {
            val cardId = call.parameters["cardId"]
            if (cardId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid card ID")
                return@delete
            }
            if (CardController.deleteCard(cardId))
                call.respond(HttpStatusCode.OK, "The card was successfully deleted")
            else
                call.respond(HttpStatusCode.NotFound, "The card was not found")
        }
    }
}
