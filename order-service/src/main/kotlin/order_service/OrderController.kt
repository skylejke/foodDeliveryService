package order_service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

object OrderController {
    private val orders = ConcurrentHashMap<Int, Order>()
    private val cart = ConcurrentLinkedQueue<CartItem>()

    fun addOrder(order: Order) {
        orders[order.orderId] = order
    }

    fun getAllOrders(count: Int, offset: Int): List<Order> {
        return orders.values.toList().drop(offset).take(count)
    }

    fun getOrderById(orderId: Int): Order? {
        return orders[orderId]
    }

    fun updateOrderStatus(orderId: Int, newStatus: String): Boolean {
        val order = orders[orderId]
        return if (order != null) {
            orders[orderId] = order.copy(status = newStatus)
            true
        } else {
            false
        }
    }

    fun clearCart() {
        cart.clear()
    }

    fun getCartItems(): List<CartItem> {
        return cart.toList()
    }

    fun addItemToCart(item: CartItem) {
        cart.add(item)
    }
}