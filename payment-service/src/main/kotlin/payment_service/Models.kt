package payment_service

import kotlinx.serialization.Serializable

@Serializable
data class CreatePaymentRequest(
    val title: String,
    val details: String,
    val amount: Double,
    val paymentMethod: String
)

@Serializable
data class CreateCardRequest(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String
)
