package thkoeln.dungeon.command

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile("localKafka")
class LocalKafkaCommandExecutor : CommandExecutor {
    override fun executeCommand(command: Command?): UUID? {
        return UUID.randomUUID()
    }
}