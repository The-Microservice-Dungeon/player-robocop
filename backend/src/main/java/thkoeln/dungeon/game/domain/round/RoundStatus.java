package thkoeln.dungeon.game.domain.round;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RoundStatus {
    @JsonProperty("started")
    INITIALIZED,
    @JsonProperty("commandInputEnded")
    RUNNING,
    @JsonProperty("ended")
    ENDED
}
