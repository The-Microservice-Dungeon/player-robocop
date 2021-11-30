package thkoeln.dungeon.player.player;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class GameOptions {
    @Id
    @Getter
    private final UUID id = UUID.randomUUID();

}
