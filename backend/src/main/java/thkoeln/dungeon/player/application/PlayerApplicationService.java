package thkoeln.dungeon.player.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.command.CommandExecutor;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.player.domain.GameParticipationRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.RESTRequestDeniedException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

import java.util.List;
import java.util.UUID;

/**
 * This game class encapsulates the game tactics for a simple autonomous controlling of a robot
 * swarm. It has the following structure:
 * - the "round started" event triggers the main round() method
 * - if there is enough money, new robots are bought (or, depending on configuration, existing robots are upgraded)
 * - for each robot, the proper command is chosen and issued (based on the configured tactics)
 * - each time an answer is received (with transaction id), the robots and the map are updated.
 */
@Service
public class PlayerApplicationService {
    private final Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private final ModelMapper modelMapper = new ModelMapper();

    private final CommandExecutor commandExecutor;
    private final PlayerRepository playerRepository;
    private final GameParticipationRepository gameParticipationRepository;
    private final GameServiceRESTAdapter gameServiceRESTAdapter;

    @Value("${dungeon.player.playerName}")
    private String playerName;

    @Value("${dungeon.player.playerEmail}")
    private String playerEmail;


    @Autowired
    public PlayerApplicationService(
            CommandExecutor commandExecutor,
            PlayerRepository playerRepository,
            GameParticipationRepository gameParticipationRepository,
            GameServiceRESTAdapter gameServiceRESTAdapter) {
        this.commandExecutor = commandExecutor;
        this.playerRepository = playerRepository;
        this.gameParticipationRepository = gameParticipationRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }


    /**
     * Create player(s), if not there already
     */
    public void createPlayers() {
        List<Player> players = playerRepository.findAll();
        if (players.size() == 0) {
            Player player = new Player();
            if ((!"".equals(playerName)) && (!"".equals(playerEmail))) {
                player.setName(playerName);
                player.setEmail(playerEmail);
            } else {
                player.assignRandomName();
            }
            playerRepository.save(player);
            logger.info("Created new player: " + player);
            players.add(player);
        }
    }


    /**
     * Obtain the bearer token for all players defined in this service
     */
    public void obtainBearerTokensForPlayers() {
        List<Player> players = playerRepository.findAll();
        for (Player player : players) obtainBearerTokenForOnePlayer(player);
    }


    /**
     * Obtain the bearer token for one specific player
     *
     * @param player
     * @return true if successful
     */
    protected void obtainBearerTokenForOnePlayer(Player player) {
        if (player.getBearerToken() != null) return;
        try {
            PlayerRegistryDto playerDto = modelMapper.map(player, PlayerRegistryDto.class);
            PlayerRegistryDto registeredPlayerDto = gameServiceRESTAdapter.registerNewPlayer(playerDto);
            if (registeredPlayerDto != null) {
                if (registeredPlayerDto.getBearerToken() == null)
                    logger.error("Received no bearer token for " + player + "!");
                else player.setBearerToken(registeredPlayerDto.getBearerToken());
                playerRepository.save(player);
                logger.info("Bearer token received for " + player);
            } else {
                logger.error("PlayerRegistryDto returned by REST service is null for player " + player);
            }
        } catch (RESTRequestDeniedException e) {
            logger.error("Name collision while getting bearer token for player " + player + ". Re-fetching player info");
            UUID bearerTokenFromExistingPlayer = this.retrieveBearerTokenForExistingPlayer(player);
            if (bearerTokenFromExistingPlayer != null) {
                player.setBearerToken(bearerTokenFromExistingPlayer);
                playerRepository.save(player);
                logger.info("Received Bearer token for existing player: " + player);
            }
        } catch (RESTConnectionFailureException | UnexpectedRESTException e) {
            logger.error("No connection or no valid response from GameService - no bearer token for player " + player);
        }
    }

    /**
     * Obtain bearer token from an existing player
     *
     * @param player
     * @return
     */
    protected UUID retrieveBearerTokenForExistingPlayer(Player player) {
        try {
            PlayerRegistryDto playerDto = modelMapper.map(player, PlayerRegistryDto.class);
            PlayerRegistryDto playerInfoDto = gameServiceRESTAdapter.getPlayerDetails(playerDto);
            if (playerInfoDto != null) {
                if (playerInfoDto.getBearerToken() == null)
                    throw new UnexpectedRESTException("Didn't receive Bearer Token for existing Player");
                else return playerInfoDto.getBearerToken();
            }
        } catch (RESTConnectionFailureException | UnexpectedRESTException e) {
            logger.error("No connection or no valid response from GameService - again, no bearer token for player " + player);
        }
        return null;
    }


    /**
     * Once we received the event that a game has been created, this method can be called to register the players
     * for the game.
     *
     * @param game
     */
    public void registerPlayersForGame(Game game) {
        List<Player> players = playerRepository.findAll();
        for (Player player : players) registerOnePlayerForGame(player, game);
    }


    /**
     * Register one specific player for a game
     *
     * @param player
     * @param game
     */
    public void registerOnePlayerForGame(Player player, Game game) {
        try {
            if (player.getBearerToken() == null) {
                obtainBearerTokenForOnePlayer(player);
            }
            if (player.getBearerToken() == null) {
                logger.error("No bearer token for " + player + " also after another attempt - cannot register for game!");
                return;
            }
            boolean success = gameServiceRESTAdapter.registerPlayerForGame(game.getGameId(), player.getBearerToken());
            if (success) {
                player.participateInGame(game);
                playerRepository.save(player);
                logger.info("Player " + player + " successfully registered for game " + game);
            }
        } catch (RESTConnectionFailureException | RESTRequestDeniedException e) {
            // shouldn't happen - cannot do more than logging and retrying later
            logger.error("Could not be get bearer token for player " + player +
                    " due to connection problems - try again later.");
        }
    }


    public void playRound(Integer roundNumber) {
        logger.info("Starting round " + roundNumber);
        Iterable<Player> players = playerRepository.findAll();
        for (Player player : players) {
            player.playRound();
        }
        UUID transactionId = commandExecutor.executeCommand(null);
        logger.info("transactionId " + transactionId);
        logger.info("Ending round " + roundNumber);
    }


    public void receiveCommandAnswer(UUID transactionId, String payload) {

    }


    public void learnAboutMoveByEnemyRobot() {

    }
}
