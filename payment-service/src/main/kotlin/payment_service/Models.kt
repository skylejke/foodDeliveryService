package payment_service

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val paymentId: Int,
    val title: String,
    val orderDetails: String,
    val amount: Double,
    val paymentMethod: String
)

@Serializable
data class Card(
    val cardId: Int,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String
)
