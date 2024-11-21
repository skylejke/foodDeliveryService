package payment_service.database

import kotlinx.serialization.Serializable
import payment_service.CreatePaymentRequest
import java.util.*

@Serializable
data class PaymentDTO(
    val paymentId: String,
    val title: String,
    val details: String,
    val amount: Double,
    val paymentMethod: String
)

fun CreatePaymentRequest.mapToPaymentDTO(): PaymentDTO =
    PaymentDTO(
        paymentId = UUID.randomUUID().toString(),
        title = title,
        details = details,
        amount = amount,
        paymentMethod = paymentMethod
    )
