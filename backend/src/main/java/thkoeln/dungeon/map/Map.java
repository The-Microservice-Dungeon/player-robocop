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


    public void replacePosition (PositionVO oldPos, PositionVO newPos) {
        for (PositionVO position : this.positions) {
            if (position == oldPos) this.positions.set(oldPos.getPosIndex(), newPos);
        }
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
        this.replacePosition(position, new PositionVO(position.getReferencingPlanetId(), robot.getRobotId(), position.getPosIndex(), position.getX(), position.getY()));
    }

    public void removeRobotOnPosition (PositionVO position) {
        this.replacePosition(position, new PositionVO(position.getReferencingPlanetId(), null, position.getPosIndex(), position.getX(), position.getY()));
    }


    public void exploreNeighbours(Planet planet) {

        PositionVO position = planet.getPosition();

        if (planet.getEastNeighbour() != null) {
            PositionVO pos = findPosition(position.getX() - 1, position.getY());
            this.replacePosition(pos, new PositionVO(planet.getEastNeighbour().getPlanetId(), pos.getReferencingRobotId(), pos.getPosIndex(), pos.getX(), pos.getY()));
            planet.getEastNeighbour().setPosition(findPosition(position.getX() - 1, position.getY()));
        }
        if (planet.getWestNeighbour() != null) {
            PositionVO pos = findPosition(position.getX() + 1, position.getY());
            this.replacePosition(pos, new PositionVO(planet.getWestNeighbour().getPlanetId(), pos.getReferencingRobotId(), pos.getPosIndex(), pos.getX(), pos.getY()));
            planet.getWestNeighbour().setPosition(findPosition(position.getX() + 1, position.getY()));
        }


        if (planet.getNorthNeighbour() != null) {
            PositionVO pos = findPosition(position.getX(), position.getY() - 1);
            this.replacePosition(pos, new PositionVO(planet.getNorthNeighbour().getPlanetId(), pos.getReferencingRobotId(), pos.getPosIndex(), pos.getX(), pos.getY()));
            planet.getNorthNeighbour().setPosition(findPosition(position.getX(), position.getY() - 1));
        }
        if (planet.getSouthNeighbour() != null) {
            PositionVO pos = findPosition(position.getX(), position.getY() + 1);
            this.replacePosition(pos, new PositionVO(planet.getSouthNeighbour().getPlanetId(), pos.getReferencingRobotId(), pos.getPosIndex(), pos.getX(), pos.getY()));
            planet.getSouthNeighbour().setPosition(findPosition(position.getX(), position.getY() + 1));
        } else {
            System.out.println("No other Planets here");
        }
    }

    public void addFirstBot(Robot bot) {
        PositionVO pos = this.positions.get(centerIndex);
        this.replacePosition(pos, new PositionVO(pos.getReferencingPlanetId(), bot.getRobotId(), pos.getPosIndex(), anzahlCols / 2,anzahlCols / 2));
        bot.setPosition(this.positions.get(centerIndex));
    }

    public void addFirstPlanet(Planet planet) {
        PositionVO pos = this.positions.get(centerIndex);
        this.replacePosition(pos, new PositionVO(planet.getPlanetId(), pos.getReferencingRobotId(), pos.getPosIndex(), anzahlCols / 2,anzahlCols / 2));
        planet.setPosition(this.positions.get(centerIndex));
        exploreNeighbours(planet);
    }

    public void addNeighboursOfPlanetToMap(Planet planet) {
        this.exploreNeighbours(planet);
    }

    public void initMap() {
        this.positions = new ArrayList<>();
        for (int i = 0; i < this.anzahlCols; i++) {

            for (int j = 0; j < this.anzahlCols; j++) {
                PositionVO tmpPos = new PositionVO(null, null, i + i, i, j);
                this.positions.add(tmpPos);
            }
        }
    }
}
