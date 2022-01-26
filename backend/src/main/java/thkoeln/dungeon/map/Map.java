package thkoeln.dungeon.map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.game.domain.game.GameDto;
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


    public PositionVO findPosition(int x, int y){
        for (PositionVO position: this.positions
             ) {
            if(position.getX() == x && position.getY() == y)
                return position;
        }
        return null;
    }




    public void exploreNeighbours(Planet planet) {

        PositionVO position = planet.getPosition();

        if (planet.getEastNeighbour() != null) {
            findPosition(position.getX() - 1, position.getY()).setPlanet(planet.getEastNeighbour());
            planet.getEastNeighbour().setPosition(findPosition(position.getX() - 1, position.getY()));
        }
        if (planet.getWestNeighbour() != null) {
            findPosition(position.getX() + 1, position.getY()).setPlanet(planet.getWestNeighbour());
            planet.getWestNeighbour().setPosition(findPosition(position.getX() + 1, position.getY()));
        }


        if (planet.getNorthNeighbour() != null) {
            findPosition(position.getX(), position.getY() - 1).setPlanet(planet.getNorthNeighbour());
            planet.getNorthNeighbour().setPosition(findPosition(position.getX(), position.getY() - 1));
        }
        if (planet.getSouthNeighbour() != null) {
            findPosition(position.getX(), position.getY() + 1).setPlanet(planet.getSouthNeighbour());
            planet.getSouthNeighbour().setPosition(findPosition(position.getX(), position.getY() + 1));
        } else{
            System.out.println("No other Planets here");
        }
    }

    public void addFirstBot(Robot bot){

        this.positions.get(centerIndex).setRobot(bot);
        this.positions.get(centerIndex).setX(anzahlCols/2);
        this.positions.get(centerIndex).setY(anzahlCols/2);
        bot.setPosition( this.positions.get(centerIndex));


    }

    public void addFirstPlanet(Planet planet){

        this.positions.get(centerIndex).setPlanet(planet);
        this.positions.get(centerIndex).setX(anzahlCols/2);
        this.positions.get(centerIndex).setY(anzahlCols/2);
        planet.setPosition( this.positions.get(centerIndex));
        exploreNeighbours(planet);
    }


    public void initMap(){
        this.positions = new ArrayList<>();
        for (int i = 0; i < this.anzahlCols ; i++) {

            for (int j = 0; j < this.anzahlCols; j++) {
                PositionVO tmpPos = new PositionVO();
                tmpPos.setX(i);
                tmpPos.setY(j);
                this.positions.add(tmpPos);
            }


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


