package ru.food_delivery.order

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import order_service.CreateOrderRequest
import order_service.OrderController
import order_service.database.mapToOrderDTO
import order_service.RabbitMQProducer
import java.util.UUID

fun Application.orderRouting() {
    routing {
        get("/order/test"){
            call.respondText("This is order service")
        }

        // Создать заказ
        post("/order") {
            try {
                val createOrderRequest = call.receive<CreateOrderRequest>()

                if (createOrderRequest.totalAmount <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "Order creation error: the amount must be positive")
                    return@post
                }

                val order = createOrderRequest.mapToOrderDTO()
                val orderId = OrderController.addOrder(order)

                val paymentMessage = """
                                {
                                    "paymentId": "${UUID.randomUUID()}", 
                                    "title": "Order ${order.orderId} ",
                                    "details": "Order ${order.orderId} details",
                                    "amount": ${order.totalAmount},
                                    "paymentMethod": "Card"
                                }
                                """.trimIndent()

                RabbitMQProducer.sendMessage(paymentMessage)

                call.respond(HttpStatusCode.Created, mapOf("orderId" to orderId))
            } catch (e: Exception){
                call.respond(HttpStatusCode.BadRequest, "Order creation error: ${e.message}")
            }

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
            val orderId = call.parameters["orderId"]
            if (orderId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid order ID")
                return@get
            }
            val order = OrderController.getOrderById(orderId)
            if (order == null) {
                call.respond(HttpStatusCode.NotFound, "The order was not found")
            } else {
                call.respond(HttpStatusCode.OK, order)
            }
        }

        // Выдать заказ
        patch("/order/{orderId}/issue") {
            updateOrderStatus(call, "Issued")
        }

        // Отменить заказ
        patch("/order/{orderId}/cancel") {
            updateOrderStatus(call, "Canceled")
        }

        // Принять заказ
        patch("/order/{orderId}/accept") {
            updateOrderStatus(call, "Accepted")
        }

        // Выполнить заказ
        patch("/order/{orderId}/complete") {
            updateOrderStatus(call, "Completed")
        }
    }
}

private suspend fun updateOrderStatus(call: ApplicationCall, newStatus: String) {
    val orderId = call.parameters["orderId"]
    if (orderId == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid order ID")
        return
    }
    val updated = OrderController.updateOrderStatus(orderId, newStatus)
    if (updated) {
        call.respond(HttpStatusCode.OK, "The order status has been updated to \"$newStatus\"")
    } else {
        call.respond(HttpStatusCode.NotFound, "The order was not found")
    }
}
