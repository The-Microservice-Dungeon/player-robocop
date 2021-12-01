package thkoeln.dungeon.player.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.commands.CommandExecutor;
import thkoeln.dungeon.gameconnector.PlayerCallback;

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
public class PlayerService implements PlayerCallback {
    Logger logger = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private CommandExecutor commandExecutor;

    @Override
    public void playRound( Integer roundNumber ) {
        logger.info( "Starting round " + roundNumber );
        UUID transactionId = commandExecutor.executeCommand( null );
        logger.info( "transactionId " + transactionId );
    }

    @Override
    public void receiveCommandAnswer(UUID transactionId, String payload) {

    }

    @Override
    public void learnAboutMoveByEnemyRobot() {

    }
}
