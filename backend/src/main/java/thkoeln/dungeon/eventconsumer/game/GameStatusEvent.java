package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;
import thkoeln.dungeon.game.domain.game.GameStatus;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameStatusEvent extends AbstractEvent {
    private static final String TYPE_KEY = "type";
    private static final String GAME_ID_KEY = "gameId";
    private GameStatus status;
    private UUID gameId;

    public GameStatusEvent(String eventIdStr, String timestampStr, String transactionIdStr, String payloadString) {
        super(eventIdStr, timestampStr, transactionIdStr);
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            GameStatusEvent payload = objectMapper.readValue(payloadString, GameStatusEvent.class);
            setStatus(payload.getStatus());
            setGameId(payload.getGameId());
        } catch (JsonProcessingException conversionFailed) {
            logger.error("Error converting payload for event with payload: " + payloadString);
            logger.error("Conversion message: "+ conversionFailed.getMessage());
        }
    }

}
