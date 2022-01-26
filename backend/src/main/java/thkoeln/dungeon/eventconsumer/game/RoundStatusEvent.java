package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;
import thkoeln.dungeon.game.domain.round.RoundStatus;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoundStatusEvent extends AbstractEvent {
    private UUID roundId;
    private int roundNumber;
    private RoundStatus roundStatus;
}
