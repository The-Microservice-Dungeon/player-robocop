package thkoeln.dungeon.command.mock

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import thkoeln.dungeon.command.Command
import thkoeln.dungeon.command.CommandExecutor
import java.util.*

@Component
@Profile("mock")
class MockCommandExecutorService : CommandExecutor {
    override fun executeCommand(command: Command?): UUID? {
        return UUID.randomUUID()
    }
}