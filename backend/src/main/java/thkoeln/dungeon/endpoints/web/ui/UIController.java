package thkoeln.dungeon.endpoints.web.ui;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameDto;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.game.domain.round.Round;
import thkoeln.dungeon.map.MapJSONWrapper;
import thkoeln.dungeon.map.PositionVO;
import thkoeln.dungeon.planet.domain.Planet;
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Game game = gameList.get(0);

        Round round = game.getRound();

        JSONObject roundJson = new JSONObject()
                .put("roundNumber", round.getRoundNumber())
                .put("roundTime", "SomeTime") // TODO: Return Actual Data
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
        List<Player> playerList = playerRepo.findAll();

        if (playerList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Player player = playerList.get(0);

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
        List<Robot> robots = roboRepo.findAll();

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
        GameDto[] gameDtos;
        try {
            gameDtos = this.gameServiceRESTAdapter.fetchCurrentGameState();
        } catch (UnexpectedRESTException | RESTConnectionFailureException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        //  System.out.println(gameDtos[0].getParticipatingPlayers());
        thkoeln.dungeon.map.Map tmpMap = new thkoeln.dungeon.map.Map(gameDtos[0]);

        tmpMap.addFirstBot(new Robot(false));
        tmpMap.addFirstPlanet(new Planet(true, false));

        MapJSONWrapper mapper = new MapJSONWrapper(tmpMap.getContentLength());

        int i = 0;
        for (PositionVO pvo : tmpMap.getPositions()) {
            mapper.addGravity(pvo.getPlanet(), i);
            mapper.addPlanetType(pvo.getPlanet(), i);
            mapper.addRobot(pvo.getRobot(), i);
            i++;
        }


        return new JSONObject(mapper).toMap();
        // GameServiceRESTAdapter restAdapter = new GameServiceRESTAdapter(new RestTemplate());
        // GameDto tmpDTO = restAdapter.fetchCurrentGameState()[0];
        // thkoeln.dungeon.map.Map map = new thkoeln.dungeon.map.Map(tmpDTO);
        // JSONObject mapJson = new JSONObject().put("numberPlayers", tmpMap.getNumberPlayers());
        // return new JSONObject().put("map", mapJson).toMap();
    }

}
