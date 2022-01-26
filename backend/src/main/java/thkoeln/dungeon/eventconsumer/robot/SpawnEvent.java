package thkoeln.dungeon.eventconsumer.robot;


import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Generated;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "robotId",
        "playerId",
        "otherSeeableRobots"
})
@Generated("jsonschema2pojo")
@Getter
@Entity
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.NONE)
public class SpawnEvent extends AbstractEvent {

    @JsonProperty("robotId")
    private UUID robotId;
    @JsonProperty("playerId")
    private UUID playerId;
    @JsonProperty("otherSeeableRobots")
    @ElementCollection
    private List<UUID> otherSeeableRobots = null;

    @JsonProperty("robotId")
    public UUID getRobotId() {
        return robotId;
    }

    @JsonProperty("robotId")
    public void setRobotId(String robotId) {
        this.robotId = UUID.fromString(robotId);
    }

    @JsonProperty("playerId")
    public UUID getPlayerId() {
        return playerId;
    }

    @JsonProperty("playerId")
    public void setPlayerId(String playerId) {
        this.playerId = UUID.fromString(playerId);
    }

    @JsonProperty("otherSeeableRobots")
    public List<UUID> getOtherSeeableRobots() {
        return otherSeeableRobots;
    }

    @JsonProperty("otherSeeableRobots")
    public void setOtherSeeableRobots(List<String> otherSeeableRobots) {
        List<UUID> result = new LinkedList<>();
        for (String robot: otherSeeableRobots){
            result.add(UUID.fromString(robot));
        }
        this.otherSeeableRobots = result;
    }
}
