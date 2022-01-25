package thkoeln.dungeon.map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Getter

public class Map {
    @Id
    private final UUID id = UUID.randomUUID();




    @Getter
    int numberPlayers;

    @Getter
    int mapSize;

    @Getter
    int anzahlCols;

    @Getter
    int centerIndex;

    @Getter
    int contentLength;

    @ElementCollection
    List<PositionVO> positions;

    /***
     * Creates a Map and calculates the center Position
     * @param gameDto
     */
    public Map(GameDto gameDto) {
        this.numberPlayers = gameDto.getParticipatingPlayers().size();

        if (numberPlayers < 10) {
            this.mapSize = 15;
        } else if (numberPlayers < 20) {
            this.mapSize = 20;
        } else {
            this.mapSize = 35;
        }
        this.anzahlCols = this.mapSize * 2;
        this.centerIndex = this.mapSize * this.anzahlCols + this.mapSize;
        this.contentLength = (int) Math.pow((mapSize*2),2);
        this.initMap();
        //this.layers = new UUID[contentLength][contentLength];

    }


    public void addFirstBot(Robot bot){

        this.positions.get(centerIndex).setRobot(bot);

    }

    public void addFirstPlanet(Planet planet){
        this.positions.get(centerIndex).setPlanet(planet);
    }


    public void initMap(){
        this.positions = new ArrayList<>();
        for (int i = 0; i < this.contentLength ; i++) {
                this.positions.add(new PositionVO(i));
        }
    }

    public Map() {



    }
}

class layers{
    int[][] layers;

    public layers(int size) {
        this.layers = new int[size][size];
    }

    public int[][] getLayers() {
        return layers;
    }

    public void setLayers(int[][] layers) {
        this.layers = layers;
    }
}


