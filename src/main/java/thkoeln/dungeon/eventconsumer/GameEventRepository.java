package thkoeln.dungeon.eventconsumer;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GameEventRepository extends CrudRepository<GameEvent, UUID> {

}
