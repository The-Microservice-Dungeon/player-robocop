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


    public PositionVO findPosition(int x, int y) {
        for (PositionVO position : this.positions
        ) {
            if (position.getX() == x && position.getY() == y)
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
        } else {
            System.out.println("No other Planets here");
        }
    }

    /***
     * Hinzufügen des Ersten Bots
     * @param bot
     */
    public void addFirstBot(Robot bot) {
        this.positions.get(centerIndex).setRobot(bot);
        this.positions.get(centerIndex).setX(anzahlCols / 2);
        this.positions.get(centerIndex).setY(anzahlCols / 2);
        bot.setPosition(this.positions.get(centerIndex));
    }

    /***
     * Hinzufügen des ersten Planeten
     * @param planet
     */
    public void addFirstPlanet(Planet planet) {
        this.positions.get(centerIndex).setPlanet(planet);
        this.positions.get(centerIndex).setX(anzahlCols / 2);
        this.positions.get(centerIndex).setY(anzahlCols / 2);
        planet.setPosition(this.positions.get(centerIndex));
        exploreNeighbours(planet);
    }

    /***
     * Robot zur map hinzufügen
     * TODO: Logik wird noch angepasst
     * @param robot
     */
    public void addRobot(Robot robot, int index){

        this.positions.get(index).setRobot(robot);
        robot.setPosition(this.positions.get(index));

    }

    /***
     *  Planet zur map hinzufügen
     * TODO: Logik wird noch angepasst
     * @param planet Planet
     * @param index wo soll eingefügt werden
     */

    public void addPlanet(Planet planet, int index){
        this.positions.get(index).setPlanet(planet);
        planet.setPosition(this.positions.get(index));

    }

    /***
     * tracks the Movement of a bot to another planet
     * @param bot Robot
     * @param planetID Planet ID of the target Planet
     */
    public void botMovement(Robot bot,UUID planetID){

        PositionVO tmpPos = bot.getPosition();
        bot.setPosition(null);
        tmpPos.setRobot(null);

        for (PositionVO pos: this.getPositions()
             ) {
            if(pos.getPlanet().getId() == planetID) {
                pos.setRobot(bot);
                bot.setPosition(pos);
            }
        }

    }



    /***
     * Initialisierung der Map
     */
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
