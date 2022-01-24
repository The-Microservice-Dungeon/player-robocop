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

    @Getter
    int mapSize;

    @Getter
    int anzahlCols;

    @Getter
    int centerIndex;

    /***
     * Creates a Map and calculates the center Position
     * @param gameDto
     */
    public Map(GameDto gameDto){
       this.numberPlayers = gameDto.getParticipatingPlayers().size();
       this.mapSize = numberPlayers;
       this.anzahlCols = this.mapSize * 2;
       this.centerIndex = this.mapSize * this.anzahlCols + this.mapSize;
    }

    public Map() {

    }


    public void addRobot(Robot rob){
        rob.setMap(this);
        robots.add(rob);
    }



}
