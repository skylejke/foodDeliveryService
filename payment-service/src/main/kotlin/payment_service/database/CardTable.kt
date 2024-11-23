package payment_service.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate.parse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object CardTable : Table("cards") {
    val cardId = varchar("card_id", 50)
    val cardNumber = varchar("card_number", 16)
    val expiryDate = date("expiry_date")
    val cvv = varchar("cvv", 4)

    override val primaryKey = PrimaryKey(cardId)

    fun addCard(cardDTO: CardDTO) {
        return transaction {
            CardTable.insert {
                it[cardId] = cardDTO.cardId
                it[cardNumber] = cardDTO.cardNumber
                it[expiryDate] = parse(cardDTO.expiryDate)
                it[cvv] = cardDTO.cvv
            }
        }
    }

    fun deleteCard(cardId: String): Boolean {
        return transaction {
            val deletedRows = CardTable.deleteWhere { CardTable.cardId eq cardId }
            deletedRows > 0
        }
    }
}
