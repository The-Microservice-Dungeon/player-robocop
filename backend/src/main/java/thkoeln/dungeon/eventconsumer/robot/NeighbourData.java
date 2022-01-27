package thkoeln.dungeon.eventconsumer.robot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import thkoeln.dungeon.planet.domain.CompassDirection;

import javax.annotation.Generated;
import javax.persistence.Embeddable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "planetId",
        "movementDifficulty",
        "direction"
})
@Generated("jsonschema2pojo")
@Embeddable
public class NeighbourData {

    @JsonProperty("planetId")
    private UUID planetId;
    @JsonProperty("movementDifficulty")
    private Integer movementDifficulty;
    @JsonProperty("direction")
    private CompassDirection direction;

    @JsonProperty("planetId")
    public UUID getPlanetId() {
        return planetId;
    }

    @JsonProperty("planetId")
    public void setPlanetId(String planetId) {
        this.planetId = UUID.fromString(planetId);
    }

    @JsonProperty("movementDifficulty")
    public Integer getMovementDifficulty() {
        return movementDifficulty;
    }

    @JsonProperty("movementDifficulty")
    public void setMovementDifficulty(Integer movementDifficulty) {
        this.movementDifficulty = movementDifficulty;
    }

    @JsonProperty("direction")
    public CompassDirection getDirection() {
        return direction;
    }

    @JsonProperty("direction")
    public void setDirection(String direction) {
        this.direction = CompassDirection.valueOf(direction);
    }

}
