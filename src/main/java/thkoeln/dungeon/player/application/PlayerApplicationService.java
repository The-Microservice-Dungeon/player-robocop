package thkoeln.dungeon.player.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.command.CommandExecutor;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.PlayerAlreadyRegisteredException;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;

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
    private Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private ModelMapper modelMapper = new ModelMapper();

    private CommandExecutor commandExecutor;
    private PlayerRepository playerRepository;
    private GameServiceRESTAdapter gameServiceRESTAdapter;

    @Value("${dungeon.numberOfPlayers}")
    private int numberOfPlayers;

    @Autowired
    public PlayerApplicationService(
            CommandExecutor commandExecutor,
            PlayerRepository playerRepository,
            GameServiceRESTAdapter gameServiceRESTAdapter ) {
        this.commandExecutor = commandExecutor;
        this.playerRepository = playerRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }


    public void createPlayers() {
        List<Player> players = playerRepository.findAll();
        if (players.size() == 0) {
            for (int iPlayer = 0; iPlayer < numberOfPlayers; iPlayer++) {
                Player player = new Player();
                playerRepository.save(player);
                logger.info("Created new player: " + player);
                players.add(player);
            }
        }
    }


    public void registerPlayers() {
        List<Player> players = playerRepository.findAll();
        for (Player player : players) {
            if ( player.getBearerToken() != null ) break;
            try {
                PlayerRegistryDto playerDto = modelMapper.map(player, PlayerRegistryDto.class);
                PlayerRegistryDto registeredPlayerDto = gameServiceRESTAdapter.registerPlayer(playerDto);
                if ( registeredPlayerDto != null ) {
                    if ( registeredPlayerDto.getBearerToken() == null ) logger.error("Received no bearer token for " + player + "!");
                    else player.setBearerToken( registeredPlayerDto.getBearerToken() );
                    playerRepository.save( player );
                    logger.info("Player " + player + " successfully registered.");
                }
                else {
                    logger.warn("Player " + player + " could not be registered due to connection problems - try again later.");
                }
            }
            catch ( PlayerAlreadyRegisteredException e ) {
                // TODO - unclear what to do in this cases
                logger.error( "Name collision while registering player " + player );
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
