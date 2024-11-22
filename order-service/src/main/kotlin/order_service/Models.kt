package order_service

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val orderId: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val deliveryAddress: String? = null,
    val deliveryMethod: String,
    val status: String,
    val createdAt: String
)

@Serializable
data class CartItem(
    val mealId: String,
    val name: String,
    val quantity: Int,
    val price: Double
)