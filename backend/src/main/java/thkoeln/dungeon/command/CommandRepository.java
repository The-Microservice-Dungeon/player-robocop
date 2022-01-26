package thkoeln.dungeon.command;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.player.domain.Player;


import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandRepository extends CrudRepository<Command, UUID> {
    List<Command> findAllByCommandTypeEquals(CommandType commandType);
    Optional<Command> findByTransactionId(UUID transactionId);
    List<Command> findByPlayer(Player player);
}
