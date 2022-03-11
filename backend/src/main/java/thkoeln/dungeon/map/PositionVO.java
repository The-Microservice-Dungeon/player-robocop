package thkoeln.dungeon.map;

import io.micrometer.core.lang.Nullable;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PositionVO {

    private UUID referencingPlanetId;

    private UUID referencingRobotId;

    private int posIndex;

    private int x;

    private int y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionVO that = (PositionVO) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }

    @Override
    public String toString () {
        return "Index: " + this.posIndex + " X: " + this.x + " Y: " + this.y + " Ref Planet: " + this.referencingPlanetId + " Ref Robot: " + this.referencingRobotId;
    }
}
