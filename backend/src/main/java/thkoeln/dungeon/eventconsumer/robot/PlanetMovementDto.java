package thkoeln.dungeon.eventconsumer.robot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import thkoeln.dungeon.planet.domain.PlanetType;
import thkoeln.dungeon.planet.domain.ResourceType;

import javax.annotation.Generated;
import javax.persistence.Embeddable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "planetId",
        "movementDifficulty",
        "planetType",
        "resourceType"
})
@Generated("jsonschema2pojo")
@Embeddable
public class PlanetMovementDto {

    @JsonProperty("planetId")
    private UUID planetId;
    @JsonProperty("movementDifficulty")
    private Integer movementDifficulty;
    @JsonProperty("planetType")
    private PlanetType planetType;
    @JsonProperty("resourceType")
    private ResourceType resourceType;

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

    @JsonProperty("planetType")
    public PlanetType getPlanetType() {
        return planetType;
    }

    @JsonProperty("planetType")
    public void setPlanetType(String planetType) {
        this.planetType = PlanetType.valueOf(planetType);
    }

    @JsonProperty("resourceType")
    public ResourceType getResourceType() {
        return resourceType;
    }

    @JsonProperty("resourceType")
    public void setResourceType(String resourceType) {
        this.resourceType = ResourceType.valueOf(resourceType);
    }

}
