package order_service

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.paymentRouting() {
    routing {
        // Создать заказ
        post("/order") {
            val order = call.receive<Order>()
            OrderController.addOrder(order)
            call.respond(HttpStatusCode.Created, "Заказ успешно создан")
        }

        // Получить все заказы
        get("/order") {
            val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val orders = OrderController.getAllOrders(count, offset)
            call.respond(HttpStatusCode.OK, orders)
        }

        // Получить информацию о заказе
        get("/order/{orderId}") {
            val orderId = call.parameters["orderId"]?.toIntOrNull()
            if (orderId == null) {
                call.respond(HttpStatusCode.BadRequest, "Неверный идентификатор заказа")
                return@get
            }
            val order = OrderController.getOrderById(orderId)
            if (order == null) {
                call.respond(HttpStatusCode.NotFound, "Заказ не найден")
            } else {
                call.respond(HttpStatusCode.OK, order)
            }
        }

        // Выдать заказ
        patch("/order/{orderId}/issue") {
            updateOrderStatus(call, "Выдан")
        }

        // Отменить заказ
        patch("/order/{orderId}/cancel") {
            updateOrderStatus(call, "Отменен")
        }

        // Принять заказ
        patch("/order/{orderId}/accept") {
            updateOrderStatus(call, "Принят")
        }

        // Выполнить заказ
        patch("/order/{orderId}/complete") {
            updateOrderStatus(call, "Выполнен")
        }

        // Очистить корзину
        delete("/cart") {
            OrderController.clearCart()
            call.respond(HttpStatusCode.OK, "Корзина успешно очищена")
        }

        // Загрузить информацию о корзине
        get("/cart") {
            val items = OrderController.getCartItems()
            call.respond(HttpStatusCode.OK, items)
        }

        // Поместить блюдо в корзину
        post("/cart") {
            val item = call.receive<CartItem>()
            OrderController.addItemToCart(item)
            call.respond(HttpStatusCode.Created, "Блюдо успешно добавлено в корзину")
        }
    }
}


private suspend fun updateOrderStatus(call: ApplicationCall, newStatus: String) {
    val orderId = call.parameters["orderId"]?.toIntOrNull()
    if (orderId == null) {
        call.respond(HttpStatusCode.BadRequest, "Неверный идентификатор заказа")
        return
    }
    val updated = OrderController.updateOrderStatus(orderId, newStatus)
    if (updated) {
        call.respond(HttpStatusCode.OK, "Статус заказа обновлен на \"$newStatus\"")
    } else {
        call.respond(HttpStatusCode.NotFound, "Заказ не найден")
    }
}