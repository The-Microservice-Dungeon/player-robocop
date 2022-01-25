package thkoeln.dungeon.endpoints.web.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameRepository;


import thkoeln.dungeon.game.domain.round.Round;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;
import thkoeln.dungeon.robot.domain.Robot;
import thkoeln.dungeon.robot.domain.RobotRepository;

import java.io.File;
import java.io.IOException;
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
    Map<String, Object> currentGameInfo(){
        // Game game = gameRepo.findAll().get(0);
        // Round round = game.getRound();

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

    @GetMapping("/map")
    String getMap() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        GameDto[] gameDtos = restTemplate.getForObject("http://localhost:8080/games", GameDto[].class);

        if (gameDtos != null) {
            return "{}";
        }

        System.out.println(gameDtos[0].getParticipatingPlayers());
        thkoeln.dungeon.map.Map tmpMap = new thkoeln.dungeon.map.Map(gameDtos[0]);

        tmpMap.addFirstBot(new Robot(false));
     //   tmpMap.addFirstPlanet(new Planet());
        ObjectMapper objectMapper = new ObjectMapper();
       return objectMapper.writeValueAsString(tmpMap);
       // GameServiceRESTAdapter restAdapter = new GameServiceRESTAdapter(new RestTemplate());
       // GameDto tmpDTO = restAdapter.fetchCurrentGameState()[0];
       // thkoeln.dungeon.map.Map map = new thkoeln.dungeon.map.Map(tmpDTO);
       // JSONObject mapJson = new JSONObject().put("numberPlayers", tmpMap.getNumberPlayers());
       // return new JSONObject().put("map", mapJson).toMap();
    }

}
