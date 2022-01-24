package thkoeln.dungeon.endpoints.web.ui;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.map.Map;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;
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


    @GetMapping("/map")
    Map getMap() throws RESTConnectionFailureException, UnexpectedRESTException {
        GameServiceRESTAdapter gsAdapter = new GameServiceRESTAdapter(new RestTemplate());
        GameDto[] tmp = gsAdapter.fetchCurrentGameState();
        return new Map(tmp[0]);
    }


}
