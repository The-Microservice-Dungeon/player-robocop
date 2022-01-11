package thkoeln.dungeon.player.domain

import org.springframework.data.repository.CrudRepository
import thkoeln.dungeon.game.domain.Game
import java.util.*

interface PlayerRepository : CrudRepository<Player?, UUID?> {
    override fun findAll(): MutableList<Player?>
    fun findByGameParticipations_Game(game: Game?): List<Player?>?
    abstract fun save(player: Player?)
}