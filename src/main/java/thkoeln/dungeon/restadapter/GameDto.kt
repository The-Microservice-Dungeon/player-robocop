package thkoeln.dungeon.restadapter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.*
import thkoeln.dungeon.game.domain.GameStatus
import java.util.*

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
data class GameDto (
    var gameId: UUID? = null,
    var gameStatus: GameStatus? = null,
    var currentRoundCount: Int? = null,
)