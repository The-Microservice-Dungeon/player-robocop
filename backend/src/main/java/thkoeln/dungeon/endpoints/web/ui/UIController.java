package thkoeln.dungeon.endpoints.web.ui;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.robot.domain.Robot;
import thkoeln.dungeon.robot.domain.RobotRepository;

import java.util.List;

/**
 * Rest Controller for Game
 */
@RestController
public class UIController {
    private final GameRepository gameRepo;
    private final PlayerRepository playerRepo;
    private final RobotRepository roboRepo;


    /**
     * Constructor
     * @param gameRepo
     */
    UIController(GameRepository gameRepo, PlayerRepository playerRepo, RobotRepository roboRepo) {
        this.gameRepo = gameRepo;
        this.playerRepo = playerRepo;
        this.roboRepo = roboRepo;
    }

    @GetMapping("/game")
    List<Game> allGames(){
        return gameRepo.findAll();
    }

    @GetMapping("/player")
    List<Player> allPlayer(){
        return playerRepo.findAll();
    }

    @GetMapping("/robots")
    List<Robot> allRobots(){
        return (List<Robot>) roboRepo.findAll();
    }


}
