package thkoeln.dungeon.command;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.game.domain.game.Game;


import java.util.List;
import java.util.UUID;

public interface CommandRepository extends CrudRepository<Command, UUID> {
    List<Command> findAllByCommandTypeEquals(CommandType commandType);
}
