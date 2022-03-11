package thkoeln.dungeon.map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameException;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
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
    int colCount;

    @Getter
    int rowCount;

    @Getter
    int centerIndex;

    @Getter
    int contentLength;

    @ElementCollection
    List<PositionVO> positions;

    @Transient
    private final Logger logger = LoggerFactory.getLogger(Map.class);

    /***
     * Creates a Map and calculates the center Position
     * @param game
     */
    public Map(Game game) {
        this.numberPlayers = game.getNumberOfPlayers();

        if (numberPlayers == 0) {
            throw new GameException("Can't create Map for Game with 0 players!");
        }

        if (numberPlayers < 10) {
            this.mapSize = 15;
        } else if (numberPlayers < 20) {
            this.mapSize = 20;
        } else {
            this.mapSize = 35;
        }
        this.colCount = this.mapSize * 2;
        this.rowCount = this.mapSize * 2;
        this.centerIndex = this.mapSize * this.colCount + this.mapSize;
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

    public PositionVO findPosition(int centerIndex) {
        for (PositionVO position : this.positions) {
            if (position.getPosIndex() == centerIndex)
                return position;
        }
        return null;
    }


    public PositionVO getCenterPosition () {
        return this.findPosition(this.getCenterIndex());
    }

    public PositionVO findPosition(Planet planet) {
        for (PositionVO position : this.positions) {
            if (planet.getPlanetId().equals(position.getReferencingPlanetId())) return position;
        }
        return null;
    }

    public PositionVO findPosition(Robot robot) {
        for (PositionVO position : this.positions) {
            if (robot.getRobotId().equals(position.getReferencingRobotId())) return position;
        }
        return null;
    }

    public void setRobotOnPosition (PositionVO position, Robot robot) {
        this.replacePosition(position, new PositionVO(position.getReferencingPlanetId(), robot.getRobotId(), position.getPosIndex(), position.getX(), position.getY()));
    }

    public void removeRobotOnPosition (PositionVO position) {
        this.replacePosition(position, new PositionVO(position.getReferencingPlanetId(), null, position.getPosIndex(), position.getX(), position.getY()));
    }

    public void initMap() {
        this.positions = new ArrayList<>();
        int posIndex = 0;
        for (int i = 0; i < this.colCount; i++) {
            for (int j = 0; j < this.rowCount; j++) {
                PositionVO tmpPos = new PositionVO(null, null, posIndex, i, j);
                this.positions.add(tmpPos);
                posIndex++;
            }
        }
    }
}
