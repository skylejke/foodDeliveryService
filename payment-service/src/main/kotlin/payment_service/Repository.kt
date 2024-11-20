package payment_service

object PaymentRepository {
    private val payments = mutableListOf<Payment>()
    private val cards = mutableListOf<Card>()

    fun addPayment(payment: Payment) {
        payments.add(payment)
    }

    fun getAllPayments(count: Int, offset: Int): List<Payment> {
        return payments.drop(offset).take(count)
    }

    fun getPaymentById(paymentId: Int): Payment? {
        return payments.find { it.paymentId == paymentId }
    }

    fun payOrder(paymentId: Int): Boolean {
        val payment = payments.find { it.paymentId == paymentId }
        return if (payment != null) {
            // Логика обработки успешной оплаты
            // Например, обновление статуса оплаты (если нужно)
            true
        } else {
            false
        }
    }

    fun addCard(card: Card) {
        cards.add(card)
    }

    fun removeCard(cardId: Int): Boolean {
        return cards.removeIf { it.cardId == cardId }
    }
}
