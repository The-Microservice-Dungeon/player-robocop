package thkoeln.dungeon.player.application

import org.modelmapper.ModelMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import thkoeln.dungeon.command.CommandExecutor
import thkoeln.dungeon.game.domain.Game
import thkoeln.dungeon.player.domain.GameParticipationRepository
import thkoeln.dungeon.player.domain.Player
import thkoeln.dungeon.player.domain.PlayerMode
import thkoeln.dungeon.player.domain.PlayerRepository
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter
import thkoeln.dungeon.restadapter.PlayerRegistryDto
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException
import thkoeln.dungeon.restadapter.exceptions.RESTRequestDeniedException
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException
import java.util.*

/**
 * This game class encapsulates the game tactics for a simple autonomous controlling of a robot
 * swarm. It has the following structure:
 * - the "round started" event triggers the main round() method
 * - if there is enough money, new robots are bought (or, depending on configuration, existing robots are upgraded)
 * - for each robot, the proper command is chosen and issued (based on the configured tactics)
 * - each time an answer is received (with transaction id), the robots and the map are updated.
 */
@Service
class PlayerApplicationService @Autowired constructor(
    private val commandExecutor: CommandExecutor,
    private val playerRepository: PlayerRepository,
    private val gameParticipationRepository: GameParticipationRepository,
    private val gameServiceRESTAdapter: GameServiceRESTAdapter
) {
    private val logger = LoggerFactory.getLogger(PlayerApplicationService::class.java)
    private val modelMapper = ModelMapper()

    @Value("\${dungeon.singlePlayer.playerName}")
    private val singlePlayerName: String? = null

    @Value("\${dungeon.singlePlayer.playerEmail}")
    private val singlePlayerEmail: String? = null

    @Value("\${dungeon.mode}")
    private val playerMode: PlayerMode? = null

    @Value("\${dungeon.multiPlayer.number}")
    private val numberOfMultiPlayers = 0
    fun currentMode(): PlayerMode? {
        return playerMode
    }

    fun numberOfPlayers(): Int {
        return if (currentMode()!!.isSingle) 1 else numberOfMultiPlayers
    }

    /**
     * Create player(s), if not there already
     */
    fun createPlayers() {
        val players = playerRepository.findAll()
        if (players.size == 0) {
            for (iPlayer in 0 until numberOfPlayers()) {
                val player = Player()
                if (currentMode()!!.isSingle && "" != singlePlayerName && "" != singlePlayerEmail) {
                    player.name = singlePlayerName
                    player.email = singlePlayerEmail
                } else {
                    player.assignRandomName()
                }
                playerRepository.save(player)
                logger.info("Created new player: $player")
                players.add(player)
            }
        }
    }

    /**
     * Obtain the bearer token for all players defined in this service
     */
    fun obtainBearerTokensForPlayers() {
        val players = playerRepository.findAll()
        for (player in players) obtainBearerTokenForOnePlayer(player)
    }

    /**
     * Obtain the bearer token for one specific player
     * @param player
     * @return true if successful
     */
    fun obtainBearerTokenForOnePlayer(player: Player?) {
        if (player?.bearerToken != null) return
        try {
            val playerDto = modelMapper.map(player, PlayerRegistryDto::class.java)
            val registeredPlayerDto = gameServiceRESTAdapter.getBearerTokenForPlayer(playerDto)
            if (registeredPlayerDto != null) {
                if (registeredPlayerDto.bearerToken == null) logger.error("Received no bearer token for $player!")
                else player?.bearerToken = registeredPlayerDto.bearerToken
                playerRepository.save(player)
                logger.info("Bearer token received for $player")
            } else {
                logger.error("PlayerRegistryDto returned by REST service is null for player $player")
            }
        } catch (e: RESTRequestDeniedException) {
            // TODO - unclear what to do in this cases
            logger.error("Name collision while getting bearer token for player $player")
        } catch (e: RESTConnectionFailureException) {
            logger.error("No connection or no valid response from GameService - no bearer token for player $player")
        } catch (e: UnexpectedRESTException) {
            logger.error("No connection or no valid response from GameService - no bearer token for player $player")
        }
    }

    /**
     * Once we received the event that a game has been created, this method can be called to register the players
     * for the game.
     * @param game
     */
    fun registerPlayersForGame(game: Game) {
        val players = playerRepository.findAll()
        for (player in players) registerOnePlayerForGame(player, game)
    }

    /**
     * Register one specific player for a game
     * @param player
     * @param game
     */
    fun registerOnePlayerForGame(player: Player?, game: Game) {
        try {
            if (player?.bearerToken == null) {
                obtainBearerTokenForOnePlayer(player)
            }
            if (player?.bearerToken == null) {
                logger.error("No bearer token for $player also after another attempt - cannot register for game!")
                return
            }
            val success = gameServiceRESTAdapter.registerPlayerForGame(game.id, player.bearerToken!!)
            if (success) {
                player.participateInGame(game)
                playerRepository.save(player)
                logger.info("Player $player successfully registered for game $game")
            }
        } catch (e: RESTConnectionFailureException) {
            // shouldn't happen - cannot do more than logging and retrying later
            logger.error(
                "Could not be get bearer token for player " + player +
                        " due to connection problems - try again later."
            )
        } catch (e: RESTRequestDeniedException) {
            logger.error(
                "Could not be get bearer token for player " + player +
                        " due to connection problems - try again later."
            )
        }
    }

    fun playRound(roundNumber: Int) {
        logger.info("Starting round $roundNumber")
        val players: Iterable<Player?> = playerRepository.findAll()
        for (player in players) {
            player!!.playRound()
        }
        val transactionId = commandExecutor.executeCommand(null)
        logger.info("transactionId $transactionId")
        logger.info("Ending round $roundNumber")
    }

    fun receiveCommandAnswer(transactionId: UUID?, payload: String?) {}
    fun learnAboutMoveByEnemyRobot() {}
}