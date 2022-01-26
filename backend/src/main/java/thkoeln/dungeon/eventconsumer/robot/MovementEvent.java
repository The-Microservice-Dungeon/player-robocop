package thkoeln.dungeon.eventconsumer.robot;

import lombok.*;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;

import javax.persistence.Entity;
import javax.persistence.OneToOne;


import java.util.*;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "success",
        "message",
        "remainingEnergy",
        "planet",
        "robots"
})
@Generated("jsonschema2pojo")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class MovementEvent extends AbstractEvent {

    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("message")
    private String message;
    @JsonProperty("remainingEnergy")
    private Integer remainingEnergy;
    @JsonProperty("planet")
    private PlanetMovementDto planet;
    @JsonProperty("robots")
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
