package payment_service

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.serialization.json.Json
import payment_service.database.PaymentDTO

object RabbitMQConsumer {
    private const val QUEUE_NAME = "create_payment"

    private val factory = ConnectionFactory().apply {
        host = System.getenv("RABBITMQ_HOST") ?: "51.250.26.59"
        port = System.getenv("RABBITMQ_PORT")?.toInt() ?: 5672
        username = System.getenv("RABBITMQ_DEFAULT_USER") ?: "guest"
        password = System.getenv("RABBITMQ_DEFAULT_PASS") ?: "guest123"
    }

    private val connection by lazy {
        factory.newConnection()
    }
    private val channel by lazy {
        connection.createChannel().apply {
            queueDeclare(QUEUE_NAME, true, false, false, null)
        }
    }

    fun startListening() {
        val deliverCallback = DeliverCallback { _, delivery ->
            val message = String(delivery.body, Charsets.UTF_8)
            println("Message received: $message")

            try {
                val payment = Json.decodeFromString<PaymentDTO>(message)
                PaymentController.addPayment(payment)
                println("Payment created successfully: $payment")
            } catch (e: Exception) {
                println("Failed to process message: ${e.message}")
            }
        }

        channel.basicConsume(QUEUE_NAME, true, deliverCallback) { _ -> }
        println("RabbitMQ consumer started, waiting for messages...")
    }
}
