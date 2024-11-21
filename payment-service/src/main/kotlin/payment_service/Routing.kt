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
        // Создать оплату
        post("/payment") {
            try {
                val paymentRequest = call.receive<CreatePaymentRequest>()
                val payment = paymentRequest.mapToPaymentDTO()
                val paymentId = PaymentController.addPayment(payment)
                call.respond(HttpStatusCode.Created, mapOf("paymentId" to paymentId))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Ошибка создания платежа: ${e.message}")
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
            val paymentId = call.parameters["paymentId"]?.toString()
            if (paymentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Неверный идентификатор платежа")
                return@get
            }
            val payment = PaymentController.getPaymentById(paymentId)
            if (payment == null) {
                call.respond(HttpStatusCode.NotFound, "Платеж не найден")
            } else {
                call.respond(HttpStatusCode.OK, payment)
            }
        }

        // Оплатить заказ
        patch("/payment/{paymentId}/pay") {
            val paymentId = call.parameters["paymentId"]?.toString()
            if (paymentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Неверный идентификатор платежа")
                return@patch
            }

            val success = PaymentController.payOrder(paymentId)
            if (success) {
                call.respond(HttpStatusCode.OK, "Заказ успешно оплачен")
            } else {
                call.respond(HttpStatusCode.NotFound, "Платеж не найден")
            }
        }

        // Привязать карту
        post("/payment/cards") {
            try {
                val cardRequest = call.receive<CreateCardRequest>()
                val card = cardRequest.mapToCardDTO()
                CardController.addCard(card)
                call.respond(HttpStatusCode.Created, "Карта успешно привязана")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Ошибка привязки карты: ${e.message}")
            }
        }

        // Удалить карту
        delete("/payment/cards/{cardId}") {
            val cardId = call.parameters["cardId"]?.toString()
            if (cardId == null) {
                call.respond(HttpStatusCode.BadRequest, "Неверный идентификатор карты")
                return@delete
            }
            val success = CardController.deleteCard(cardId)
            if (success) {
                call.respond(HttpStatusCode.OK, "Карта успешно удалена")
            } else {
                call.respond(HttpStatusCode.NotFound, "Карта не найдена")
            }
        }
    }
}
