package thkoeln.dungeon.eventconsumer.trading;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.NONE)
public class BankCreatedEvent extends AbstractEvent {
    private UUID playerId;
    private Integer money;
}
