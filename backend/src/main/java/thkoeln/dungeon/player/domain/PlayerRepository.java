package thkoeln.dungeon.player.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.game.domain.game.Game;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends CrudRepository<Player, UUID> {
    List<Player> findAll();

    List<Player> findByCurrentGame(Game game);

    Player findByNameAndEmail(String name, String email);

    List<Player> findByRegistrationTransactionId(UUID registrationTransactonId);

    Optional<Player> findByPlayerId(UUID playerId);
}
