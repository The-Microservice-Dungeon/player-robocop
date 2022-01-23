package thkoeln.dungeon.eventconsumer.robot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;
import thkoeln.dungeon.planet.domain.Planet;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovementEvent extends AbstractEvent {
    private Boolean success;
    private String message;
    private int remainingEnergy;
    @OneToOne
    private Planet planet;

}
