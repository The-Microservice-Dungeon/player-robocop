package thkoeln.dungeon.endpoints.web.ui;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.game.domain.round.Round;
import thkoeln.dungeon.map.MapApplicationService;
import thkoeln.dungeon.map.MapJSONWrapper;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.robot.application.RobotApplicationService;
import thkoeln.dungeon.robot.domain.Robot;
import thkoeln.dungeon.robot.domain.RobotRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rest Controller for Game
 */
@RestController
public class UIController {
    private final MapApplicationService mapService;
    private final GameApplicationService gameApplicationService;
    private final PlayerApplicationService playerApplicationService;
    private final RobotApplicationService robotApplicationService;

    /**
     * Constructor
     *
     * @param gameApplicationService
     * @param playerApplicationService
     * @param robotApplicationService
     */
    @Autowired
    UIController(MapApplicationService mapService, GameApplicationService gameApplicationService, PlayerApplicationService playerApplicationService, RobotApplicationService robotApplicationService) {
        this.gameApplicationService = gameApplicationService;
        this.mapService = mapService;
        this.playerApplicationService = playerApplicationService;
        this.robotApplicationService = robotApplicationService;
    }

    @GetMapping("/game")
    Map<String, Object> currentGameInfo(HttpServletResponse response) {
        Game game = this.gameApplicationService.retrieveCurrentGame();
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Round round = game.getRound();

        JSONObject roundJson = new JSONObject()
                .put("roundNumber", round.getRoundNumber())
                .put("roundStatus", round.getRoundStatus());

        JSONObject gameJson = new JSONObject()
                .put("status", game.getGameStatus())
                .put("playerCount", game.getNumberOfPlayers())
                .put("maxRounds", game.getMaxRounds())
                .put("currentRound", roundJson);

        return new JSONObject()
                .put("game", gameJson)
                .toMap();
    }

    @GetMapping("/player")
    Map<String, Object> playerInfo() {
        Player player = this.playerApplicationService.getCurrentPlayer();

        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        JSONObject playerJson = new JSONObject()
                .put("name", player.getName())
                .put("email", player.getEmail())
                .put("money", player.getMoney())
                .put("robots", player.getRobotCount());

        return new JSONObject()
                .put("player", playerJson)
                .toMap();
    }

    @GetMapping("/robots")
    Map<String, Object> allRobotInfo() {
        List<Robot> robots = this.robotApplicationService.getAllRobots();

        if (robots.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        ArrayList<JSONObject> robotObjects = new ArrayList<>();

        // TODO: Return Stats and Inventory
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

    @GetMapping("/map")
    Map<String, Object> getMap() {
        MapJSONWrapper layerMap;
        try {
            layerMap = mapService.getLayerMap();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There is no current game so no map.");
        }


        return new JSONObject(layerMap)
                .toMap();
    }

}
