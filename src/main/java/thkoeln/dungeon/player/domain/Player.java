package thkoeln.dungeon.player.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.game.domain.Game;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Player {
    @Id
    private final UUID id = UUID.randomUUID();
    private String name;
    private String email;
    private UUID bearerToken;

    @ManyToOne
    private Game game;

    public void playRound() {

    }
}
