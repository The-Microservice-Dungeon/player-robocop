package thkoeln.dungeon.map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

@Entity
@Getter

public class Map {
    @Id
    private final UUID id = UUID.randomUUID();

    @Getter
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "map")
    List<Planet> planets;

    @Getter
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "map")
    List<Robot> robots;

    @Getter
    int numberPlayers;

    public Map(GameDto gameDto){
       numberPlayers = gameDto.getParticipatingPlayers().size();
    }

    public Map() {

    }


    public void addRobot(Robot rob){
        rob.setMap(this);
        robots.add(rob);
    }



}
