package thkoeln.dungeon.eventconsumer.game;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RoundStatusEventRepository extends CrudRepository<RoundStatusEvent, UUID> {
}
