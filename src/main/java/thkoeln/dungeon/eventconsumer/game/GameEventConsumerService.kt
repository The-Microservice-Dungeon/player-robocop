package thkoeln.dungeon.eventconsumer.game

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import thkoeln.dungeon.game.application.GameApplicationService

@Service
class GameEventConsumerService @Autowired constructor(
    private val gameApplicationService: GameApplicationService,
    private val gameStatusEventRepository: GameStatusEventRepository
) {
    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     * --- this extra line for testing purposes ---
     */
    //@KafkaListener( topics = "status" )  // that is what the documentation says
    fun consumeGameStatusEvent(@Payload gameStatusEventPayload: GameStatusEventPayload, headers: MessageHeaders) {
        val gameStatusEvent = GameStatusEvent(headers, gameStatusEventPayload)
        gameStatusEventRepository.save(gameStatusEvent)
        gameApplicationService.gameStatusExternallyChanged(gameStatusEvent.gameId, gameStatusEvent.gameStatus)
    }

    fun consumeNewRoundStartedEvent() {
        // todo
    }
}