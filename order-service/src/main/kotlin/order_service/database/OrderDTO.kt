package order_service.database

import kotlinx.serialization.Serializable
import order_service.Meal
import order_service.CreateOrderRequest
import java.time.Instant
import java.util.UUID

@Serializable
data class OrderDTO(
    val orderId: String,
    val items: List<Meal>,
    val totalAmount: Double,
    val deliveryAddress: String? = null,
    val deliveryMethod: String,
    val status: String,
    val createdAt: String
)

fun CreateOrderRequest.mapToOrderDTO(): OrderDTO =
    OrderDTO(
        orderId = UUID.randomUUID().toString(),
        items = items,
        totalAmount = totalAmount,
        deliveryAddress = deliveryAddress,
        deliveryMethod = deliveryMethod,
        status = "Created",
        createdAt = Instant.now().toString()
    )