package thkoeln.dungeon.command;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommandExecutorService implements thkoeln.dungeon.command.CommandExecutor {

    @Override
    public UUID executeCommand(Command command) {
        return UUID.randomUUID();
    }

}
