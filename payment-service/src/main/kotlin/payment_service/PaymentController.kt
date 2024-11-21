package payment_service

import payment_service.database.PaymentDTO
import payment_service.database.PaymentTable

object PaymentController {
    fun addPayment(paymentDTO: PaymentDTO): String = PaymentTable.addPayment(paymentDTO)


    fun getAllPayments(count: Int, offset: Int): List<PaymentDTO> {
        return PaymentTable.getAllPayments()
            .drop(offset)
            .take(count)
    }

    fun getPaymentById(paymentId: String): PaymentDTO? = PaymentTable.getPaymentById(paymentId)


    fun payOrder(paymentId: String): Boolean {
        val payment = PaymentTable.getAllPayments().find { it.paymentId == paymentId }
        return payment != null
        // Логика обработки успешной оплаты
        // Например, обновление статуса оплаты (если нужно)
    }
}
