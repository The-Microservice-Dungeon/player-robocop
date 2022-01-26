package thkoeln.dungeon.map;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.UUID;

@Embeddable
public class PositionVO {

    @Getter
    @Setter
    private UUID planet;

    @Getter
    @Setter
    private UUID robot;

    @Getter
    @Setter
    private int posIndex;




    public PositionVO(int posIndex){
        this.posIndex = posIndex;
    }


    public PositionVO() {

    }
}
