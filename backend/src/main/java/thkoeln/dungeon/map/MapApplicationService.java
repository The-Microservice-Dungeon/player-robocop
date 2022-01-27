package thkoeln.dungeon.map;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.robot.NeighbourData;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.planet.application.PlanetApplicationService;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.application.RobotApplicationService;
import thkoeln.dungeon.robot.domain.Robot;
import java.util.List;

import java.util.UUID;

@Setter
@Service
public class MapApplicationService {
    private final PlanetApplicationService planetApplicationService;
    private final RobotApplicationService robotApplicationService;
    private final MapRepository mapRepository;

    private final Logger logger = LoggerFactory.getLogger(MapApplicationService.class);


    private Map currentMap;

    @Autowired
    public MapApplicationService(PlanetApplicationService planetApplicationService, RobotApplicationService robotApplicationService, MapRepository mapRepository) {
        this.planetApplicationService = planetApplicationService;
        this.robotApplicationService = robotApplicationService;
        this.mapRepository = mapRepository;
    }

    public void createMapFromGame (Game game) {
        this.currentMap = new Map(game);
        mapRepository.save(currentMap);
    }

    private void placeFirstRobotAndPlanet (Robot robot, Planet planet) {
        if (this.currentMap == null) {
            logger.error("Cant Place Robots / Planets. No Map exists.");
            // TODO: dont let this case happen. Maybe build map on sync or on game data retrieval
            return;
        }
        this.currentMap.addFirstBot(robot);
        this.currentMap.addFirstPlanet(planet);
        mapRepository.save(currentMap);
        PositionVO robotPosition = this.currentMap.findPosition(robot);
        PositionVO planetPosition = this.currentMap.findPosition(planet);
        this.robotApplicationService.setRobotPosition(robot, robotPosition);
        this.planetApplicationService.setPlanetPosition(planet, planetPosition);
    }

    // TODO: Call on robot spawned event
    public void handleNewRobotSpawn (Robot robot, UUID planetId) {
        //we need a planet with position 0,0
        if (currentMap==null){
            logger.error("Map not initialized yet");
            return;
        }
        PositionVO center = currentMap.getPositions().get(currentMap.getCenterIndex());
        Planet firstPlanet = planetApplicationService.createStartPlanet(planetId, center);
        if (this.planetApplicationService.isFirstPlanet()){
            this.placeFirstRobotAndPlanet(robot, firstPlanet);
        }
    }

    public void handleNewPlanetNeighbours (Planet planet, List<NeighbourData> neighbours) {
        this.planetApplicationService.generateNeighboursForPlanet(planet, neighbours);
        planet = this.planetApplicationService.refreshPlanet(planet);

        if (planet.hasNeighbours()) {
            Planet neighbour = planet.allNeighbours().get(0);
            this.currentMap.addNeighboursOfPlanetToMap(neighbour);
            mapRepository.save(currentMap);
        }
    }

    // call on Robot move
    public void updateRobotPosition (Robot robot, Planet newPlanet) {
        PositionVO oldRobotPosition = this.currentMap.findPosition(robot);
        this.currentMap.removeRobotOnPosition(oldRobotPosition);
        PositionVO newPlanetPosition = this.currentMap.findPosition(newPlanet);
        robotApplicationService.setRobotPosition(robot, newPlanetPosition);
        this.currentMap.setRobotOnPosition(newPlanetPosition, robot);
        mapRepository.save(currentMap);
    }

    public MapJSONWrapper getLayerMap () {
        MapJSONWrapper wrapper = new MapJSONWrapper(currentMap.getContentLength());

        int i = 0;
        for (PositionVO pvo : currentMap.getPositions()) {
            UUID planetId = pvo.getReferencingPlanetId();
            if (planetId != null) {
                Planet planet = this.planetApplicationService.getById(pvo.getReferencingPlanetId());
                wrapper.addGravity(planet, i);
                wrapper.addPlanetType(planet, i);
            }

            UUID robotId = pvo.getReferencingRobotId();
            if (robotId != null) {
                Robot robot = this.robotApplicationService.getById(robotId);
                wrapper.addRobot(robot, i);
            }
            i++;
        }
        return wrapper;
    }
}
