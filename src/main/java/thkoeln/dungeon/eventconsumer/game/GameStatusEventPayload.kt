package thkoeln.dungeon.eventconsumer.game

import thkoeln.dungeon.game.domain.GameStatus
import java.util.*

class GameStatusEventPayload {
    lateinit var gameId: UUID
    lateinit var gameStatus: GameStatus
}