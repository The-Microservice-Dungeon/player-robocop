package thkoeln.dungeon.player.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import thkoeln.dungeon.game.domain.Game;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Setter
@Getter
@ToString
public class GameParticipation {
    @Id
    private final UUID id = UUID.randomUUID();
    private Integer money;

    @ManyToOne
    private Game game;
    @ManyToOne
    private Player player;

}
