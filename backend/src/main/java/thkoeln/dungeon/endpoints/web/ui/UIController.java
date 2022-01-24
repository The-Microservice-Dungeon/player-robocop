package thkoeln.dungeon.endpoints.web.ui;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.robot.domain.Robot;
import thkoeln.dungeon.robot.domain.RobotRepository;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    // TODO: Return Actual Data
    Map<String, Object> currentGameInfo(){
        JSONObject roundJson = new JSONObject()
                .put("roundNumber", 1)
                .put("roundTime", "SomeTime")
                .put("roundStatus", "CREATED");

        JSONObject gameJson = new JSONObject()
                .put("status", "CREATED")
                .put("playerCount", 1)
                .put("maxRounds", 420)
                .put("currentRound", roundJson);

        return new JSONObject()
                .put("game", gameJson)
                .toMap();
    }

    @GetMapping("/player")
    Map<String, Object> playerInfo(){
        Player player = playerRepo.findAll().get(0);

        return new JSONObject()
                .put("name", player.getName())
                .put("email", player.getEmail())
                .put("money", 0) // TODO: Return Actual Data
                .put("robots", 0) // TODO: Return Actual Data
                .toMap();
    }

    @GetMapping("/robots")
    Map<String, Object> allRobotInfo(){

        List <Robot> robots = roboRepo.findAll();

        ArrayList<JSONObject> robotObjects = new ArrayList<>();

        // TODO: Return Actual and Full Data
        for (Robot robot : robots) {
            JSONObject robotJson = new JSONObject()
                    .put("health", robot.getHealth())
                    .put("energy", robot.getEnergy());
            robotObjects.add(robotJson);
        }

        return new JSONObject()
                .put("robots", robotObjects)
                .toMap();
    }

}
