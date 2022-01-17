package thkoeln.dungeon.command.commandbodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommandBody {
    private UUID robotId; // Id of own Robot

    // Bodies from https://the-microservice-dungeon.github.io/docs/openapi/robot/#tag/commands/paths/~1commands/post
}
