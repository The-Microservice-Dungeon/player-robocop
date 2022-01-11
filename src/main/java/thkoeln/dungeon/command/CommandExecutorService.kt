package thkoeln.dungeon.command

import org.springframework.stereotype.Component
import java.util.*

@Component
class CommandExecutorService : CommandExecutor {
    override fun executeCommand(command: Command?): UUID? {
        return UUID.randomUUID()
    }
}