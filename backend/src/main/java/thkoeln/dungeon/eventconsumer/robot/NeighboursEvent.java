package thkoeln.dungeon.eventconsumer.robot;

import javax.annotation.Generated;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;
import thkoeln.dungeon.planet.domain.CompassDirection;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "planetId",
        "movementDifficulty",
        "direction"
})
@Generated("jsonschema2pojo")
@Getter
@Entity
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.NONE)
public class NeighboursEvent extends AbstractEvent {

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
