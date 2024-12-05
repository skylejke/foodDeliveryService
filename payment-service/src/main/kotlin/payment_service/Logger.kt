package payment_service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.ConsoleAppender

object LoggerConfig {
    val logger: Logger = LoggerFactory.getLogger("main-logger")

    init {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext

        // File Appender
        val fileAppender = FileAppender<ILoggingEvent>().apply {
            setContext(context)
            name = "file"
            file = "./logs/app.log"
            encoder = PatternLayoutEncoder().apply {
                setContext(context)
                pattern = "{'time':'%d{yyyy-MM-dd HH:mm:ss}', 'level':'%level', 'thread':'%thread', 'requestId':'%X{RequestID}', 'message':'%msg'}%n"
                start()
            }
            start()
        }

        // Console Appender
        val consoleAppender = ConsoleAppender<ILoggingEvent>().apply {
            setContext(context)
            name = "console"
            encoder = PatternLayoutEncoder().apply {
                setContext(context)
                pattern = "%d{yyyy-MM-dd HH:mm:ss} [%level] [RequestID=%X{RequestID}] %msg%n"
                start()
            }
            start()
        }

        val rootLogger = context.getLogger("ROOT") as ch.qos.logback.classic.Logger
        rootLogger.detachAndStopAllAppenders()
        rootLogger.addAppender(fileAppender)
        rootLogger.addAppender(consoleAppender)
    }
}
