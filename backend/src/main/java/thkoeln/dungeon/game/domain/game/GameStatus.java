package thkoeln.dungeon.game.domain.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

@ToString
public enum GameStatus {
    CREATED("created"),
    GAME_RUNNING("game_running"),
    GAME_FINISHED("game_finished"),
    ORPHANED("orphaned"); // this is the state a game takes when the GameService doesn't list it anymore


    private final String key;

    GameStatus(String key) {
        this.key = key;
    }

    @JsonCreator
    public static GameStatus fromString(String key) {
        return key == null ? null : GameStatus.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String getKey() {
        return key;
    }
}
