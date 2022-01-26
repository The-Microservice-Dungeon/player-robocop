package thkoeln.dungeon.game.domain.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.game.domain.round.Round;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
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
    private final UUID id = UUID.randomUUID();

    // this is the EXTERNAL id that we receive from GameService. We could use this also as our own id, but then
    // we'll run into problems in case GameService messes up their ids (e.g. start the same game twice, etc.) So,
    // we better keep these two apart.
    private UUID gameId;
    private GameStatus gameStatus;

    private Integer maxPlayers;
    private Integer maxRounds;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<UUID> participatingPlayers = new LinkedList<>();

    @Embedded
    private Round round;

    public void startRound(int currentRoundNumber) {
        this.round = new Round(currentRoundNumber);
        logger.info("Round number " + currentRoundNumber + " has begun, fire commands now!");
    }

    public void start() {
        setGameStatus(GameStatus.STARTED);
    }

    public void end() {
        setGameStatus(GameStatus.ENDED);
    }



    @Transient
    private Logger logger = LoggerFactory.getLogger(Game.class);

    public static Game newlyCreatedGame(UUID gameId) {
        Game game = new Game();
        game.setGameId(gameId);
        game.resetToNewlyCreated();
        return game;
    }

    public void resetToNewlyCreated() {
        setGameStatus(GameStatus.CREATED);
        setRound(new Round(0));
        logger.warn("Reset game " + this + " to CREATED!");
    }


    public void makeOrphan() {
        setGameStatus(GameStatus.ORPHANED);
        logger.warn("Marked game " + this + " as ORPHANED!");
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id.equals(game.id);
    }

    public void setCurrentRoundCount(int currentRoundNumber) {
        this.round = new Round(currentRoundNumber);
    }

    public Integer getNumberOfPlayers () {
        return this.participatingPlayers.size();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
