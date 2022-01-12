package thkoeln.dungeon.command;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("localKafka")
public class LocalKafkaCommandExecutor implements CommandExecutor {

    @Override
    public UUID executeCommand(Command command) {
        return UUID.randomUUID();
    }

}
