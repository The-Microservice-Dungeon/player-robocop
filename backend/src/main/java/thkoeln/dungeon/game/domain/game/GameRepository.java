package thkoeln.dungeon.game.domain.game;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {
    List<Game> findByGameId(UUID gameId);

    boolean existsByGameId(UUID gameId);

    List<Game> findAllByGameStatusEquals(GameStatus gameStatus);

    List<Game> findAllByGameStatusBetween(GameStatus gameStatus1, GameStatus gameStatus2);

    List<Game> findAll();
}
