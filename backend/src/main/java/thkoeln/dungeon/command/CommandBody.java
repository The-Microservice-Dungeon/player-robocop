package thkoeln.dungeon.command;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandBody {
    @NotNull
    private CommandType commandType;
    private UUID planetId;
    private UUID targetId;
    private ItemType itemName;
    private Integer itemQuantity;
    // Bodies from https://the-microservice-dungeon.github.io/docs/openapi/robot/#tag/commands/paths/~1commands/post
}
