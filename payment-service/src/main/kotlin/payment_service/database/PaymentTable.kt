package payment_service.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


object PaymentTable : Table("payments") {
    val paymentId = varchar("payment_id", 50)
    val title = varchar("title", 255)
    val details = varchar("order_details", 255)
    val amount = decimal("amount", 10, 2)
    val paymentMethod = varchar("payment_method", 100)

    override val primaryKey = PrimaryKey(paymentId)

    fun addPayment(paymentDTO: PaymentDTO): String {
        return transaction {
            PaymentTable.insert {
                it[paymentId] = paymentDTO.paymentId
                it[title] = paymentDTO.title
                it[details] = paymentDTO.details
                it[amount] = paymentDTO.amount.toBigDecimal()
                it[paymentMethod] = paymentDTO.paymentMethod
            } get paymentId
        }
    }

    fun getAllPayments(): List<PaymentDTO> {
        return transaction {
            PaymentTable.selectAll().map {
                PaymentDTO(
                    paymentId = it[paymentId],
                    title = it[title],
                    details = it[details],
                    amount = it[amount].toDouble(),
                    paymentMethod = it[paymentMethod]
                )
            }
        }
    }

    fun getPaymentById(paymentId: String): PaymentDTO? {
        return transaction {
            PaymentTable.select { PaymentTable.paymentId eq paymentId }
                .mapNotNull {
                    PaymentDTO(
                        paymentId = it[PaymentTable.paymentId],
                        title = it[title],
                        details = it[details],
                        amount = it[amount].toDouble(),
                        paymentMethod = it[paymentMethod]
                    )
                }.singleOrNull()
        }
    }
}