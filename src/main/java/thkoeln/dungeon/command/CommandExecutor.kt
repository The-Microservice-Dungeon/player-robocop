package thkoeln.dungeon.command

import java.util.*

interface CommandExecutor {
    fun executeCommand(command: Command?): UUID?
}