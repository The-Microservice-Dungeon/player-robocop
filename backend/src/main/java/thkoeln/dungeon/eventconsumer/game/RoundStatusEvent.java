package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;
import thkoeln.dungeon.game.domain.round.RoundStatus;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoundStatusEvent extends AbstractEvent {
    private UUID roundId;
    private int roundNumber;
    private RoundStatus roundStatus;

    public RoundStatusEvent(String eventIdStr, String timestampStr, String transactionIdStr, String payloadStr){
        super(eventIdStr, timestampStr, transactionIdStr);
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            RoundStatusEvent payload = objectMapper.readValue(payloadStr, RoundStatusEvent.class);
            setRoundStatus(payload.getRoundStatus());
            setRoundId(payload.getRoundId());
            setRoundNumber(payload.getRoundNumber());
        } catch (JsonProcessingException conversionFailed) {
            logger.error("Error converting payload for event with payload: " + payloadStr);
            logger.error("Conversion message: "+ conversionFailed.getMessage());
        }
    }
}
