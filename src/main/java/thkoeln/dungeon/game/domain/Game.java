package thkoeln.dungeon.game.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Game {
    @Id
    private UUID id = UUID.randomUUID();

    // this is the EXTERNAL id that we receive from GameService. We could use this also as our own id, but then
    // we'll run into problems in case GameService messes up their ids (e.g. start the same game twice, etc.) So,
    // we better keep these two apart.
    private UUID gameId;
    private GameStatus gameStatus;
    private Integer currentRoundCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id.equals(game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
