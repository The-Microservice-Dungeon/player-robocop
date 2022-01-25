package thkoeln.dungeon.endpoints.web.ui;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameDto;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.game.domain.round.Round;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;
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
    private final GameRepository gameRepo;
    private final PlayerRepository playerRepo;
    private final RobotRepository roboRepo;
    private final GameServiceRESTAdapter gameServiceRESTAdapter;

    /**
     * Constructor
     *
     * @param gameRepo
     */
    @Autowired
    UIController(GameServiceRESTAdapter gameServiceRESTAdapter, GameRepository gameRepo, PlayerRepository playerRepo, RobotRepository roboRepo) {
        this.gameRepo = gameRepo;
        this.playerRepo = playerRepo;
        this.roboRepo = roboRepo;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }

    @GetMapping("/game")
    Map<String, Object> currentGameInfo(HttpServletResponse response) {
        List<Game> gameList = gameRepo.findAll();
        if (gameList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new JSONObject().toMap();
        }

        Game game = gameList.get(0);

        Round round = game.getRound();

        JSONObject roundJson = new JSONObject()
                // .put("roundNumber", round.getRoundNumber())
                .put("roundNumber", 1)
                .put("roundTime", "SomeTime") // TODO: Return Actual Data
                //.put("roundStatus", round.getRoundStatus());
                .put("roundStatus", "Created");

        JSONObject gameJson = new JSONObject()
                //.put("status", game.getGameStatus())
                .put("status", "Created")
                .put("playerCount", 1) // TODO: Return Actual Data
                .put("maxRounds", 420) // TODO: Return Actual Data
                .put("currentRound", roundJson);

        return new JSONObject()
                .put("game", gameJson)
                .toMap();
    }

    @GetMapping("/player")
    Map<String, Object> playerInfo() {
        Player player = playerRepo.findAll().get(0);

        JSONObject playerJson = new JSONObject()
                .put("name", player.getName())
                .put("email", player.getEmail())
                .put("money", 0) // TODO: Return Actual Data
                .put("robots", 0); // TODO: Return Actual Data

        return new JSONObject()
                .put("player", playerJson)
                .toMap();
    }

    @GetMapping("/robots")
    Map<String, Object> allRobotInfo() {

        List<Robot> robots = roboRepo.findAll();

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

    @GetMapping("/map")
    Map<String, Object> getMap() {
        GameDto[] gameDtos;
        try {
            gameDtos = this.gameServiceRESTAdapter.fetchCurrentGameState();
        } catch (UnexpectedRESTException | RESTConnectionFailureException e) {
            return new JSONObject().toMap();
        }


        System.out.println(gameDtos[0].getParticipatingPlayers());
        thkoeln.dungeon.map.Map tmpMap = new thkoeln.dungeon.map.Map(gameDtos[0]);

        tmpMap.addFirstBot(new Robot(false));
        //   tmpMap.addFirstPlanet(new Planet());

        return new JSONObject(tmpMap).toMap();
        // GameServiceRESTAdapter restAdapter = new GameServiceRESTAdapter(new RestTemplate());
        // GameDto tmpDTO = restAdapter.fetchCurrentGameState()[0];
        // thkoeln.dungeon.map.Map map = new thkoeln.dungeon.map.Map(tmpDTO);
        // JSONObject mapJson = new JSONObject().put("numberPlayers", tmpMap.getNumberPlayers());
        // return new JSONObject().put("map", mapJson).toMap();
    }

}
