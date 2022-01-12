package thkoeln.dungeon.command.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.command.Command;
import thkoeln.dungeon.command.CommandExecutor;

import java.util.UUID;

@Component
@Profile("mock")
public class MockCommandExecutorService implements CommandExecutor {

    @Override
    public UUID executeCommand(Command command) {
        return UUID.randomUUID();
    }

}
