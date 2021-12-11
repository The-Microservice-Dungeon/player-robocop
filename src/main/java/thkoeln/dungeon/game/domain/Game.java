package thkoeln.dungeon.game.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.restadapter.GameStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {
    @Id
    private UUID id = UUID.randomUUID();
    @Setter
    private UUID gameId;
    @Setter
    private GameStatus status;

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", status=" + status +
                '}';
    }
}
