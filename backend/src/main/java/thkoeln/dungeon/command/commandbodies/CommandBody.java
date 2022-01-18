package thkoeln.dungeon.command.commandbodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


public abstract class CommandBody {
    private UUID robotId; // Id of own Robot

    // Bodies from https://the-microservice-dungeon.github.io/docs/openapi/robot/#tag/commands/paths/~1commands/post
}
