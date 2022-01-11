package thkoeln.dungeon.game.application

import org.modelmapper.ModelMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import thkoeln.dungeon.game.domain.Game
import thkoeln.dungeon.game.domain.GameException
import thkoeln.dungeon.game.domain.GameRepository
import thkoeln.dungeon.game.domain.GameStatus
import thkoeln.dungeon.player.application.PlayerApplicationService
import thkoeln.dungeon.restadapter.GameDto
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException
import java.util.*

@Service
class GameApplicationService @Autowired constructor(
    private val gameRepository: GameRepository,
    private val gameServiceRESTAdapter: GameServiceRESTAdapter,
    playerApplicationService: PlayerApplicationService?
) {
    private val logger = LoggerFactory.getLogger(GameApplicationService::class.java)
    var modelMapper = ModelMapper()
    fun retrieveActiveGames(): List<Game?>? {
        return gameRepository.findAllByGameStatusEquals(GameStatus.GAME_RUNNING)
    }

    /**
     * Makes sure that our own game state is consistent with what GameService says.
     * We take a very simple approach here. We, as a Player, don't manage any game
     * state - we just assume that GameService does a proper job. So we just store
     * the incoming games. Only in the case that a game should suddenly "disappear",
     * we keep it and mark it as ORPHANED - there may be local references to it.
     */
    fun synchronizeGameState() {
        var gameDtos: Array<GameDto?>? = arrayOfNulls(0)
        try {
            gameDtos = gameServiceRESTAdapter.fetchCurrentGameState()
        } catch (e: UnexpectedRESTException) {
            logger.warn(
                """
    Problems with GameService while synchronizing game state - need to try again later.
    ${e.stackTrace}
    """.trimIndent()
            )
        } catch (e: RESTConnectionFailureException) {
            logger.warn(
                """
    Problems with GameService while synchronizing game state - need to try again later.
    ${e.stackTrace}
    """.trimIndent()
            )
        }

        // We need to treat the new games (those we haven't stored yet) and those we
        // already have in a different way. Therefore let's split the list.
        val unknownGameDtos: MutableList<GameDto?> = ArrayList()
        val knownGameDtos: MutableList<GameDto?> = ArrayList()
        for (gameDto in gameDtos!!) {
            if (gameRepository.existsByGameId(gameDto?.gameId)) knownGameDtos.add(gameDto)
            else unknownGameDtos.add(gameDto)
        }
        val storedGames = gameRepository.findAll()
        for (game in storedGames) {
            val foundDtoOptional = knownGameDtos.stream()
                .filter { dto: GameDto? -> game?.id == dto?.gameId }.findAny()
            if (foundDtoOptional.isPresent) {
                modelMapper.map(foundDtoOptional.get(), game)
                gameRepository.save(game)
                logger.info("Updated game $game")
            } else {
                game!!.makeOrphan()
                gameRepository.save(game)
            }
        }
        for (gameDto in unknownGameDtos) {
            val game = modelMapper.map(gameDto, Game::class.java)
            gameRepository.save(game)
            logger.info("Received game $game for the first time")
        }
        logger.info("Retrieval of new game state finished")
    }

    /**
     * "Status changed" event published by GameService, esp. after a game has been created
     */
    fun gameStatusExternallyChanged(gameId: UUID?, gameStatus: GameStatus?) {
        when (gameStatus) {
            GameStatus.CREATED -> gameExternallyCreated(gameId)
        }
    }

    /**
     * To be called by event consumer listening to GameService event
     * @param gameId ID of the new game
     */
    fun gameExternallyCreated(gameId: UUID?) {
        logger.info("Processing external event that the game has been created")
        val fittingGames = gameRepository.findByGameId(gameId)
        var game: Game? = null
        if (fittingGames!!.size == 0) {
            game = Game.Companion.newlyCreatedGame(gameId)
            gameRepository.save(game)
        } else {
            if (fittingGames.size > 1) game = mergeGamesIntoOne(fittingGames)
            game!!.resetToNewlyCreated()
            gameRepository.save(game)
        }
    }

    /**
     * Repair the situation that there are seemingly several games sharing the same gameId. This should not
     * happen. Do this by "merging" the games.
     * @param fittingGames
     */
    fun mergeGamesIntoOne(fittingGames: List<Game?>?): Game? {
        if (fittingGames == null) throw GameException("List of games to be merged must not be null!")
        if (fittingGames.size <= 1) throw GameException("List of games to be merged must contain at least 2 entries!")

        // todo - needs to be properly implemented
        return fittingGames[0]
    }

    /**
     * To be called by event consumer listening to GameService event
     * @param eventId
     */
    fun gameExternallyStarted(eventId: UUID) {
        logger.info("Processing external event that the game with id $eventId has started")
        val foundGames = gameRepository.findAllByGameStatusEquals(GameStatus.GAME_RUNNING)
    }

    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    fun gameEnded(gameId: UUID?) {
        logger.info("Processing 'game ended' event")
        // todo
    }

    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    fun newRound(gameId: UUID?, roundNumber: Int) {
        logger.info("Processing 'new round' event for round no. $roundNumber")
        // todo
    }
}