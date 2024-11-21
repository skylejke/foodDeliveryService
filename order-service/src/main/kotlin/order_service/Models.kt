package order_service

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val orderId: Int,
    val items: List<CartItem>,
    val totalAmount: String,
    val deliveryAddress: String? = null,
    val deliveryMethod: String,
    val status: String,
    val createdAt: String
)

@Serializable
data class CartItem(
    val mealId: Int,
    val name: String,
    val quantity: Int,
    val price: Double
)