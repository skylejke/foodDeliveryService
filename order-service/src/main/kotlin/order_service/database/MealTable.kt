package order_service.database

import org.jetbrains.exposed.sql.Table

object MealTable : Table("meals") {
    val mealId = varchar("meal_id", 50)
    val orderId = varchar("order_id", 36).references(OrderTable.orderId)
    val name = varchar("name", 100)
    val quantity = integer("quantity")
    val price = double("price")

    override val primaryKey = PrimaryKey(mealId)
}
