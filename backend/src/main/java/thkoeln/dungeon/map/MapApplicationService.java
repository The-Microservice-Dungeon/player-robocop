package thkoeln.dungeon.map;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.robot.NeighboursEvent;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.planet.application.PlanetApplicationService;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.application.RobotApplicationService;
import thkoeln.dungeon.robot.domain.Robot;

import java.util.UUID;

@Setter
@Service
public class MapApplicationService {
    private final PlanetApplicationService planetApplicationService;
    private final RobotApplicationService robotApplicationService;

    private final Logger logger = LoggerFactory.getLogger(MapApplicationService.class);


    private Map currentMap;

    @Autowired
    public MapApplicationService (PlanetApplicationService planetApplicationService, RobotApplicationService robotApplicationService) {
        this.planetApplicationService = planetApplicationService;
        this.robotApplicationService = robotApplicationService;
    }

    public void placeDemoStuff () {
        currentMap.addFirstBot(new Robot(true));
        currentMap.addFirstPlanet(new Planet(UUID.randomUUID(),true, false));
    }

    public void createMapFromGame (Game game) {
        this.currentMap = new Map(game);
    }

    private void placeFirstRobotAndPlanet (Robot robot, Planet planet) {
        if (this.currentMap == null) {
            logger.error("Cant Place Robots / Planets. No Map exists.");
            // TODO: dont let this case happen. Maybe build map on sync or on game data retrieval
            return;
        }
        this.currentMap.addFirstBot(robot);
        this.currentMap.addFirstPlanet(planet);
    }

    // TODO: Call on robot spawned event
    public void handleNewRobotSpawn (Robot robot, Planet planet) {
        if (this.planetApplicationService.isFirstPlanet()) this.placeFirstRobotAndPlanet(robot, planet);
    }

    public void handleNewPlanetNeighbours (Planet planet, NeighboursEvent[] neighbours) {
        this.planetApplicationService.generateNeighboursForPlanet(planet, neighbours);
        planet = this.planetApplicationService.refreshPlanet(planet);

        if (planet.hasNeighbours()) {
            Planet neighbour = planet.allNeighbours().get(0);
            this.currentMap.addNeighboursOfPlanetToMap(neighbour);
        }
    }

    // call on Robot move
    public void updateRobotPosition (Robot robot, Planet newPlanet) {
        PositionVO oldRobotPosition = this.currentMap.findPosition(robot);
        this.currentMap.removeRobotOnPosition(oldRobotPosition);
        PositionVO newPlanetPosition = this.currentMap.findPosition(newPlanet);
        // robotApplicationService setRobotPosition(robot, newPlanetPosition)
        this.currentMap.setRobotOnPosition(newPlanetPosition, robot);
    }

    public MapJSONWrapper getLayerMap () {
        MapJSONWrapper wrapper = new MapJSONWrapper(currentMap.getContentLength());

        int i = 0;
        for (PositionVO pvo : currentMap.getPositions()) {
            Planet planet = this.planetApplicationService.getById(pvo.getReferencingPlanetId());
            Robot robot = this.robotApplicationService.getById(pvo.getReferencingPlanetId());
            wrapper.addGravity(planet, i);
            wrapper.addPlanetType(planet, i);
            wrapper.addRobot(robot, i);
            i++;
        }
        return wrapper;
    }
}
