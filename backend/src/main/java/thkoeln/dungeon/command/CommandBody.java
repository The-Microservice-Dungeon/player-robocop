package thkoeln.dungeon.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommandBody {
    private CommandType commandType;
    private UUID robotId;
    private UUID targetId;
    private ItemType itemType;

    // Bodies from https://the-microservice-dungeon.github.io/docs/openapi/robot/#tag/commands/paths/~1commands/post
}
