package order_service

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory

object RabbitMQProducer {
    private const val QUEUE_NAME = "create_payment"

    private val factory = ConnectionFactory().apply {
        host = System.getenv("RABBITMQ_HOST") ?: "51.250.26.59"
        port = System.getenv("RABBITMQ_PORT")?.toInt() ?: 5672
        username = System.getenv("RABBITMQ_DEFAULT_USER") ?: "guest"
        password = System.getenv("RABBITMQ_DEFAULT_PASS") ?: "guest123"
    }

    private val connection = factory.newConnection()
    private val channel: Channel = connection.createChannel()

    init {
        channel.queueDeclare(QUEUE_NAME, true, false, false, null)
    }

    fun sendMessage(message: String) {
        channel.basicPublish("", QUEUE_NAME, null, message.toByteArray(Charsets.UTF_8))
        println("Message sent to queue: $message")
    }
}
