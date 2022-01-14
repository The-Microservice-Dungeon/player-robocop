package thkoeln.dungeon.game.domain.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public enum GameStatus {
    @JsonProperty("created")
    CREATED,
    @JsonProperty("started")
    GAME_RUNNING,
    @JsonProperty("ended")
    GAME_FINISHED,
    ORPHANED // this is the state a game takes when the GameService doesn't list it anymore
}
