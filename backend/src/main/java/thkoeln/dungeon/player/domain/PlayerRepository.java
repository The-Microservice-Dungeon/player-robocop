package thkoeln.dungeon.player.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.game.domain.game.Game;

import java.util.List;
import java.util.UUID;

public interface PlayerRepository extends CrudRepository<Player, UUID> {
    List<Player> findAll();

    List<Player> findByGameParticipations_Game(Game game);

    Player findByNameAndEmail(String name, String email);
}
