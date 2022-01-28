package thkoeln.dungeon.player.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.command.CommandExecutor;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.player.domain.GameParticipationRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerDomainException;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.restadapter.exceptions.RESTAdapterException;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.RESTRequestDeniedException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;
import thkoeln.dungeon.robot.domain.Robot;

import java.util.List;
import java.util.Optional;
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

    private final SimpMessagingTemplate websocket;

    private final CommandExecutor commandExecutor;
    private final PlayerRepository playerRepository;
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
            SimpMessagingTemplate websocket, GameServiceRESTAdapter gameServiceRESTAdapter) {
        this.commandExecutor = commandExecutor;
        this.playerRepository = playerRepository;
        this.websocket = websocket;
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

    public void reloadBearerToken () {
        Player player = getCurrentPlayer();
        UUID bearerTokenFromExistingPlayer = this.retrieveBearerTokenForExistingPlayer(player);
        if (bearerTokenFromExistingPlayer != null) {
            player.setBearerToken(bearerTokenFromExistingPlayer);
            playerRepository.save(player);
            logger.info("Received Bearer token for existing player: " + player);
        } else {
            logger.error("Could not get Bearer Token for existing player: " + player);
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

    public Player retrieveCurrentPlayer() {
        return playerRepository.findByNameAndEmail(playerName, playerEmail);
    }


    /**
     * Register one specific player for a game
     *
     * @param player
     * @param game
     */
    public void registerOnePlayerForGame(Player player, Game game) {
        if (player.getBearerToken() == null) {
            logger.error("Player" + player + " has no BearerToken! Trying to obtain it.");
            obtainBearerTokenForOnePlayer(player);
        }
        try {
            UUID transactionId = gameServiceRESTAdapter.registerPlayerForGame(game.getGameId(), player.getBearerToken());
            if (transactionId != null) {
                player.registerFor(game, transactionId);
                playerRepository.save(player);
                logger.info("Player " + player + " successfully registered for game " + game +
                        " with transactionId " + transactionId);
            }
        } catch (RESTAdapterException e) {
            // shouldn't happen - cannot do more than logging and retrying later
            // todo - err msg wrong
            logger.error("Could not register " + player + " for " + game +
                    "\nOriginal Exception:\n" + e.getMessage() + "\n" + e.getStackTrace());
        } catch (PlayerDomainException e) {
            logger.error("Whoops.");
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

    public void handlePlayerRegistrationEvent (UUID registrationTransactionId, UUID playerId, String name) {
        if (!playerName.equals(name)) {
            String errorMessage = "This event is not for us! It is for " + name;

            logger.error(errorMessage);
            throw new PlayerRegistryException(errorMessage);
        }

        if (registrationTransactionId == null) {
            String errorMessage = "registrationTransactionId cannot be null!";

            logger.error(errorMessage);
            throw new PlayerRegistryException(errorMessage);
        }

        if (playerId == null) {
            String errorMessage = "PlayerId cannot be null!";
            logger.error(errorMessage);
            throw new PlayerRegistryException(errorMessage);
        }

        this.assignPlayerId(registrationTransactionId, playerId);
    }

    public void assignPlayerId(UUID registrationTransactionId, UUID playerId) {
        logger.info("Assign playerId from game registration");

        List<Player> foundPlayers = playerRepository.findByRegistrationTransactionId(registrationTransactionId);
        if (foundPlayers.size() != 1) {
            String errorMessage = "Found not exactly 1 game for player registration with " + registrationTransactionId + ", but " + foundPlayers.size();
            logger.error(errorMessage);
            throw new PlayerRegistryException(errorMessage);
        }

        Player player = foundPlayers.get(0);
        player.setPlayerId(playerId);

        logger.info("Updated Player " + player);

        playerRepository.save(player);
    }

    public void handleBankCreatedEvent(UUID playerId, Integer money){
        Optional<Player> found = playerRepository.findByPlayerId(playerId);

        if (found.isPresent()){
            Player player = found.get();
            this.setMoneyOfPlayer(player, money);
        } else {
            String errorMessage = "Player with playerId "+ playerId +" not found.";
            logger.error(errorMessage);
            throw new PlayerRegistryException(errorMessage);
        }
    }

    public void setMoneyOfPlayer(Player player, Integer money){
        logger.info("Set player money to "+money);
        player.setMoney(money.floatValue());
        playerRepository.save(player);
        this.websocket.convertAndSend("player_events", "player_money_set");
    }

    public void changeMoneyOfPlayer(UUID playerId, Integer moneyChangedByAmount){
        Optional<Player> found = playerRepository.findByPlayerId(playerId);
        if (found.isPresent()){
            logger.info("Adjusting player money by "+moneyChangedByAmount);
            Player player = found.get();
            player.addMoney(moneyChangedByAmount);
            playerRepository.save(player);
            this.websocket.convertAndSend("player_events", "player_money_changed");
        }
        else {
            throw new PlayerRegistryException("Player with playerId "+ playerId+" not found.");
        }
    }

    public Player getCurrentPlayer () {
        return this.playerRepository.findAll().get(0);
    }

    // TODO: call on Robot Spawned Event
    public void addRobotToPlayer (Robot robot) {
        Player player = getCurrentPlayer();
        player.addRobot(robot);
        this.playerRepository.save(player);
        this.websocket.convertAndSend("player_events", "robot_added");
    }

    // TODO: call on Robot Destroyed Event
    public void removeRobotFromPlayer (Robot robot) {
        Player player = getCurrentPlayer();
        player.removeRobot(robot);
        this.playerRepository.save(player);
        this.websocket.convertAndSend("player_events", "robot_removed");
    }

    public void receiveCommandAnswer(UUID transactionId, String payload) {

    }


    public void learnAboutMoveByEnemyRobot() {

    }
}
