package thkoeln.dungeon.eventconsumer.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;
import thkoeln.dungeon.planet.domain.*;

import java.awt.*;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovementEventPayload {
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("message")
    private String message;
    @JsonProperty("remainingEnergy")
    private int remainingEnergy;
    @JsonProperty("planet")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private PlanetMovementDto planetMovementDto;

    static class PlanetMovementDto{
        @JsonProperty("planetId")
        private UUID planetId;
        @JsonProperty("movementDifficulty")
        private int movementDifficulty;
        @JsonProperty("planetType")
        private PlanetType planetType;
        @JsonProperty("resourceType")
        private ResourceType resourceType;
        public List<RobotPayloadDto> robotDtoList;
    }
}
