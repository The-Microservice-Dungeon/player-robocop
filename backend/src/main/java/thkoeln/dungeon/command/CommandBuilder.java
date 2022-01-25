package thkoeln.dungeon.command;

import com.sun.istack.NotNull;
import com.sun.xml.bind.v2.TODO;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.robot.domain.Robot;

public abstract class CommandBuilder {
    public static Command buildBuyRobotCommand(
            @NotNull Game game,
            @NotNull Player player,
            @NotNull Integer quantity
    ) {
        return new Command(game, player, CommandType.buying, null, null, ItemType.ROBOT, quantity);
    }

    public static Command buildBuyItemCommand(
            @NotNull Game game,
            @NotNull Player player,
            @NotNull Robot robot,
            @NotNull ItemType itemType,
            @NotNull Integer quantity
    ) {
        return new Command(game, player, CommandType.buying, robot, null, itemType, quantity);
    }

    public static Command buildRegenerationCommand(
            @NotNull Game game,
            @NotNull Player player,
            @NotNull Robot robot
    ) {
        return new Command(
                game, player, CommandType.regeneration, robot, null, null, null
        );
    }

    public static Command buildMovementCommand(
            @NotNull Game game,
            @NotNull Player player,
            @NotNull Robot robot,
            @NotNull Planet targetPlanet
    ) {
        return new Command(game, player, CommandType.movement, robot, targetPlanet, null,null);
    }

    public static Command buildMiningCommand(
            @NotNull Game game,
            @NotNull Player player,
            @NotNull Robot robot) {
        return new Command(game, player, CommandType.mining, robot, null, null, null);

    }

    public static Command buildSellingCommand(){
        //TODO not yet implemented
        return null;
    }

    public static Command buildFightingCommand(){
        //TODO not yet implemented
        return null;
    }
}
