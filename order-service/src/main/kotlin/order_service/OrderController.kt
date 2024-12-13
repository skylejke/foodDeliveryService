package order_service

import order_service.database.OrderDTO
import order_service.database.OrderTable

object OrderController {
    fun addOrder(orderDTO: OrderDTO): String = OrderTable.addOrder(orderDTO)

    fun getAllOrders(count: Int, offset: Int): List<OrderDTO> =
        OrderTable.getAllOrders()
            .drop(offset)
            .take(count)


    fun getOrderById(orderId: String): OrderDTO? = OrderTable.getOrderById(orderId)

    fun updateOrderStatus(orderId: String, newStatus: String): Boolean =
        OrderTable.updateOrderStatus(orderId, newStatus)
}