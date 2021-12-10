package thkoeln.dungeon.game.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.game.domain.Game;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {
    public List<Game> findAllByStatusEquals(GameStatus gameStatus );
    public List<Game> findAllByStatusBetween( GameStatus gameStatus1, GameStatus gameStatus2 );
}
