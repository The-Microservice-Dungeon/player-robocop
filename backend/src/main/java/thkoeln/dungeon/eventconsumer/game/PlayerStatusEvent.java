package thkoeln.dungeon.eventconsumer.game;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor ( access = AccessLevel.PROTECTED )
public class PlayerStatusEvent extends AbstractEvent {
    private UUID playerId;
    private String name;
    public boolean isValid() {
        return ( playerId != null );
    }
}
