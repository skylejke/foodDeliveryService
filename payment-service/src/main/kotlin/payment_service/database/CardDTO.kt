package payment_service.database

import kotlinx.serialization.Serializable
import payment_service.CreateCardRequest
import java.util.UUID

@Serializable
data class CardDTO(
    val cardId: String,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String
)

fun CreateCardRequest.mapToCardDTO() =
    CardDTO(
        cardId = UUID.randomUUID().toString(),
        cardNumber = cardNumber,
        expiryDate = expiryDate,
        cvv = cvv
    )