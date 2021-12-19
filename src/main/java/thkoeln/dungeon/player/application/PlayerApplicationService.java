package thkoeln.dungeon.player.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.command.CommandExecutor;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.PlayerAlreadyRegisteredException;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;
import thkoeln.dungeon.restadapter.UnexpectedRestAdapterException;

import java.util.*;

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
    private Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private ModelMapper modelMapper = new ModelMapper();

    private CommandExecutor commandExecutor;
    private PlayerRepository playerRepository;
    private GameApplicationService gameApplicationService;
    private GameServiceRESTAdapter gameServiceRESTAdapter;

    @Value("${dungeon.numberOfPlayers}")
    private int numberOfPlayers;

    @Autowired
    public PlayerApplicationService(
            CommandExecutor commandExecutor,
            PlayerRepository playerRepository,
            GameApplicationService gameApplicationService,
            GameServiceRESTAdapter gameServiceRESTAdapter ) {
        this.commandExecutor = commandExecutor;
        this.playerRepository = playerRepository;
        this.gameApplicationService = gameApplicationService;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }


    public void createAndRegisterPlayers() {
        List<Player> players = playerRepository.findAll();
        if (players.size() == 0) {
            for (int iPlayer = 0; iPlayer < numberOfPlayers; iPlayer++) {
                Player player = new Player();
                playerRepository.save(player);
                logger.info("Created new player: " + player);
                players.add(player);
            }
        }
        // If a player has no bearer token, then a registration call to the GameService is required.
        for (Player player : players) {
            if (player.getBearerToken() == null) registerPlayer( player );
        }
    }


    public void registerPlayer( Player player ) {
        if (player.getBearerToken() == null) {
            try {
                PlayerRegistryDto playerDto = modelMapper.map(player, PlayerRegistryDto.class);
                PlayerRegistryDto registeredPlayerDto = gameServiceRESTAdapter.registerPlayer(playerDto);
                Player registeredPlayer = modelMapper.map(registeredPlayerDto, Player.class);
                playerRepository.save(registeredPlayer);
                logger.info( "Player " + player + " successfully registered." );
            }
            catch ( PlayerAlreadyRegisteredException e ) {
                // TODO - unclear what to do in this cases
                logger.error( "Name collision while registering player!" );
            }
        }
    }


    public void playRound( Integer roundNumber ) {
        logger.info( "Starting round " + roundNumber );
        Iterable<Player> players = playerRepository.findAll();
        for ( Player player : players ) {
            player.playRound();
        }
        UUID transactionId = commandExecutor.executeCommand( null );
        logger.info( "transactionId " + transactionId );
        logger.info( "Ending round " + roundNumber );
    }




    public void receiveCommandAnswer(UUID transactionId, String payload) {

    }


    public void learnAboutMoveByEnemyRobot() {

    }
}
