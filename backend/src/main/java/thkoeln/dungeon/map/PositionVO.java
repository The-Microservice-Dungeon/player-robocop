package thkoeln.dungeon.map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class PositionVO {

    private UUID referencingPlanetId;

    private UUID referencingRobotId;

    private int posIndex;

    private int x;

    private int y;

    public void clearRobot () {
        this.referencingRobotId = null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
}
