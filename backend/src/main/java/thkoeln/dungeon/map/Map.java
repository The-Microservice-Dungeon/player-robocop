package thkoeln.dungeon.map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
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
     * @param game
     */
    public Map(Game game) {
        this.numberPlayers = game.getNumberOfPlayers();

        if (numberPlayers < 10) {
            this.mapSize = 15;
        } else if (numberPlayers < 20) {
            this.mapSize = 20;
        } else {
            this.mapSize = 35;
        }
        this.anzahlCols = this.mapSize * 2;
        this.centerIndex = this.mapSize * this.anzahlCols + this.mapSize;
        this.contentLength = (int) Math.pow((mapSize * 2), 2);
        this.initMap();
    }


    public PositionVO findPosition(PositionVO pPosition) {
        for (PositionVO position : this.positions) {
            if (position == pPosition) return position;
        }
        return null;
    }

    public PositionVO findPosition(int x, int y) {
        for (PositionVO position : this.positions) {
            if (position.getX() == x && position.getY() == y)
                return position;
        }
        return null;
    }

    public PositionVO findPosition(Planet planet) {
        for (PositionVO position : this.positions) {
            if (position.getReferencingPlanetId() == planet.getPlanetId()) return position;
        }
        return null;
    }

    public PositionVO findPosition(Robot robot) {
        for (PositionVO position : this.positions) {
            if (position.getReferencingRobotId() == robot.getRobotId()) return position;
        }
        return null;
    }

    public void setRobotOnPosition (PositionVO position, Robot robot) {
        this.findPosition(position).setReferencingRobotId(robot.getRobotId());
    }

    public void removeRobotOnPosition (PositionVO position) {
        this.findPosition(position).clearRobot();
    }


    public void exploreNeighbours(Planet planet) {

        PositionVO position = planet.getPosition();

        if (planet.getEastNeighbour() != null) {
            findPosition(position.getX() - 1, position.getY()).setReferencingPlanetId(planet.getEastNeighbour().getPlanetId());
            planet.getEastNeighbour().setPosition(findPosition(position.getX() - 1, position.getY()));
        }
        if (planet.getWestNeighbour() != null) {
            findPosition(position.getX() + 1, position.getY()).setReferencingPlanetId(planet.getWestNeighbour().getPlanetId());
            planet.getWestNeighbour().setPosition(findPosition(position.getX() + 1, position.getY()));
        }


        if (planet.getNorthNeighbour() != null) {
            findPosition(position.getX(), position.getY() - 1).setReferencingPlanetId(planet.getNorthNeighbour().getPlanetId());
            planet.getNorthNeighbour().setPosition(findPosition(position.getX(), position.getY() - 1));
        }
        if (planet.getSouthNeighbour() != null) {
            findPosition(position.getX(), position.getY() + 1).setReferencingPlanetId(planet.getSouthNeighbour().getPlanetId());
            planet.getSouthNeighbour().setPosition(findPosition(position.getX(), position.getY() + 1));
        } else {
            System.out.println("No other Planets here");
        }
    }

    public void addFirstBot(Robot bot) {
        this.positions.get(centerIndex).setReferencingRobotId(bot.getRobotId());
        this.positions.get(centerIndex).setX(anzahlCols / 2);
        this.positions.get(centerIndex).setY(anzahlCols / 2);
        bot.setPosition(this.positions.get(centerIndex));
    }

    public void addFirstPlanet(Planet planet) {
        this.positions.get(centerIndex).setReferencingPlanetId(planet.getPlanetId());
        this.positions.get(centerIndex).setX(anzahlCols / 2);
        this.positions.get(centerIndex).setY(anzahlCols / 2);
        planet.setPosition(this.positions.get(centerIndex));
        exploreNeighbours(planet);
    }

    public void addNeighboursOfPlanetToMap(Planet planet) {
        this.exploreNeighbours(planet);
        Random rand = new Random();
        //Randomness will be replaced another time
        this.getPositions().get(rand.nextInt(this.contentLength)).setReferencingPlanetId(planet.getPlanetId());
    }

    public void initMap() {
        this.positions = new ArrayList<>();
        for (int i = 0; i < this.anzahlCols; i++) {

            for (int j = 0; j < this.anzahlCols; j++) {
                PositionVO tmpPos = new PositionVO();
                tmpPos.setX(i);
                tmpPos.setY(j);
                this.positions.add(tmpPos);
            }
        }
    }
}
