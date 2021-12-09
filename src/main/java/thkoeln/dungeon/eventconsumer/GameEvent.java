package thkoeln.dungeon.eventconsumer;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.game.domain.GameStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
public class GameEvent {
    @Id
    private UUID id = UUID.randomUUID();
    @Setter
    private UUID gameId;
    @Setter
    private GameStatus gameStatus;
}
