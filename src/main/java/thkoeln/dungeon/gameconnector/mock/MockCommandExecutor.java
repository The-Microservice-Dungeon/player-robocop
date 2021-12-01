package thkoeln.dungeon.gameconnector.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.commands.CommandExecutor;
import thkoeln.dungeon.player.commands.Command;

import java.util.UUID;

@Component
@Profile( "mock" )
public class MockCommandExecutor implements CommandExecutor {

    @Override
    public UUID executeCommand( Command command ) {
        return UUID.randomUUID();
    }

}
