package thkoeln.dungeon.map;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

@NoArgsConstructor
@Setter
@Service
public class MapApplicationService {

    private Map currentMap;

    public void placeDemoStuff () {
        currentMap.addFirstBot(new Robot(true));
        currentMap.addFirstPlanet(new Planet(true, false));
    }

    public void createMapFromGame (Game game) {
        this.currentMap = new Map(game);
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
