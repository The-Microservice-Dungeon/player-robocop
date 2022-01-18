package thkoeln.dungeon.restadapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import thkoeln.dungeon.command.CommandType;
import thkoeln.dungeon.command.CommandBody;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandDto {
    private UUID gameId;
    private UUID playerToken;
    private UUID robotId;
    private CommandType commandType;
    private CommandBody commandObject;
}
