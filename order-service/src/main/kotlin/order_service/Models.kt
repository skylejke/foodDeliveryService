package order_service

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val items: List<Meal>,
    val totalAmount: Double,
    val deliveryAddress: String? = null,
    val deliveryMethod: String,
)

@Serializable
data class Meal(
    val mealId: String,
    val name: String,
    val quantity: Int,
    val price: Double
)