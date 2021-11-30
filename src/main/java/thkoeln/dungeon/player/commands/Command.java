package thkoeln.dungeon.player.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import thkoeln.dungeon.player.robots.Robot;

/**
 * Should be abstract, but that conflicts with JPS
 */
@Getter
@AllArgsConstructor
public class Command {
    private CommandType commandType;
    private Robot robot;
}
