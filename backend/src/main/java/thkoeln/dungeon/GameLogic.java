package thkoeln.dungeon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.command.CommandDispatcherService;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.application.GameStatusException;
import thkoeln.dungeon.game.application.NoGameAvailableException;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.map.MapApplicationService;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.ResourceType;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.robot.application.RobotApplicationService;
import thkoeln.dungeon.robot.domain.Robot;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

@Component
public class GameLogic {
    private final CommandDispatcherService commandDispatcherService;
    private final RobotApplicationService robotApplicationService;
    private final GameApplicationService gameApplicationService;
    private final PlayerApplicationService playerApplicationService;
    private final MapApplicationService mapApplicationService;

    private final Logger logger = LoggerFactory.getLogger(RobotApplicationService.class);

    @Autowired
    public GameLogic(CommandDispatcherService commandDispatcherService, RobotApplicationService robotApplicationService, GameApplicationService gameApplicationService, PlayerApplicationService playerApplicationService, MapApplicationService mapApplicationService) {
        this.commandDispatcherService = commandDispatcherService;
        this.robotApplicationService = robotApplicationService;
        this.gameApplicationService = gameApplicationService;
        this.playerApplicationService = playerApplicationService;
        this.mapApplicationService = mapApplicationService;
    }

    public void playRound() {

        Game game = gameApplicationService.retrieveCurrentGame();
        Player player = playerApplicationService.retrieveCurrentPlayer();
        Float money = player.getMoney();
        try {
            //this would be cool, but would break everything probably
            /*
            if (money > 100f) {
                int maxRobotBuy = money.intValue() / 100;
                commandDispatcherService.buyRobot(maxRobotBuy);
                return;
            }
            */
            if (game.getRound().getRoundNumber() == 1) {
                commandDispatcherService.buyRobot(1);
                return;
            }
            List<Robot> robots = robotApplicationService.getAllRobots();
            logger.info("Found "+ robots.size()+" robots in application service");
            for (Robot robot : robots) {
                int energy = robot.getEnergy();
                Planet planet = mapApplicationService.getPlanetForRobot(robot);
                if (planet.getResourceType() == ResourceType.COAL && energy >= 1) {
                    commandDispatcherService.mine(robot);
                    break;
                }
                List<Planet> neighbours = mapApplicationService.getNeighboursForPlanet(planet);
                Planet moveTarget = neighbours.get(0);
                //select first unvisited if any
                for (Planet neighbour : neighbours) {
                    if (!planet.getVisited()) {
                        moveTarget = neighbour;
                        break;
                    }
                }
                if (energy >= planet.getMovementDifficulty()) {
                    commandDispatcherService.moveRobotToPlanet(robot, moveTarget);
                    break;
                }
                commandDispatcherService.regenerateEnergy(robot);
            }
        } catch (GameStatusException | NoGameAvailableException e) {
            logger.error(e.getMessage());
            logger.error("Stacktrace: \n" + Arrays.toString(e.getStackTrace()));
        }
    }
}
