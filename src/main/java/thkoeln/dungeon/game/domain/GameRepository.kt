package thkoeln.dungeon.game.domain

import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameRepository : CrudRepository<Game?, UUID?> {
    fun findByGameId(gameId: UUID?): List<Game?>?
    fun existsByGameId(gameId: UUID?): Boolean
    fun findAllByGameStatusEquals(gameStatus: GameStatus?): List<Game?>?
    fun findAllByGameStatusBetween(gameStatus1: GameStatus?, gameStatus2: GameStatus?): List<Game?>?
    override fun findAll(): List<Game?>
    fun save(game: Game?)
}