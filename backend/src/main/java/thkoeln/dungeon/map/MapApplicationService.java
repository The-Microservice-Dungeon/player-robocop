package thkoeln.dungeon.map;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.robot.NeighbourData;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameException;
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
    private final SimpMessagingTemplate websocket;


    private final Logger logger = LoggerFactory.getLogger(MapApplicationService.class);


    private Map currentMap;

    @Autowired
    public MapApplicationService(PlanetApplicationService planetApplicationService, RobotApplicationService robotApplicationService, MapRepository mapRepository, SimpMessagingTemplate websocket) {
        this.planetApplicationService = planetApplicationService;
        this.robotApplicationService = robotApplicationService;
        this.mapRepository = mapRepository;
        this.websocket = websocket;
    }

    public void createMapFromGame (Game game) {
        if (this.currentMap != null) {
            logger.warn("Map already exists! Don't create another");
            return;
        }
        try {
            this.currentMap = new Map(game);
        } catch (GameException e) {
            logger.error("Failed to create map: " + e.getMessage());
            return;
        }
        mapRepository.save(currentMap);
        this.websocket.convertAndSend("map_events", "new_map_created");
    }

    public void deleteMap () {
        this.currentMap = null;
        mapRepository.deleteAll();
    }

    private void placeFirstRobotAndPlanet (Robot robot, Planet planet) {
        if (this.currentMap == null) {
            logger.error("Cant Place Robots / Planets. No Map exists.");
            return;
        }

        PositionVO center = this.currentMap.getCenterPosition();

        logger.info("Placing first Robot and Planet on " + center);

        planet = this.planetApplicationService.setPlanetPosition(planet, center);
        robot = this.robotApplicationService.setRobotPosition(robot, center);

        addFirstPlanet(planet);
        addFirstBot(robot);

        mapRepository.save(currentMap);

        this.websocket.convertAndSend("map_events", "planets_changed");
    }

    public void handleNewRobotSpawn (Robot robot, UUID planetId) {
        if (currentMap==null){
            logger.error("Map not initialized yet");
            return;
        }
        Planet firstPlanet = planetApplicationService.createStartPlanet(planetId);

        if (this.planetApplicationService.isFirstPlanet()){
            this.placeFirstRobotAndPlanet(robot, firstPlanet);
        }
    }

    private void addFirstPlanet(Planet planet) {
        PositionVO centerPos = this.currentMap.getCenterPosition();
        PositionVO centerPosWithPlanet = new PositionVO(planet.getPlanetId(), centerPos.getReferencingRobotId(), centerPos.getPosIndex(), centerPos.getX(), centerPos.getY());
        this.currentMap.replacePosition(centerPos, centerPosWithPlanet);
        planetApplicationService.setPlanetPosition(planet, centerPosWithPlanet);
        exploreNeighbours(planet);
    }

    private void addFirstBot(Robot bot) {
        PositionVO centerPos = this.currentMap.getCenterPosition();
        PositionVO centerPosWithRobot = new PositionVO(centerPos.getReferencingPlanetId(), bot.getRobotId(), centerPos.getPosIndex(), centerPos.getX(), centerPos.getY());
        this.currentMap.replacePosition(centerPos, centerPosWithRobot);
    }

    public void handleNewPlanetNeighbours (Planet planet, List<NeighbourData> neighbours) {
        this.planetApplicationService.generateNeighboursForPlanet(planet, neighbours);
        planet = this.planetApplicationService.refreshPlanet(planet);

        if (planet.hasNeighbours()) {
            exploreNeighbours(planet);
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
        this.websocket.convertAndSend("map_events", "robot_moved");
    }

    public Planet getPlanetForRobot (Robot robot) {
        PositionVO robotPosition = this.currentMap.findPosition(robot);
        if (robotPosition == null) {
            logger.warn("Robot " + robot + " has no position on the current map! Can't get planet");
            return null;
        }

        return planetApplicationService.getById(robotPosition.getReferencingPlanetId());
    }

    public List<Planet> getNeighboursForPlanet(Planet planet) {
        Planet refreshedPlanet = this.planetApplicationService.getById(planet.getPlanetId());
        return refreshedPlanet.allNeighbours();
    }

    public List<Planet> getNeighboursForPosition (PositionVO positionVO) {
        PositionVO planetPosition = this.currentMap.findPosition(positionVO);
        if (planetPosition == null) {
            logger.warn("Position " + positionVO + " is not on this map!");
            return null;
        }

        UUID planetId = planetPosition.getReferencingPlanetId();
        if (planetId == null) {
            logger.warn("There is no Planet on position " + positionVO);
            return null;
        }

        Planet planet = this.planetApplicationService.getById(planetId);
        if (planet == null) {
            logger.warn("Planet with Id " + planetId + " not found!");
            return null;
        }

        return planet.allNeighbours();
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
            } else {
                wrapper.addGravity(null, i);
                wrapper.addPlanetType(null, i);
            }

            UUID robotId = pvo.getReferencingRobotId();
            if (robotId != null) {
                Robot robot = this.robotApplicationService.getById(robotId);
                wrapper.addRobot(robot, i);
            } else {
                wrapper.addRobot(null, i);
            }

            i++;
        }
        return wrapper;
    }

    private void exploreNeighbours (Planet planet) {
        PositionVO position = planet.getPosition();

        logger.info("Exploring neighbours around planet " + planet);

        boolean neighbourFound = false;

        if (planet.getEastNeighbour() != null) {
            PositionVO pos = this.currentMap.findPosition(position.getX() - 1, position.getY());
            PositionVO neighbourPosition = new PositionVO(planet.getEastNeighbour().getPlanetId(), pos.getReferencingRobotId(), pos.getPosIndex(), pos.getX(), pos.getY());
            this.currentMap.replacePosition(pos, neighbourPosition);
            Planet updatedNeighbour = planetApplicationService.setPlanetPosition(planet.getEastNeighbour(), neighbourPosition);
            logger.info("Found East Neighbour (" + updatedNeighbour + ")!");
            neighbourFound = true;
        }
        if (planet.getWestNeighbour() != null) {
            PositionVO pos = this.currentMap.findPosition(position.getX() + 1, position.getY());
            PositionVO neighbourPosition = new PositionVO(planet.getWestNeighbour().getPlanetId(), pos.getReferencingRobotId(), pos.getPosIndex(), pos.getX(), pos.getY());
            this.currentMap.replacePosition(pos, neighbourPosition);
            Planet updatedNeighbour = planetApplicationService.setPlanetPosition(planet.getWestNeighbour(), neighbourPosition);
            logger.info("Found West Neighbour (" + updatedNeighbour+ ")!");
            neighbourFound = true;
        }
        if (planet.getNorthNeighbour() != null) {
            PositionVO pos = this.currentMap.findPosition(position.getX(), position.getY() - 1);
            PositionVO neighbourPosition = new PositionVO(planet.getNorthNeighbour().getPlanetId(), pos.getReferencingRobotId(), pos.getPosIndex(), pos.getX(), pos.getY());
            this.currentMap.replacePosition(pos, neighbourPosition);
            Planet updatedNeighbour = planetApplicationService.setPlanetPosition(planet.getNorthNeighbour(), neighbourPosition);
            logger.info("Found North Neighbour (" + updatedNeighbour+ ")!");
            neighbourFound = true;
        }
        if (planet.getSouthNeighbour() != null) {
            PositionVO pos = this.currentMap.findPosition(position.getX(), position.getY() + 1);
            PositionVO neighbourPosition = new PositionVO(planet.getSouthNeighbour().getPlanetId(), pos.getReferencingRobotId(), pos.getPosIndex(), pos.getX(), pos.getY());
            this.currentMap.replacePosition(pos, neighbourPosition);
            Planet updatedNeighbour = planetApplicationService.setPlanetPosition(planet.getSouthNeighbour(), neighbourPosition);
            logger.info("Found South Neighbour (" + updatedNeighbour + ")!");
            neighbourFound = true;
        }

        this.mapRepository.save(this.currentMap);
        if (neighbourFound) {
            this.websocket.convertAndSend("map_events", "planets_changed");
        }
    }

    public Integer getMapSize () {
        return this.currentMap.getMapSize();
    }
}
