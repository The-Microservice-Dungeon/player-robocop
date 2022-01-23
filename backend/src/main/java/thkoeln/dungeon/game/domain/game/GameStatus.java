package thkoeln.dungeon.game.domain.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

@ToString
public enum GameStatus {
    @JsonProperty("created")
    CREATED("created"),
    @JsonProperty("started")
    STARTED("started"),
    @JsonProperty("ended")
    ENDED("ended"),
    //State that game takes if it isn't listed by the game service anymore.
    ORPHANED("orphaned");

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
