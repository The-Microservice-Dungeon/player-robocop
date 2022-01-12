package thkoeln.dungeon.command;

import java.util.UUID;

public interface CommandExecutor {
    UUID executeCommand(Command command);
}
