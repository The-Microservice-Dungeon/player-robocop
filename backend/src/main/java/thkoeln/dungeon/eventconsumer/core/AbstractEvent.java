package thkoeln.dungeon.eventconsumer.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import thkoeln.dungeon.player.application.PlayerApplicationService;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEvent {
    public static final String TRANSACTION_ID_KEY = "transactionId";
    @Id
    @Setter(AccessLevel.NONE)
    protected UUID id = UUID.randomUUID();
    protected UUID eventId;
    protected Date timestamp;
    protected UUID transactionId;
    @Transient
    protected Logger logger = LoggerFactory.getLogger(AbstractEvent.class);

    public <EventType extends AbstractEvent> EventType fillHeader( String eventIdStr, String timestampStr, String transactionIdStr ) {
        try {
            setEventId(UUID.fromString(eventIdStr));
        } catch (IllegalArgumentException e) {
            logger.warn("Event " + eventId + " at time " + timestamp + " has invalid eventId.");
        }
        try {
            //At least that's what game sends as timestamp
            SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = dateTime.parse(timestampStr);
            setTimestamp(date);
        } catch (ParseException e) {
            logger.warn("Event " + eventId + " at time " + timestamp + " has invalid timestamp.");
        }
        try {
            setTransactionId(UUID.fromString(transactionIdStr));
        } catch (IllegalArgumentException e) {
            logger.warn("Event " + eventId + " at time " + timestamp + " doesn't have a valid transactionId " + transactionIdStr);
        }
        return (EventType) this;
    }

    public <EventType extends AbstractEvent> EventType fillWithPayload( String jsonString ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            return (EventType) objectMapper.readValue( jsonString, this.getClass() );
        }
        catch(JsonProcessingException conversionFailed ) {
            logger.error( "Error converting payload for event with jsonString " + jsonString );
            logger.error("Reason: "+ conversionFailed.getMessage());
            return (EventType) this;
        }
    }

}
