package thkoeln.dungeon.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandDto {
    @NotNull
    private UUID gameId;
    @NotNull
    private UUID playerToken;
    private UUID robotId;
    @NotNull
    private CommandType commandType;
    @NotNull
    private CommandBody commandObject;
}
