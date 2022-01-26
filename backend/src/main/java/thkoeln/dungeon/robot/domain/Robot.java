package thkoeln.dungeon.robot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.map.Map;
import thkoeln.dungeon.map.PositionVO;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Robot {
    @Id
    private final UUID id = UUID.randomUUID();

    //todo: not yet implemented
    private UUID externalId = UUID.randomUUID();
    private int energy;
    private int health;

    @Getter
    @Setter
    private boolean dummy = false;

    @Embedded
    private PositionVO position;

    @ManyToOne(optional = false)
    private Map maps;

    public Robot(boolean isDummy) {
        this.dummy = isDummy;
    }
}

