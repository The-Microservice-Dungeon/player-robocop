package thkoeln.dungeon.eventconsumer.game

import lombok.*
import org.springframework.messaging.MessageHeaders
import thkoeln.dungeon.eventconsumer.core.AbstractEvent
import thkoeln.dungeon.game.domain.GameStatus
import java.util.*
import javax.persistence.Entity

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class GameStatusEvent(messageHeaders: MessageHeaders, gameStatusEventPayload: GameStatusEventPayload) :
    AbstractEvent(messageHeaders) {
    var gameStatus: GameStatus? = null
    var gameId: UUID? = null

    init {
        this.gameStatus = gameStatusEventPayload.gameStatus
        this.gameId = gameStatusEventPayload.gameId
    }

    companion object {
        private const val TYPE_KEY = "type"
        private const val GAME_ID_KEY = "gameId"
    }
}