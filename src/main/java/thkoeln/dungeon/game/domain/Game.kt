package thkoeln.dungeon.game.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.*
import org.slf4j.LoggerFactory
import thkoeln.dungeon.game.domain.Game
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

@Entity
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
class Game {
    @Id
    var id: UUID = UUID.randomUUID()

    // this is the EXTERNAL id that we receive from GameService. We could use this also as our own id, but then
    // we'll run into problems in case GameService messes up their ids (e.g. start the same game twice, etc.) So,
    // we better keep these two apart.
    var gameId: UUID? = null
    var gameStatus: GameStatus? = null
    var currentRoundCount: Int? = null

    @Transient
    private val logger = LoggerFactory.getLogger(Game::class.java)
    fun resetToNewlyCreated() {
        this.gameStatus = GameStatus.CREATED
        this.currentRoundCount = 0
        logger.warn("Reset game $this to CREATED!")
    }

    fun makeOrphan() {
        this.gameStatus = GameStatus.ORPHANED
        logger.warn("Marked game $this as ORPHANED!")
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val game = o as Game
        return id == game.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newlyCreatedGame(gameId: UUID?): Game {
            val game = Game()
            if (gameId != null) {
                game.id = gameId
            }
            game.resetToNewlyCreated()
            return game
        }
    }
}