package thkoeln.dungeon.map;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

public class MapJSONWrapper {

    @Getter
    @Setter
    private Integer[] gravity;

    @Getter
    @Setter
    private Integer[] robots;

    @Getter
    @Setter
    private Integer[] types;


    public MapJSONWrapper(int size) {
        this.gravity = new Integer[size];
        this.types = new Integer[size];
        this.robots = new Integer[size];
    }

    public void addRobot(Robot robot, int index) {
        try {
            this.robots[index] = robot != null ? 10 : -1;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void addPlanetType(Planet planet, int index) {
        try {
            int type = -1;

            if (planet != null) {
                if (planet.getResourceType() != null) {
                    switch (planet.getResourceType()) {
                        case COAL -> type = 4;
                        case IRON -> type = 5;
                        case GEM -> type = 6;
                        case PLATIN -> type = 7;
                    }
                }

                if (planet.isSpaceStation()) type = 3;
                //System.out.println("Planet " + planet + " mapped to: " + type);
            }

            this.types[index] = type;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
            System.out.println(exception.getMessage());
        }

    }

    public void addGravity(Planet planet, int index) {
        try {
            int gravity = -1;
            if (planet != null) {
                Integer movementDifficulty = planet.getMovementDifficulty();
                if (movementDifficulty == null) {
                    gravity = 9;
                } else {
                    gravity = movementDifficulty;
                }

                //System.out.println("Movement Difficulty: " + planet.getMovementDifficulty() + " mapped to: " + gravity);
            }

            this.gravity[index] = gravity;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
            System.out.println(exception.getMessage());
        }

    }
}
