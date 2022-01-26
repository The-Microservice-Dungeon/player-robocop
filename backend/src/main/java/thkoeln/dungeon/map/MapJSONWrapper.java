package thkoeln.dungeon.map;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

import java.util.UUID;

public class MapJSONWrapper {


    @Getter
    @Setter
    private int[] gravity;

    @Getter
    @Setter
    private UUID[] robots;

    @Getter
    @Setter
    private UUID[] planets;


    public MapJSONWrapper(int size){
        this.gravity = new int[size];
        this.planets = new UUID[size];
        this.robots = new  UUID[size];
    }

    public void addRobot( Robot robot, int index){
        try {
            this.robots[index] = robot.getId();
        }catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void addPlanet( Planet planet, int index){

        try {
            this.planets[index] = planet.getId();
        }catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
            System.out.println(exception.getMessage());
        }

    }

    public void addGravity( Planet planet, int index){
      try {
          this.gravity[index] = planet.getMovementDifficulty();
      }catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
          System.out.println(exception.getMessage());
      }

    }







}
