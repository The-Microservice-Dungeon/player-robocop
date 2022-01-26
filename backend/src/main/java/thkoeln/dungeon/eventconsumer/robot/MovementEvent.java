package thkoeln.dungeon.eventconsumer.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;

import javax.annotation.Generated;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "success",
        "message",
        "remainingEnergy",
        "planet",
        "robots"
})
@Generated("jsonschema2pojo")
@Getter
@Entity
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.NONE)
public class MovementEvent extends AbstractEvent {

    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("message")
    private String message;
    @JsonProperty("remainingEnergy")
    private Integer remainingEnergy;
    @JsonProperty("planet")
    @Embedded
    private PlanetMovementDto planet;
    @JsonProperty("robots")
    @ElementCollection
    private List<UUID> robots = null;

    @JsonProperty("success")
    public Boolean getSuccess() {
        return success;
    }

    @JsonProperty("success")
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("remainingEnergy")
    public Integer getRemainingEnergy() {
        return remainingEnergy;
    }

    @JsonProperty("remainingEnergy")
    public void setRemainingEnergy(Integer remainingEnergy) {
        this.remainingEnergy = remainingEnergy;
    }

    @JsonProperty("planet")
    public PlanetMovementDto getPlanet() {
        return planet;
    }

    @JsonProperty("planet")
    public void setPlanet(PlanetMovementDto planet) {
        this.planet = planet;
    }

    @JsonProperty("robots")
    public List<UUID> getRobots() {
        return robots;
    }

    @JsonProperty("robots")
    public void setRobots(List<String> robots) {
        List<UUID> result = new LinkedList<>();
        for (String robot: robots){
            result.add(UUID.fromString(robot));
        }
        this.robots = result;
    }

}
