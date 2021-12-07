package thkoeln.dungeon.game.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.game.domain.Game;

import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {

}
