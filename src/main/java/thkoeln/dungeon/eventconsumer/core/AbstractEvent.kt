package thkoeln.dungeon.eventconsumer.core

import lombok.*
import org.slf4j.LoggerFactory
import org.springframework.messaging.MessageHeaders
import thkoeln.dungeon.player.application.PlayerApplicationService
import java.util.*
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Transient

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractEvent(messageHeaders: MessageHeaders) {
    @Id
    @Setter(AccessLevel.NONE)
    protected var id = UUID.randomUUID()

    protected var eventId: UUID? = null
    protected var timestamp: Long? = null
    protected var transactionId: UUID? = null

    @Transient
    protected var logger = LoggerFactory.getLogger(PlayerApplicationService::class.java)

    init {
        this.eventId = messageHeaders.id
        this.timestamp = messageHeaders.timestamp
        try {
            this.transactionId = UUID.fromString(messageHeaders[TRANSACTION_ID_KEY].toString())
        } catch (e: IllegalArgumentException) {
            logger.warn("Event $eventId at time $timestamp doesn't have a transactionId.")
        }
    }

    companion object {
        private const val TRANSACTION_ID_KEY = "transactionId"
    }
}