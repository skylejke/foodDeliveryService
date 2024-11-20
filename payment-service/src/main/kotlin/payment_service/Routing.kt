package payment_service

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.orderRouting() {
    routing {
        // Создать оплату
        post("/payment") {
            val payment = call.receive<Payment>()
            PaymentRepository.addPayment(payment)
            call.respond(HttpStatusCode.Created, "Платеж успешно создан")
        }

        // Получить все оплаты
        get("/payment") {
            val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val payments = PaymentRepository.getAllPayments(count, offset)
            call.respond(HttpStatusCode.OK, payments)
        }

        // Получить информацию об оплате
        get("/payment/{paymentId}") {
            val paymentId = call.parameters["paymentId"]?.toIntOrNull()
            if (paymentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Неверный идентификатор платежа")
                return@get
            }
            val payment = PaymentRepository.getPaymentById(paymentId)
            if (payment == null) {
                call.respond(HttpStatusCode.NotFound, "Платеж не найден")
            } else {
                call.respond(HttpStatusCode.OK, payment)
            }
        }

        // Оплатить заказ
        patch("/payment/{paymentId}/pay") {
            val paymentId = call.parameters["paymentId"]?.toIntOrNull()
            if (paymentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Неверный идентификатор платежа")
                return@patch
            }

            val success = PaymentRepository.payOrder(paymentId)
            if (success) {
                call.respond(HttpStatusCode.OK, "Заказ успешно оплачен")
            } else {
                call.respond(HttpStatusCode.NotFound, "Платеж не найден")
            }
        }

        // Привязать карту
        post("/payment/cards") {
            val card = call.receive<Card>()
            PaymentRepository.addCard(card)
            call.respond(HttpStatusCode.Created, "Карта успешно привязана")
        }

        // Удалить карту
        delete("/payment/cards/{cardId}") {
            val cardId = call.parameters["cardId"]?.toIntOrNull()
            if (cardId == null) {
                call.respond(HttpStatusCode.BadRequest, "Неверный идентификатор карты")
                return@delete
            }
            val success = PaymentRepository.removeCard(cardId)
            if (success) {
                call.respond(HttpStatusCode.OK, "Карта успешно удалена")
            } else {
                call.respond(HttpStatusCode.NotFound, "Карта не найдена")
            }
        }
    }
}
