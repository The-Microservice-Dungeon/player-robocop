package thkoeln.dungeon.map;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

import java.util.UUID;

@NoArgsConstructor
@Setter
@Service
public class MapApplicationService {

    private Map currentMap;

    public void placeDemoStuff () {
        currentMap.addFirstBot(new Robot(true));
        currentMap.addFirstPlanet(new Planet(UUID.randomUUID(),true, false));
    }

    public void createMapFromGame (Game game) {
        this.currentMap = new Map(game);
    }

    // TODO: Call on robot spawned event
    public void placeFirstRobotAndPlanet (Robot robot, Planet planet) {
        this.currentMap.addFirstBot(robot);
        this.currentMap.addFirstPlanet(planet);
    }

    // TODO: call on new planet found
    public void addNewPlanet (Planet planet) {
        this.currentMap.addPlanet(planet);
    }

    // TODO: call on Robot move
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
            wrapper.addGravity(pvo.getPlanet(), i);
            wrapper.addPlanetType(pvo.getPlanet(), i);
            wrapper.addRobot(pvo.getRobot(), i);
            i++;
        }
        return wrapper;
    }
}
