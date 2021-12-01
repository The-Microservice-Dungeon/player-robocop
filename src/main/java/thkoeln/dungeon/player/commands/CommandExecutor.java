package thkoeln.dungeon.player.commands;

import thkoeln.dungeon.player.commands.Command;

import java.util.UUID;

public interface CommandExecutor {
    public UUID executeCommand(Command command );
}
