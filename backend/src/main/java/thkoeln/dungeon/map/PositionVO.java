package thkoeln.dungeon.map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
public class PositionVO {

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "planet_ID")
    private Planet planet;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "robot_ID")
    private Robot robot;

    @Getter
    @Setter
    private int posIndex;


    @Getter
    @Setter
    private int x;

    @Getter
    @Setter
    private int y;

    public void clearRobot () {
        this.robot = null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
}
