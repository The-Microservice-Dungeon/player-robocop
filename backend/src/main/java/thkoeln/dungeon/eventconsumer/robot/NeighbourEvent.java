package thkoeln.dungeon.eventconsumer.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;

import javax.annotation.Generated;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "neighbours"
})
@Generated("jsonschema2pojo")
@Getter
@Entity
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.NONE)
public class NeighbourEvent extends AbstractEvent {

    @JsonProperty("neighbours")
    @ElementCollection
    private List<NeighbourData> neighbours = null;

    @JsonProperty("neighbours")
    public List<NeighbourData> getNeighbours() {
        return neighbours;
    }

    @JsonProperty("neighbours")
    public void setNeighbours(List<NeighbourData> neighbours) {
        this.neighbours = neighbours;
    }

}
