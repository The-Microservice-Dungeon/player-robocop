package thkoeln.dungeon.map;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.game.GameDto;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

@NoArgsConstructor
@Setter
@Service
public class MapService {

    private GameDto[] gameDtos = {};

    public Map getDemoMap () {
        Map demoMap = new Map(this.gameDtos[0]);

        demoMap.addFirstBot(new Robot(true));
        demoMap.addFirstPlanet(new Planet(true, false));

        return demoMap;
    }

    public MapJSONWrapper getLayerMap () {
        Map demoMap = this.getDemoMap();
        MapJSONWrapper wapper = new MapJSONWrapper(demoMap.getContentLength());

        int i = 0;
        for (PositionVO pvo : demoMap.getPositions()) {
            wapper.addGravity(pvo.getPlanet(), i);
            wapper.addPlanetType(pvo.getPlanet(), i);
            wapper.addRobot(pvo.getRobot(), i);
            i++;
        }
        return wapper;
    }
}
