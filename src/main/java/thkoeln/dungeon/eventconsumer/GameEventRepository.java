package thkoeln.dungeon.eventconsumer;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameStatus;

import java.util.UUID;

public interface GameEventRepository extends CrudRepository<GameEvent, UUID> {

}
