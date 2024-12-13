package order_service.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import order_service.Meal
import order_service.database.MealTable.mealId
import order_service.database.MealTable.name
import order_service.database.MealTable.price
import order_service.database.MealTable.quantity
import java.time.Instant
import java.time.ZoneOffset

object OrderTable : Table("orders") {
    internal val orderId = varchar("order_id", 50)
    private val totalAmount = double("total_amount")
    private val deliveryAddress = varchar("delivery_address", 255).nullable()
    private val deliveryMethod = varchar("delivery_method", 50)
    private val status = varchar("status", 20)
    private val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(orderId)

    fun addOrder(orderDTO: OrderDTO): String {
        return transaction {
            OrderTable.insert {
                it[orderId] = orderDTO.orderId
                it[totalAmount] = orderDTO.totalAmount
                it[deliveryAddress] = orderDTO.deliveryAddress
                it[deliveryMethod] = orderDTO.deliveryMethod
                it[status] = orderDTO.status
                it[createdAt] = Instant.parse(orderDTO.createdAt).atZone(ZoneOffset.UTC).toLocalDateTime()
            }
            orderDTO.items.forEach { item ->
                MealTable.insert {
                    it[mealId] = item.mealId
                    it[orderId] = orderDTO.orderId
                    it[name] = item.name
                    it[quantity] = item.quantity
                    it[price] = item.price
                }
            }
            orderDTO.orderId
        }
    }

    fun getAllOrders(): List<OrderDTO> {
        return transaction {
            OrderTable.selectAll()
                .map { orderRow ->
                    val items = MealTable.select { MealTable.orderId eq orderRow[orderId] }
                        .map {
                            Meal(
                                mealId = it[mealId],
                                name = it[name],
                                quantity = it[quantity],
                                price = it[price]
                            )
                        }
                    OrderDTO(
                        orderId = orderRow[orderId],
                        totalAmount = orderRow[totalAmount],
                        deliveryAddress = orderRow[deliveryAddress],
                        deliveryMethod = orderRow[deliveryMethod],
                        status = orderRow[status],
                        createdAt = orderRow[createdAt].toInstant(ZoneOffset.UTC).toString(),
                        items = items
                    )
                }
        }
    }

    fun getOrderById(orderId: String): OrderDTO? {
        return transaction {
            val orderRow = OrderTable.select { OrderTable.orderId eq orderId }.singleOrNull() ?: return@transaction null
            val items = MealTable.select { MealTable.orderId eq orderId }
                .map {
                    Meal(
                        mealId = it[mealId],
                        name = it[name],
                        quantity = it[quantity],
                        price = it[price]
                    )
                }
            OrderDTO(
                orderId = orderRow[OrderTable.orderId],
                totalAmount = orderRow[totalAmount],
                deliveryAddress = orderRow[deliveryAddress],
                deliveryMethod = orderRow[deliveryMethod],
                status = orderRow[status],
                createdAt = orderRow[createdAt].toInstant(ZoneOffset.UTC).toString(),
                items = items
            )
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String): Boolean {
        return transaction {
            val updatedRows = OrderTable.update({ OrderTable.orderId eq orderId }) {
                it[status] = newStatus
            }
            updatedRows > 0
        }
    }
}