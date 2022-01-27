package thkoeln.dungeon.robot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import thkoeln.dungeon.map.Map;
import thkoeln.dungeon.map.PositionVO;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Robot {
    @Id
    private final UUID robotId;

    private int energy;
    private int health;

    private boolean dummy = false;

    @Setter
    @Nullable
    @Embedded
    private PositionVO position = new PositionVO();

    public Robot(boolean isDummy) {
        this.robotId = UUID.randomUUID();
        this.dummy = isDummy;
    }
    public Robot(UUID robotId){
        this.robotId = robotId;
    }
    protected Robot(){
        this.robotId = UUID.randomUUID();
    }
}

