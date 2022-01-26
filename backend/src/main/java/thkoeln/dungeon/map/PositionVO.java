package thkoeln.dungeon.map;

import io.micrometer.core.lang.Nullable;
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
@Getter
@Setter
public class PositionVO {

    @OneToOne
    @Nullable
    @JoinColumn(name = "planet_ID")
    private Planet planet;

    @OneToOne
    @Nullable
    @JoinColumn(name = "robot_ID")
    private Robot robot;

    private int posIndex;

    private int x;

    private int y;

    public void clearRobot () {
        this.robot = null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
}
