package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import thkoeln.dungeon.game.domain.round.RoundStatus;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoundStatusEventPayload {
    @JsonProperty("roundId")
    private UUID roundId;
    @JsonProperty("roundNumber")
    private int roundNumber;
    @JsonProperty("roundStatus")
    private RoundStatus roundStatus;

    public static RoundStatusEventPayload fromJsonString(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        return objectMapper.readValue( jsonString, RoundStatusEventPayload.class );
    }

    public RoundStatusEventPayload (String uuidString, int roundNumber, String roundStatusString) {
        setRoundId(UUID.fromString(uuidString));
        setRoundNumber(roundNumber);
        setRoundStatus(RoundStatus.valueOf(roundStatusString));
    }
}
