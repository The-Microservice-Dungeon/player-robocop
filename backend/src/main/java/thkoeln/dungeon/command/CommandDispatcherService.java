package thkoeln.dungeon.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.application.GameStatusException;
import thkoeln.dungeon.game.application.NoGameAvailableException;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameStatus;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.robot.domain.Robot;

import java.util.UUID;

@Service
public class CommandDispatcherService {
        private final GameApplicationService gameApplicationService;
        private final CommandExecutionService commandExecutionService;
        private final PlayerApplicationService playerApplicationService;
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        @Autowired
        public CommandDispatcherService(GameApplicationService gameApplicationService,
                                  CommandExecutionService commandExecutionService,
                                  PlayerApplicationService playerApplicationService) {
            this.gameApplicationService = gameApplicationService;
            this.commandExecutionService = commandExecutionService;
            this.playerApplicationService = playerApplicationService;
        }

        public void init() throws GameStatusException, NoGameAvailableException {
            Game game = gameApplicationService.retrieveListedGameWithStatus(GameStatus.CREATED);
            Player player = playerApplicationService.retrieveCurrentPlayer();
            playerApplicationService.registerOnePlayerForGame(player,game);
        }
        public void buyRobot() throws GameStatusException, NoGameAvailableException {
            Game game = gameApplicationService.retrieveListedGameWithStatus(GameStatus.STARTED);
            Player player = playerApplicationService.retrieveCurrentPlayer();
            Command robotCommand = CommandBuilder.buildBuyRobotCommand(game, player,1);
            UUID transactionId = commandExecutionService.executeCommand(robotCommand);
            if (transactionId==null || transactionId.toString().isBlank()){
                logger.error("TransactionId is null or blank");
            }
        }

        public void moveRobotToPlanet(Robot robot, Planet targetPlanet) throws GameStatusException, NoGameAvailableException {
            Game game = gameApplicationService.retrieveListedGameWithStatus(GameStatus.STARTED);
            Player player = playerApplicationService.retrieveCurrentPlayer();
            Command moveCommand = CommandBuilder.buildMovementCommand(game, player, robot, targetPlanet);
            UUID transactionId = commandExecutionService.executeCommand(moveCommand);
            if (transactionId==null || transactionId.toString().isBlank()){
                logger.error("TransactionId is null or blank");
            }
        }
}
