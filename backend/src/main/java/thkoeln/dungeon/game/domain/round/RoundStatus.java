package thkoeln.dungeon.game.domain.round;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RoundStatus {
    BEFORE_FIRST_ROUND,
    @JsonProperty("started")
    STARTED,
    @JsonProperty("command input ended")
    COMMAND_INPUT_ENDED,
    @JsonProperty("ended")
    ENDED
}
