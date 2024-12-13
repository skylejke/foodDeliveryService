package payment_service

import payment_service.database.CardDTO
import payment_service.database.CardTable

object CardController {
    fun addCard(cardDTO: CardDTO) = CardTable.addCard(cardDTO)
    fun deleteCard(cardId: String): Boolean = CardTable.deleteCard(cardId)
}